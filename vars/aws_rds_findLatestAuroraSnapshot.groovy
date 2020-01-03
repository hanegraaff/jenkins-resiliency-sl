import com.amazonaws.auth.AWSCredentialsProvider
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.logging.Log
import com.hanegraaff.logging.LogManager
import com.hanegraaff.resiliency.AmazonRDSResiliency

/**
 * Identifies the latest database snapshot given a prefix.
 * The prefix is used a filter, and "latest" is found by sorting
 * snapshots in alphabetical (descending) order and returning
 * the top item.
 *
 * For example if my snapshots are:
 * snapshot_20191229
 * snapshot_20191230
 * snapshot_20191231
 *
 * The return value will be "snapshot_20191231"
 *
 * @param prefix The prefix used filter snapshots
 * @param credProvider Optional credentials provider if an assume-role operation is used.
 *        If null, it will use a default (local) provider
 * @param region Optional name of the RDS region to use. if null,
 *        it will use a default region
 * @return the name of the latest snapshot based on the prefix
 */
def call(String prefix, AWSCredentialsProvider credProvider, String region) {
    LogManager.setPipelineSteps(this)
    Log.logToJenkinsConsole "Retrieving latest snapshot for the following prefix: $prefix in region: $region"

    try {
        AmazonRDSResiliency rds = new AmazonRDSResiliency(credProvider)
        rds.initializeClient(region)

        def latestSnapshot = rds.getLatestDBClusterSnapshot(prefix)

        Log.logToJenkinsConsole "Latest snapshot is: $latestSnapshot"
        return latestSnapshot
    }
    catch(AWSException awe){
        Log.logToJenkinsConsole "there was an error retrieving latest snapshot: "
            + awe.printMessage()
        throw awe
    }
}
