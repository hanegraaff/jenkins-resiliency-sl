package com.hanegraaff

/**
 * Interface for all pipeline steps used by any class in this shared library
 * Pipeline steps are primarily used by global variable scripts, but if they
 * are used by any of the supporting classes then they can be mocked.
 */

interface IStepExecutor {
    /**
     * Wrapper for pipeline "sh" step
     *
     * @param command Command to be executed in shell
     * @return int indicating the return value.
     */
    int sh(String command)

    /**
     * Wrapper for pipeline "error" step
     *
     * @param message Error message
     */
    void error(String message)

    /**
     * Wrapper for pipeline "echo" step
     *
     * @param message String to print to the Jenkins console
     */
    void echo(String message)
}