package com.hanegraaff.resilency

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2Client
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.resiliency.AmazonEC2Resiliency
import org.junit.Before
import org.junit.Test

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when


class TestAmazonEC2Resiliency {

    @Before
    void setup(){

    }

    /**
     * Tests that when Amazon throws an exception that it's properly recast as an AWSException
     */
    @Test(expected = AWSException.class)
    void test_stopEC2Instance_WithAWSException(){
        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency(mockCreds)

        amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.client = mock(AmazonEC2Client.class)

        when(amazonEC2.client.stopInstances(any()))
                .thenThrow(com.amazonaws.SdkClientException.class)

        amazonEC2.stopEC2Instance("some_instance")
    }

    /**
     * Tests that when Amazon throws an exception that it's properly recast as an AWSException
     */
    @Test(expected = AWSException.class)
    void test_turnOnEC2Instance_WithAWSException(){
        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency(mockCreds)

        amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.client = mock(AmazonEC2Client.class)

        when(amazonEC2.client.startInstances(any()))
                .thenThrow(com.amazonaws.SdkClientException.class)

        amazonEC2.startEC2Instance("some_instance")
    }

    /**
     * Tests that attempting to use an uninitialized instance of AmazonEC2Resiliency
     * will result in an IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    void test_stopEC2Instance_NotInitialized1() {

        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency()

        //Not calling this means that it will not be initialized properly
        //amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.stopEC2Instance("some_instance")
    }

    /**
     * Tests that attempting to use an uninitialized instance of AmazonEC2Resiliency
     * will result in an IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    void test_turnOnEC2Instance_NotInitialized1() {

        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency()

        //Not calling this means that it will not be initialized properly
        //amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.startEC2Instance("some_instance")
    }


    /**
     * Tests that when a null credentials provider is supplied, an IllegalStateException is thrown
     */
    @Test(expected = IllegalStateException.class)
    void test_stopEC2Instance_NotInitialized2() {

        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency(mockCreds)

        //Not calling this means that it will not be initialized properly
        //amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.stopEC2Instance("some_instance")
    }

    /**
     * Tests that when a null credentials provider is supplied, an IllegalStateException is thrown
     */
    @Test(expected = IllegalStateException.class)
    void test_startEC2Instance_NotInitialized2() {

        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency(mockCreds)

        //Not calling this means that it will not be initialized properly
        //amazonEC2.initializeClient(Regions.US_EAST_1.name())

        amazonEC2.startEC2Instance("some_instance")
    }

    /**
     * Tests that when an invalid region is supplied, an IllegalArgumentException is thrown
     */
    @Test(expected = IllegalArgumentException.class)
    void test_invoke_UnsupportedRegion() {
        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)

        AmazonEC2Resiliency amazonEC2 = new AmazonEC2Resiliency(mockCreds)
        amazonEC2.initializeClient("Invalid Region")
    }
}
