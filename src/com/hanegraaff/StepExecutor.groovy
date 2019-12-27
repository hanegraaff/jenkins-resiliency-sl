package com.hanegraaff

class StepExecutor implements IStepExecutor {
    // this will be provided by the vars script and
    // let's us access Jenkins steps
    private _steps

    StepExecutor(steps) {
        this._steps = steps
    }

    @Override
    int sh(String command) {
        this._steps.sh returnStatus: true, script: "${command}"

        returnVal = this._steps.sh(returnStdout: true, script: "${command}").trim()
        echo "sh return value is $returnVal"

        return returnVal
    }


    @Override
    void error(String message) {
        this._steps.error(message)
    }
}