package at.schrer.utils.parallel;

import at.schrer.utils.parallel.counters.*;

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
                new SimpleLoopPrimeCounter(),
                new StreamPrimeCounter(),
                new ParallelStreamCounter(),
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
