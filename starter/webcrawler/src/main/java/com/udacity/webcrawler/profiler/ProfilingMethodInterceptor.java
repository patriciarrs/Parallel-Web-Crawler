package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

    private final Clock clock;
    private final Object delegate;
    private final ProfilingState profilingState;

    // TODO: You will need to add more instance fields and constructor arguments to this class.
    ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState profilingState) {
        this.clock = Objects.requireNonNull(clock);
        this.delegate = Objects.requireNonNull(delegate);
        this.profilingState = Objects.requireNonNull(profilingState);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Rubric: bypass equals on Object.class to avoid proxy interference
        if (method.getDeclaringClass().equals(Object.class) && method.getName().equals("equals")) {
            return delegate.equals(args[0]);
        }

        if (method.getAnnotation(Profiled.class) == null) {
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Instant start = clock.instant();
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            profilingState.record(delegate.getClass(), method,
                    Duration.between(start, clock.instant()), Thread.currentThread().getId());
        }
    }
}
