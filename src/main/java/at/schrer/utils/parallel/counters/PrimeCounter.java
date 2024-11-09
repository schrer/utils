package at.schrer.utils.parallel.counters;

import java.util.function.LongFunction;

public abstract class PrimeCounter implements LongFunction<PrimeCounter.PerfResult> {

    public abstract String getDisplayName();

    @Override
    public PerfResult apply(long max) {
        long startTime = System.nanoTime();
        long resultCount = applyInternal(max);
        long duration = (System.nanoTime() - startTime);
        return new PrimeCounter.PerfResult(this.getDisplayName(), duration, resultCount);
    }

    protected abstract long applyInternal(long max);

    protected boolean isPrime(long number){
        if (number == 2) {
            return true;
        }
        if (number == 1 || number % 2 == 0) {
            return false;
        }

        long i = 3;

        while(i <= (long)Math.sqrt(number)) {
            if(number % i == 0) {
                return false;
            }
            i = i+2;
        }
        return true;
    }

    public record PerfResult(String counterName, long durationNano, long count){}
}
