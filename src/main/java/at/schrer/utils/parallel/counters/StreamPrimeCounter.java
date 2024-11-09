package at.schrer.utils.parallel.counters;

import java.util.stream.LongStream;

public class StreamPrimeCounter extends PrimeCounter{
    @Override
    public String getDisplayName() {
        return "Stream";
    }

    @Override
    protected long applyInternal(long max) {
        return LongStream.rangeClosed(2, max)
                .filter(this::isPrime)
                .count();
    }
}
