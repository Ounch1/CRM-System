package com.yrgo.advice;

import org.aspectj.lang.ProceedingJoinPoint;

public class PerformanceTimingAdvice {
    String blue = "\u001B[34m";  // Blue text color
    String yellow = "\u001B[33m";  // Yellow text color
    String white = "\u001B[0m";  // Reset to default color

    public Object performTimingMeasurement(ProceedingJoinPoint method) throws Throwable {
        // before
        double startTime = System.nanoTime();
        try {
            // proceed to target
            Object value = method.proceed();
            return value;
        } finally {
            // after
            long endTime = System.nanoTime();
            double timeTaken = endTime - startTime;

            String className = method.getSignature().getDeclaringType().getSimpleName();

            // Format: blue for the message, yellow for the method name, and reset at the end
            System.out.printf(blue + "TIMER: Method " +
                    yellow + className + "." + method.getSignature().getName() + "()" + blue + " took " +
                    timeTaken / 1000000 + "ms\n" + white);
        }
    }

}
