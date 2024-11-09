package at.schrer.utils.parallel;

import at.schrer.utils.parallel.counters.PrimeCounter;
import at.schrer.utils.parallel.counters.VirtualThreadsPrimeCounter;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * This is a bit of performance playground.
 * The results are not accurate, as the JIT kicks in some cases and has then "prepared" functions for following test cases.
 */
public class PrimePerformance {
    private static final long THRESHOLD = 10000000;

    public static void main(String[] args) {
        var results = Stream.of(
                simpleLoopPrimeCounter(),
                streamPrimeCounter(),
                parallelStreamPrimeCounter(),
                new VirtualThreadsPrimeCounter(1),
                new VirtualThreadsPrimeCounter(2),
                new VirtualThreadsPrimeCounter(4),
                new VirtualThreadsPrimeCounter(8),
                new VirtualThreadsPrimeCounter(16),
                new VirtualThreadsPrimeCounter(20),
                new VirtualThreadsPrimeCounter(24)
        ).map(it -> it.apply(THRESHOLD)).toList();

        printResults(results);
    }

    public static PrimeCounter simpleLoopPrimeCounter(){
        return new PrimeCounter() {
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
        };
    }

    public static PrimeCounter streamPrimeCounter() {
        return new PrimeCounter() {
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
        };
    }

    public static PrimeCounter parallelStreamPrimeCounter(){
        return new PrimeCounter() {
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
        };
    }

    private static void printResults(List<PrimeCounter.PerfResult> resultList){
        // Define the column widths
        int nameWidth = 15;
        int durationWidth = 15;
        int countWidth = 8;

        // Print table header
        String headerFormat = "%-" + nameWidth + "s %" + durationWidth + "s %" + countWidth + "s%n";
        System.out.printf(headerFormat, "Name", "Duration", "Count");

        // Print a separator line
        System.out.println("-".repeat(nameWidth + durationWidth + countWidth + 2));

        String lineFormat = "%-" + nameWidth + "s %" + durationWidth + "d %" + countWidth + "s%n";
        for (PrimeCounter.PerfResult result : resultList) {
            System.out.printf(lineFormat, result.counterName(), result.durationNano(), result.count());
        }
    }
}
