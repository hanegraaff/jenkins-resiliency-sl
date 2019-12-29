package com.hanegraaff

import com.hanegraaff.jenkins.IStepExecutor

class AWSCli {

    public IStepExecutor _stepExec

    public AWSCli(IStepExecutor stepExec){
        this._stepExec = stepExec
    }

    int invoke(cmd){
        return this._stepExec.sh(cmd)
    }
}
