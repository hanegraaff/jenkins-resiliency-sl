package com.hanegraaff.jenkins

/**
 * Main (and only) implementation for the IStepExecutor interface.
 * When this class is constructed a step object will be supplied to it
 * via the constructor.
 */
class StepExecutor implements IStepExecutor {

    private _steps

    /**
     * constructor
     *
     * @param steps Step object with access to the underlining pipeline steps
     */
    StepExecutor(steps) {
        this._steps = steps
    }

    /**
     * See IStepExecutor::sh
     */
    @Override
    int sh(String command) {
        this._steps.sh returnStatus: true, script: "${command}"

        def returnVal = this._steps.sh(returnStdout: true, script: "${command}").trim()
        echo "sh return value is $returnVal"

        return -1
    }

    /**
     * See IStepExecutor::error
     */
    @Override
    void error(String message) {
        this._steps.error(message)
    }

    /**
     * See IStepExecutor::echo
     */
    @Override
    void echo(String message) {
        this._steps.echo(message)
    }
}