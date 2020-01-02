package com.hanegraaff.resiliency

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.StartInstancesRequest
import com.amazonaws.services.ec2.model.StartInstancesResult
import com.amazonaws.services.ec2.model.StopInstancesRequest
import com.amazonaws.services.ec2.model.StopInstancesResult
import com.hanegraaff.aws.AWSConfigurator
import com.hanegraaff.exceptions.AWSException

/**
 * This class acts a wrapper for any EC2 resiliency function. It must be initialized in two steps:
 *
 * 1) By calling the constructor and optionally passing it an external credentials provider
 * 2) By calling the "initializeClient()" method and optionally passing it a region name
 *
 * This is done to work around Jenkins CPS limitation when writing constructors
 *
 * The builder is set
 *
 * Example:
 *      AWSCredentialsProvider creds = ...
 *      AmazonEC2Resiliency amazonEC2 = new AmazonRDSResiliency(creds)
 *      amazonEC2.initializeClient(Regions.US_EAST_1.name())
 *
 *      amazonEC2.getLatestDBClusterSnapshot("some_prefix")
 */
class AmazonEC2Resiliency extends BaseResilency{

    protected AmazonEC2 client

    /**
     *  See class documentation - Do not call any library methods from here
     *
     * @param credProvider
     */
    AmazonEC2Resiliency(AWSCredentialsProvider credProvider){
        super(credProvider)
    }

    /**
     * Override the client by setting the proper builder
     *
     * @param regionName
     */
    @Override
    void initializeClient(String regionName){
        if (initialized)
            return

        if (creds == null)
            this.creds = AWSConfigurator.getCredentialsProvider()

        Regions region =  (regionName != null)?AWSConfigurator.fromString(regionName):AWSConfigurator.getCurrentRegion()

        client = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(this.creds)
                .withRegion(region)
                .build()

        this.initialized = true
    }

    /**
     * Turns off n EC2 instance, or throws an AWS Exception is case of any errors
     *
     * @param instanceID String containing the InstanceID name
     * @return String with the new EC2 state (e.g. stopping)
     */
    String stopEC2Instance(String instanceID) {
        throwIfNotInitialized()

        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceID)
        StopInstancesResult results

        try {
            results = client.stopInstances(request)
        }
        catch (Exception e) {
            throw new AWSException("Could not stop EC2 instance", e)
        }

        return results.getStoppingInstances().first()?.getCurrentState()?.getName()
    }

    /**
     * Turns on n EC2 instance, or throws an AWS Exception is case of any errors
     *
     * @param instanceID String containing the InstanceID name
     * @return String with the new EC2 state (e.g. starting)
     */
    String startEC2Instance(String instanceID){
        throwIfNotInitialized()

        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceID)
        StartInstancesResult results

        try {
            results = client.startInstances(request)
        }
        catch (Exception e) {
            throw new AWSException("Could not start EC2 instance", e)
        }

        return results.getStartingInstances().first()?.getCurrentState()?.getName()
    }
}
