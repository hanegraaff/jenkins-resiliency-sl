package com.hanegraaff.resiliency.rds

import com.amazonaws.auth.AWSCredentialsProvider
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
        this.initilized = false;
    }

    AmazonRDS(AWSCredentialsProvider credProvider){
        this.creds = false;
    }


    /**
     * Initializes this class with a standard (locally configured) credentials
     * provider, of one is already not supplied.
     *
     * Note that this is being kept outside the default constructor so that there are no CPS issues
     * when running this code inside a pipeline. For more information see this:
     *
     * https://wiki.jenkins.io/display/JENKINS/Pipeline+CPS+method+mismatches
     *
     */
    protected void tryInit(){
        if (!initilized){
            this.creds = AWSConfigurator.getCredentialsProvider()
            rdsClient = AmazonRDSClientBuilder
                    .standard()
                    .withCredentials(this.creds)
                    .withRegion(Regions.US_EAST_1)
                    .build()

            this.initilized = true
        }
    }


    /**
     * helper function that returns a list of manual snapshots names
     * using the AWS SDK.
     *
     * @return List of strings with snapshot names
     */
    protected List getDBClusterSnapShotList(){
        tryInit()

        DescribeDBClusterSnapshotsRequest request = new DescribeDBClusterSnapshotsRequest()
        DescribeDBClusterSnapshotsResult response

        def snapshotList = []

        def done = false
        def page = null

        Log.log("reading snapshot types")

        // do this in a loop so we can handle paging
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


    /**
     * returns the name of the latest cluster snapshot given a prefix
     * The snapshot will be returned in alphabetical order
     *
     * @param prefix
     * @return
     */
    String getLatestDBClusterSnapshot(String prefix){
        List snapshotList = getDBClusterSnapShotList()
        return ""
    }
}
