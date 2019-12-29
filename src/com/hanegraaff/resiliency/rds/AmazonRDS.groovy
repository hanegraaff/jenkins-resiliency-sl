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


class AmazonRDS {

    AWSCredentialsProvider creds
    AmazonRDSClient rdsClient

    def initilized

    /**
     * Constructor
     */
    AmazonRDS(){
        initilized = false;
    }


    /**
     * Initializes the AWS constructs used by this class
     * Note that this is being kept outside the constructor so that there are no CPS issues
     * when running this code inside a pipeline. For more information see this:
     *
     * https://wiki.jenkins.io/display/JENKINS/Pipeline+CPS+method+mismatches
     *
     */
    protected void init(){
        if (!initilized){
            this.creds = AWSConfigurator.getCredentialsProvider()
            rdsClient = AmazonRDSClientBuilder
                    .standard()
                    .withCredentials(this.creds)
                    .withRegion(Regions.US_EAST_1)
                    .build()
        }

    }


    protected List getDBClusterSnapShotList(){
        init()

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
