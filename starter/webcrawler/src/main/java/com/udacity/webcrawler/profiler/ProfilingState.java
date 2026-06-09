package com.udacity.webcrawler.profiler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class that records method performance data from the method interceptor.
 */
final class ProfilingState {
    // Aggregated totals keyed by "ClassName#method" — for the summary line
    private final Map<String, Duration> totalData = new ConcurrentHashMap<>();
    private final Map<String, Long> totalCallCounts = new ConcurrentHashMap<>();

    // Per-thread breakdown keyed by "ClassName#method [thread=N]" — for the detail lines
    private final Map<String, Duration> threadData = new ConcurrentHashMap<>();
    private final Map<String, Long> threadCallCounts = new ConcurrentHashMap<>();

    /**
     * Records the given method invocation data.
     *
     * @param callingClass the Java class of the object that called the method.
     * @param method       the method that was called.
     * @param elapsed      the amount of time that passed while the method was called.
     * @param threadId     the thread ID
     */
    void record(Class<?> callingClass, Method method, Duration elapsed, long threadId) {
        Objects.requireNonNull(callingClass);
        Objects.requireNonNull(method);
        Objects.requireNonNull(elapsed);
        if (elapsed.isNegative()) {
            throw new IllegalArgumentException("negative elapsed time");
        }

        String methodKey = formatMethodCall(callingClass, method);
        String threadKey = methodKey + " [thread=" + threadId + "]";

        // Update aggregated totals
        totalData.merge(methodKey, elapsed, Duration::plus);
        totalCallCounts.merge(methodKey, 1L, Long::sum);

        // Update per-thread breakdown
        threadData.merge(threadKey, elapsed, Duration::plus);
        threadCallCounts.merge(threadKey, 1L, Long::sum);
    }

    /**
     * Writes the method invocation data to the given {@link Writer}.
     *
     * <p>Recorded data is aggregated across calls to the same method. For example, suppose
     * {@link #record(Class, Method, Duration, long) record} is called three times for the same method
     * {@code M()}, with each invocation taking 1 second. The total {@link Duration} reported by
     * this {@code write()} method for {@code M()} should be 3 seconds.
     */
    void write(Writer writer) throws IOException {
        // Get all unique method keys (without thread suffix)
        List<String> methodKeys = totalData.keySet().stream().sorted().toList();

        for (String methodKey : methodKeys) {
            // Write the aggregated summary line
            Duration total = totalData.get(methodKey);
            long calls = totalCallCounts.getOrDefault(methodKey, 0L);
            writer.write(String.format("%s took %s over %d call(s) (all threads)%n",
                    methodKey, formatDuration(total), calls));

            // Write per-thread breakdown, indented
            threadData.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(methodKey + " [thread="))
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        try {
                            long threadCalls = threadCallCounts.getOrDefault(e.getKey(), 0L);
                            writer.write(String.format("    %s took %s over %d call(s)%n",
                                    e.getKey(), formatDuration(e.getValue()), threadCalls));
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    });
        }
    }

    /**
     * Formats the given method call for writing to a text file.
     *
     * @param callingClass the Java class of the object whose method was invoked.
     * @param method       the Java method that was invoked.
     * @return a string representation of the method call.
     */
    private static String formatMethodCall(Class<?> callingClass, Method method) {
        return String.format("%s#%s", callingClass.getName(), method.getName());
    }

    /**
     * Formats the given {@link Duration} for writing to a text file.
     */
    private static String formatDuration(Duration duration) {
        return String.format(
                "%sm %ss %sms", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart());
    }
}
