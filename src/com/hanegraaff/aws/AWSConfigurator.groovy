package com.hanegraaff.aws

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

class AWSConfigurator {

    static AWSCredentialsProvider getCredentialsProvider(){
        return new DefaultAWSCredentialsProviderChain()
    }
}
