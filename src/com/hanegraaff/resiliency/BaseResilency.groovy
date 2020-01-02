package com.hanegraaff.resiliency

import com.amazonaws.auth.AWSCredentialsProvider

/**
 * Base class for resiliency classes. It forces subclasses to be initialized in two steps
 * to overcome the CPS limitation in Jenkins. Specifically they must be initialized
 * like this:
 *
 * 1) By calling the constructor and optionally passing it an external credentials provider
 * 2) By calling the "initializeClient()" method and optionally passing it a region name
 *
 * Implementing classes are responsible for setting the builder object
 * with an appropriate service specific implementation
 */
abstract class BaseResilency {
    protected AWSCredentialsProvider creds
    protected def initialized

    /**
     * Constructor
     *
     * Will use a default credentials provider
     */
    BaseResilency(){
        this.initialized = false
    }

    /**
     * Constructor
     *
     * Will use an external credentials provider
     * typically using an assume role operation
     *
     * @param credProvider The external credentials providers
     */
    BaseResilency(AWSCredentialsProvider credProvider){
        this.creds = credProvider
        this.initialized = false
    }

    /**
     * Initializes this class post construction
     *
     * This is being kept outside the default constructor so that there are no CPS issues
     * when running this code inside a pipeline. For more information see this:
     *
     * https://wiki.jenkins.io/display/JENKINS/Pipeline+CPS+method+mismatches
     *
     * @param regionName The optional name of the region to use. If null, then a default one will be used
     */
    abstract void initializeClient(String regionName)

    /**
     * Throws an IllegalStateException if not initialized. Typically included at the beginning
     * of a method that uses the AWS SDK
     */
    void throwIfNotInitialized(){
        if (!initialized)
            throw new IllegalStateException("You must initialize AmazonEC2Resiliency with a valid Credentials provider")
    }
}
