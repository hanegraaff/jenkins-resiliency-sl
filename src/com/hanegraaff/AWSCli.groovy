package com.hanegraaff

class AWSCli {

    public IStepExecutor _stepExec

    public AWSCli(IStepExecutor stepExec){
        this._stepExec = stepExec
    }

    def invoke(cmd){
        return this._stepExec.sh(cmd)
    }


}