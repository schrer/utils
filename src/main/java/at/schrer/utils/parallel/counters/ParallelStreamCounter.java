package at.schrer.utils.parallel.counters;

import java.util.stream.LongStream;

public class ParallelStreamCounter extends PrimeCounter{
    @Override
    public String getDisplayName() {
        return "Parallel stream";
    }

    @Override
    protected long applyInternal(long max) {
        return LongStream.rangeClosed(2, max)
                .parallel()
                .filter(this::isPrime)
                .count();
    }
}
