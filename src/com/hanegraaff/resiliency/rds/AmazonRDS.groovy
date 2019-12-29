package com.hanegraaff.resiliency.rds

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.services.rds.AmazonRDSClientBuilder
import com.amazonaws.services.rds.model.DescribeDBClusterSnapshotsRequest
import com.amazonaws.services.rds.model.DescribeDBClusterSnapshotsResult
import com.hanegraaff.aws.AWSConfigurator
import com.hanegraaff.logging.Log


//@Grab('com.amazonaws:aws-java-sdk-rds:1.11.228')
class AmazonRDS {

    AWSCredentialsProvider creds
    AmazonRDSClient rdsClient

    /**
     * Constructor
     *
     * Prepares the AWS Client using the DefaultAWSCredentialsProviderChain described here:
     * https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
     *
     */
    AmazonRDS(){
       this.creds = AWSConfigurator.getCredentialsProvider()
       rdsClient = AmazonRDSClientBuilder
               .standard()
                    .withCredentials(this.creds)
                    .withRegion(Regions.US_EAST_1)
                    .build()
    }


    protected List getDBClusterSnapShotList(){
        DescribeDBClusterSnapshotsRequest request = new DescribeDBClusterSnapshotsRequest()
        DescribeDBClusterSnapshotsResult response

        def snapshotList = []

        def done = false
        def page = null

        Log.log("reading snapshot types")
        while (!done){
            request.withSnapshotType("manual")
            request.withMarker(page)

            response = rdsClient.describeDBClusterSnapshots()
            response.getDBClusterSnapshots().each(){
                snapshotList.add(it.getDBClusterSnapshotIdentifier())
                Log.log(it.getDBClusterSnapshotIdentifier())

            }

            page = response.getMarker()
            if (page == null) break
        }

        return snapshotList
    }


    String getLatestDBClusterSnapshot(String prefix){

        List snapshotList = getDBClusterSnapShotList()
        return ""
    }
}
