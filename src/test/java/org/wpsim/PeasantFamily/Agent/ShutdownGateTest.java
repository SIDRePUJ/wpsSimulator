package org.wpsim.PeasantFamily.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Dependency-free boundary test; compile with ShutdownGate and run with java -ea. */
public final class ShutdownGateTest {
    public static void main(String[] args) throws Exception {
        repeatedAndConcurrentCalls();
        notificationFailureCanRetry();
        killFailureCanRetryWithoutRenotifying();
        reentrantNotificationDoesNotRepeat();
        reentrantKillDoesNotRepeat();
        System.out.println("ShutdownGateTest PASS");
    }

    private static void repeatedAndConcurrentCalls() throws Exception {
        ShutdownGate gate = new ShutdownGate();
        AtomicInteger notifications = new AtomicInteger();
        AtomicInteger kills = new AtomicInteger();
        CountDownLatch ready = new CountDownLatch(8);
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Thread thread = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
                } catch (Exception e) {
                    failure.compareAndSet(null, e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        if (!ready.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("Shutdown workers did not start");
        }
        start.countDown();
        for (Thread thread : threads) {
            thread.join(5000);
            if (thread.isAlive()) {
                throw new AssertionError("Concurrent shutdown deadlocked");
            }
        }
        if (failure.get() != null) {
            throw new AssertionError("Concurrent shutdown failed", failure.get());
        }
        gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        equal(1, notifications.get());
        equal(1, kills.get());
    }

    private static void notificationFailureCanRetry() throws Exception {
        ShutdownGate gate = new ShutdownGate();
        AtomicInteger notifications = new AtomicInteger();
        AtomicInteger kills = new AtomicInteger();
        try {
            gate.execute(() -> {
                notifications.incrementAndGet();
                throw new Exception("send failed");
            }, kills::incrementAndGet);
            throw new AssertionError("Notification failure was swallowed");
        } catch (Exception expected) {
            if (!"send failed".equals(expected.getMessage())) {
                throw expected;
            }
        }
        equal(0, kills.get());
        gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        equal(2, notifications.get());
        equal(1, kills.get());
    }

    private static void killFailureCanRetryWithoutRenotifying() throws Exception {
        ShutdownGate gate = new ShutdownGate();
        AtomicInteger notifications = new AtomicInteger();
        AtomicInteger kills = new AtomicInteger();
        try {
            gate.execute(notifications::incrementAndGet, () -> {
                kills.incrementAndGet();
                throw new Exception("kill failed");
            });
            throw new AssertionError("Kill failure was swallowed");
        } catch (Exception expected) {
            if (!"kill failed".equals(expected.getMessage())) {
                throw expected;
            }
        }
        gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        equal(1, notifications.get());
        equal(2, kills.get());
        gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        equal(1, notifications.get());
        equal(2, kills.get());
    }

    private static void reentrantKillDoesNotRepeat() throws Exception {
        ShutdownGate gate = new ShutdownGate();
        AtomicInteger notifications = new AtomicInteger();
        AtomicInteger kills = new AtomicInteger();
        gate.execute(notifications::incrementAndGet, () -> {
            kills.incrementAndGet();
            gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        });
        equal(1, notifications.get());
        equal(1, kills.get());
    }

    private static void reentrantNotificationDoesNotRepeat() throws Exception {
        ShutdownGate gate = new ShutdownGate();
        AtomicInteger notifications = new AtomicInteger();
        AtomicInteger kills = new AtomicInteger();
        gate.execute(() -> {
            notifications.incrementAndGet();
            gate.execute(notifications::incrementAndGet, kills::incrementAndGet);
        }, kills::incrementAndGet);
        equal(1, notifications.get());
        equal(1, kills.get());
    }

    private static void equal(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
