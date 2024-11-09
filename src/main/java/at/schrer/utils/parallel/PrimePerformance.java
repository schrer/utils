package at.schrer.utils.parallel;

import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * This is a bit of performance playground.
 * The results are not accurate, as the JIT kicks in some cases and has then "prepared" functions for following test cases.
 */
public class PrimePerformance {
    public static void main(String[] args) {
        final long max = 10000000;

        var results = Stream.of(
                new PerfCase("Simple loop", PrimePerformance::simpleLoopPrimeCounter),
                new PerfCase("Stream", PrimePerformance::streamPrimeCounter),
                new PerfCase("Parallel stream", PrimePerformance::parallelStreamPrimeCounter),
                new PerfCase("VThreads x1", new VirtualThreadsPrimeCounter(1)),
                new PerfCase("VThreads x2", new VirtualThreadsPrimeCounter(2)),
                new PerfCase("VThreads x4", new VirtualThreadsPrimeCounter(4)),
                new PerfCase("VThreads x8", new VirtualThreadsPrimeCounter(8)),
                new PerfCase("VThreads x16", new VirtualThreadsPrimeCounter(16)),
                new PerfCase("VThreads x20", new VirtualThreadsPrimeCounter(20)),
                new PerfCase("VThreads x24", new VirtualThreadsPrimeCounter(24))
        ).map(it -> runCase(it, max)).toList();

        println(results);
    }

    private static PerfResult runCase(PerfCase perfCase, long upperBound){
        long startTime = System.nanoTime();
        long resultCount = perfCase.counterFunction().applyAsLong(upperBound);
        long duration = (System.nanoTime() - startTime);
        return new PerfResult(perfCase.name(), duration, resultCount);
    }

    public static Long simpleLoopPrimeCounter(Long max){
        return simpleLoopPrimeCounter(1, max+1);
    }

    public static Long simpleLoopPrimeCounter(long start, long endExclusive){
        long primeCountLoop = 0;
        for (long i = start; i < endExclusive; i++) {
            if (isPrime(i)) {
                primeCountLoop++;
            }
        }
        return primeCountLoop;
    }

    public static Long streamPrimeCounter(Long max) {
        return LongStream.rangeClosed(1, max).filter(PrimePerformance::isPrime).count();
    }

    public static Long parallelStreamPrimeCounter(Long max){
        return LongStream.rangeClosed(1, max).parallel().filter(PrimePerformance::isPrime).count();
    }


    public static boolean isPrime(long number){
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

    private static void println(List<PerfResult> resultList){
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
        for (PerfResult result : resultList) {
            System.out.printf(lineFormat, result.name(), result.durationNano(), result.count());

        }
    }

    private record PerfResult(String name, long durationNano, long count){}
    private record PerfCase(String name, LongUnaryOperator counterFunction){}
}
