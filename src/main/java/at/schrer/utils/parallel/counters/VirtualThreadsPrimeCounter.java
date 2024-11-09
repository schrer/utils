package at.schrer.utils.parallel.counters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadsPrimeCounter extends PrimeCounter {

    private final int numThreads;

    public VirtualThreadsPrimeCounter(int numThreads) {
        this.numThreads = numThreads;
    }

    @Override
    public long applyInternal(long max) {
        long partitionSize = max/numThreads;
        List<Section> sections = new ArrayList<>(numThreads);
        for (int i = 0; i<numThreads; i++) {
            long start = partitionSize*i+1;
            long end = i==(numThreads-1)? max+1 : partitionSize*(i+1)+1;
            sections.add(new Section(start, end));
        }

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Long>> futures = new ArrayList<>(numThreads);
            for (Section section : sections) {
                futures.add(executor.submit(
                        () -> simpleLoopPrimeCounter(section.start(), section.end())
                ));
            }
            long primeCount = 0;
            for (Future<Long> future : futures) {
                primeCount += future.get();
            }
            return primeCount;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private long simpleLoopPrimeCounter(long start, long endExclusive){
        long primeCountLoop = 0;
        for (long i = start; i < endExclusive; i++) {
            if (isPrime(i)) {
                primeCountLoop++;
            }
        }
        return primeCountLoop;
    }

    @Override
    public String getDisplayName() {
        return "VThreads x" + numThreads;
    }

    private record Section(long start, long end){}
}
