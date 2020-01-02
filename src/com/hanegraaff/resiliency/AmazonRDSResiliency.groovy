package com.hanegraaff.resiliency

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.AmazonRDS
import com.amazonaws.services.rds.AmazonRDSClientBuilder
import com.amazonaws.services.rds.model.DescribeDBClusterSnapshotsRequest
import com.amazonaws.services.rds.model.DescribeDBClusterSnapshotsResult
import com.hanegraaff.aws.AWSConfigurator
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.logging.Log

/**
 * This class acts a wrapper for any RDS resiliency function. It must be initialized in two steps:
 *
 * 1) By calling the constructor and optionally passing it an external credentials provider
 * 2) By calling the "initializeClient()" method and optionally passing it a region name
 *
 * This is done to work around Jenkins CPS limitation when writing constructors
 *
 * Example:
 *      AWSCredentialsProvider creds = ...
 *      AmazonRDSResiliency amazonRDS = new AmazonRDSResiliency(creds)
 *      amazonRDS.initializeClient(Regions.US_EAST_1.name())
 *
 *      amazonRDS.getLatestDBClusterSnapshot("some_prefix")
 */
class AmazonRDSResiliency {
    // defining as a member variable so that it may be mocked
    protected AWSCredentialsProvider creds
    protected AmazonRDS client

    def initialized

    /**
     * Constructor
     *
     * Will use a default credentials provider
     */
    AmazonRDSResiliency(){
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
    AmazonRDSResiliency(AWSCredentialsProvider credProvider){
        this.creds = credProvider
        this.initialized = false
    }


    /**
     * Initializes this class post construction
     *
     * That this is being kept outside the default constructor so that there are no CPS issues
     * when running this code inside a pipeline. For more information see this:
     *
     * https://wiki.jenkins.io/display/JENKINS/Pipeline+CPS+method+mismatches
     *
     * @param regionName The optional name of the region to use. If null, then a default one will be used
     */
    void initializeClient(String regionName){
        if (initialized)
            return

        if (creds == null)
            this.creds = AWSConfigurator.getCredentialsProvider()

        Regions region =  (regionName != null)?AWSConfigurator.fromString(regionName):AWSConfigurator.getCurrentRegion()

        client = AmazonRDSClientBuilder
                .standard()
                .withCredentials(this.creds)
                .withRegion(region)
                .build()

        this.initialized = true
    }


    /**
     * helper function that returns a list of manual snapshots names
     * using the AWS SDK.
     *
     * @return List of strings with snapshot names
     */
    protected List<String> getDBClusterSnapShotList(){

        if (!initialized)
            throw new IllegalStateException("You must initialize AmazonRDSResiliency with a valid Credentials provider")

        DescribeDBClusterSnapshotsRequest request = new DescribeDBClusterSnapshotsRequest()
        DescribeDBClusterSnapshotsResult response

        def snapshotList = []

        def done = false
        def page = null

        // do this in a loop so we can handle paging
        while (!done){
            request.withSnapshotType("manual")
            request.withMarker(page)

            try {
                response = client.describeDBClusterSnapshots(request)
            }
            catch(Exception e){
                throw new AWSException("Error reading database snapshots", e)
            }

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
     * @param prefix a String representing a prefix used to sort the list
     * @return
     */
    String getLatestDBClusterSnapshot(String prefix){
        def snapshotList = getDBClusterSnapShotList()

        if (snapshotList.isEmpty()) return ""

        def filteredList = snapshotList.findAll() {it.startsWith(prefix)}

        if (filteredList.isEmpty()) return ""

        // return the last (most recent) item
        return filteredList.sort()[filteredList.size() - 1]
    }
}
