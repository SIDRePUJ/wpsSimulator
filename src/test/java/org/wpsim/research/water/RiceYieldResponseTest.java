package org.wpsim.research.water;

/** Dependency-free equation and unit tests; run with java -ea after javac. */
public final class RiceYieldResponseTest {
    public static void main(String[] args) {
        RiceYieldResponse.RiceOutcome full = RiceYieldResponse.estimate(6.0, 500, 500, 1.1, 2);
        close(6.0, full.tonnesPerHa());
        close(12.0, full.tonnes());
        close(1.0, full.relativeYield());

        RiceYieldResponse.RiceOutcome deficit = RiceYieldResponse.estimate(6.0, 250, 500, 1.1, 2);
        close(2.7, deficit.tonnesPerHa());
        close(5.4, deficit.tonnes());
        close(0.45, deficit.relativeYield());
        RiceYieldResponse.RiceOutcome failure = RiceYieldResponse.estimate(6.0, 0, 500, 1.1, 2);
        close(0.0, failure.tonnesPerHa());
        expectFailure(() -> RiceYieldResponse.estimate(6, 600, 500, 1.1, 1));
        expectFailure(() -> RiceYieldResponse.estimate(6, 250, 0, 1.1, 1));
        System.out.println("RiceYieldResponseTest PASS");
    }

    private static void close(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-8) {
            throw new AssertionError("expected " + expected + ", actual " + actual);
        }
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected IllegalArgumentException");
    }
}
