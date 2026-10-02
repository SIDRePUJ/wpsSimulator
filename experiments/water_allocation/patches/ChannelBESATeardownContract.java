package BESA.Kernel.Agent;

import BESA.Kernel.Agent.Event.EventBESA;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Focused contract for the exact-source ChannelBESA teardown patch. */
public final class ChannelBESATeardownContract {
    private ChannelBESATeardownContract() {}

    private static final class TestGuard extends GuardBESA {
        TestGuard(String type) { evType = type; }
        @Override public void funcExecGuard(EventBESA event) {}
        @Override public void finalize() {}
    }

    private static ChannelBESA channelWithPorts(int count) {
        ChannelBESA channel = new ChannelBESA(null);
        for (int i = 0; i < count; i++) {
            if (channel.addPort(new TestGuard("test-" + i)) == null) {
                throw new AssertionError("Failed to add distinct test port " + i);
            }
        }
        return channel;
    }

    private static void deterministicPurge() {
        ChannelBESA channel = channelWithPorts(8);
        channel.purgePorts(null);
        if (!channel.getPorts().isEmpty()) {
            throw new AssertionError("Purge left " + channel.getPorts().size() + " ports");
        }
    }

    private static void synchronizedMethods() throws Exception {
        for (Method method : ChannelBESA.class.getDeclaredMethods()) {
            if (method.getName().equals("findPort") || method.getName().equals("addPort")
                    || method.getName().equals("removePort") || method.getName().equals("purgePorts")) {
                if (!Modifier.isSynchronized(method.getModifiers())) {
                    throw new AssertionError("Unsynchronized shared-port method: " + method);
                }
            }
        }
    }

    private static void concurrentPurge() throws Exception {
        for (int round = 0; round < 100; round++) {
            ChannelBESA channel = channelWithPorts(8);
            CountDownLatch start = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();
            Thread[] threads = new Thread[3];
            for (int i = 0; i < threads.length; i++) {
                final boolean reader = i == 2;
                threads[i] = new Thread(() -> {
                    try {
                        if (!start.await(5, TimeUnit.SECONDS)) {
                            throw new AssertionError("Start barrier timed out");
                        }
                        if (reader) {
                            for (int j = 0; j < 16; j++) {
                                channel.findPort("test-" + (j % 8));
                            }
                        } else {
                            channel.purgePorts(null);
                        }
                    } catch (Throwable error) {
                        failure.compareAndSet(null, error);
                    }
                }, "channel-contract-" + i);
                threads[i].start();
            }
            start.countDown();
            for (Thread thread : threads) {
                thread.join(5000);
                if (thread.isAlive()) {
                    throw new AssertionError("Purge thread failed to terminate");
                }
            }
            if (failure.get() != null) {
                throw new AssertionError("Concurrent purge failed", failure.get());
            }
            if (!channel.getPorts().isEmpty()) {
                throw new AssertionError("Concurrent purge left ports in round " + round);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        deterministicPurge();
        if (args.length == 1 && args[0].equals("--full")) {
            synchronizedMethods();
            concurrentPurge();
        }
        System.out.println("ChannelBESA teardown contract passed");
    }
}
