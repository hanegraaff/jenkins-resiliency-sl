package com.hanegraaff.resilency

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.AmazonRDSClient
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.resiliency.AmazonRDSResiliency
import org.junit.Before
import org.junit.Test

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Tests for the AmazonRDSResiliency class
 */
class TestAmazonRDSResiliency {
    @Before
    void setup(){

    }

    /**
     * Tests that when Amazon throws an exception that it's properly recast as an AWSException
     */
    @Test(expected = AWSException.class)
    void test_getLatestDBClusterSnapshot_WithAWSException(){
        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonRDSResiliency amazonRDS = new AmazonRDSResiliency(mockCreds)

        amazonRDS.initializeClient(Regions.US_EAST_1.name())

        amazonRDS.client = mock(AmazonRDSClient.class)

        when(amazonRDS.client.describeDBClusterSnapshots(any()))
                .thenThrow(com.amazonaws.SdkClientException.class)

        amazonRDS.getLatestDBClusterSnapshot("some_prefix")
    }

    /**
     * Tests that attempting to use an uninitialized instance of AmazonRDSResiliency
     * will result in an IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    void test_getLatestDBClusterSnapshot_NotInitialized1() {

        AmazonRDSResiliency amazonRDS = new AmazonRDSResiliency()

        //Not calling this means that it will not be initialized properly
        //amazonRDS.initializeClient(Regions.US_EAST_1.name())

        amazonRDS.getLatestDBClusterSnapshot("some_prefix")
    }


    /**
     * Tests that when a null credentials provider is supplied, an IllegalStateException is thrown
     */
    @Test(expected = IllegalStateException.class)
    void test_getLatestDBClusterSnapshot_NotInitialized2() {

        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)
        AmazonRDSResiliency amazonRDS = new AmazonRDSResiliency(mockCreds)

        //Not calling this means that it will not be initialized properly
        //amazonRDS.initializeClient(Regions.US_EAST_1.name())

        amazonRDS.getLatestDBClusterSnapshot("some_prefix")
    }

    /**
     * Tests that when an invalid region is supplied, an IllegalArgumentException is thrown
     */
    @Test(expected = IllegalArgumentException.class)
    void test_getLatestDBClusterSnapshot_UnsupportedRegion() {
        AWSCredentialsProvider mockCreds = mock(AWSCredentialsProvider.class)

        AmazonRDSResiliency amazonRDS = new AmazonRDSResiliency(mockCreds)
        amazonRDS.initializeClient("Invalid Region")
    }

    /**
     * Tests that when no snapshots are found an empty string is returned as the latest
     */
    @Test
    void test_getLatestDBClusterSnapshot_NoSnapshots() {

        AmazonRDSResiliency amazonRDS = mock(AmazonRDSResiliency.class)

        when(amazonRDS.getDBClusterSnapShotList()).thenReturn([])
        when(amazonRDS.getLatestDBClusterSnapshot(anyString())).thenCallRealMethod()

        def latestSnapshot = amazonRDS.getLatestDBClusterSnapshot("some_prefix")

        assert latestSnapshot == ""
    }

    /**
     * Tests that when three snapshots are found, the latest one is returned
     */
    @Test
    void test_getLatestDBClusterSnapshot_WithThreeSnapshots() {

        AmazonRDSResiliency amazonRDS = mock(AmazonRDSResiliency.class)

        when(amazonRDS.getLatestDBClusterSnapshot(anyString())).thenCallRealMethod()
        when(amazonRDS.getDBClusterSnapShotList()).thenReturn(['snapshot_abc', 'snapshot_xyz', 'snapshot_123'])

        def latestSnapshot = amazonRDS.getLatestDBClusterSnapshot("snapshot")

        println("latest snapshot is $latestSnapshot")

        assert latestSnapshot == "snapshot_xyz"
    }

    /**
     * Tests that when one snapshot is found, the same one one is returned
     */
    @Test
    void test_getLatestDBClusterSnapshot_WithOneSnapshots() {

        AmazonRDSResiliency amazonRDS = mock(AmazonRDSResiliency.class)

        when(amazonRDS.getLatestDBClusterSnapshot(anyString())).thenCallRealMethod()
        when(amazonRDS.getDBClusterSnapShotList()).thenReturn(['snapshot_abc'])

        def latestSnapshot = amazonRDS.getLatestDBClusterSnapshot("snapshot")

        println("latest snapshot is $latestSnapshot")

        assert latestSnapshot == "snapshot_abc"
    }
}