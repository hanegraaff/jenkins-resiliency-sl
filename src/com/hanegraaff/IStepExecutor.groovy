package com.hanegraaff

interface IStepExecutor {
    int sh(String command)
    void error(String message)
    // add more methods for respective steps if needed
}