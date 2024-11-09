package at.schrer.utils.parallel.counters;

public class SimpleLoopPrimeCounter extends PrimeCounter{
    @Override
    public String getDisplayName() {
        return "Simple loop";
    }

    @Override
    protected long applyInternal(long max) {
        long primeCountLoop = 0;
        for (long i = 2; i <= max; i++) {
            if (isPrime(i)) {
                primeCountLoop++;
            }
        }
        return primeCountLoop;
    }
}
