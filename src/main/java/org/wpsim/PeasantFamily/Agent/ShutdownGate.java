package org.wpsim.PeasantFamily.Agent;

/** Serializes the family death notification and its subsequent self-kill attempt. */
final class ShutdownGate {
    @FunctionalInterface
    interface Action {
        void run() throws Exception;
    }

    private boolean notified;
    private boolean notifying;
    private boolean killed;
    private boolean killing;

    synchronized void execute(Action notifyControl, Action killSelf) throws Exception {
        if (!notified) {
            if (notifying) {
                return;
            }
            notifying = true;
            try {
                notifyControl.run();
                notified = true;
            } finally {
                notifying = false;
            }
        }
        if (!killed && !killing) {
            // Reentrant shutdown must not retry while BESA is still processing this kill.
            killing = true;
            try {
                killSelf.run();
                killed = true;
            } finally {
                killing = false;
            }
        }
    }
}
