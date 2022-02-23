package com.reading.start.tests;

/**
 * Represent test result of module.
 */
public interface ITestModuleResult {
    int getIndex();

    /**
     * Return test duration.
     */
    long getTestTime();

    /**
     * Return string that represent short info about test result.
     */
    String getInfo();

    /**
     * Return info whether test was interrupted.
     */
    boolean isInterrupted();
}
