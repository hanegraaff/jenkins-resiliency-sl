package com.hanegraaff.ioc

import com.hanegraaff.IStepExecutor

interface IContext {
    IStepExecutor getStepExecutor()
}