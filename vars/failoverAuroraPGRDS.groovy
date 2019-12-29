import com.hanegraaff.logging.LogManager
import com.hanegraaff.resiliency.rds.AmazonRDS

/**
 * Performs a Database Failover (AWS Postgres Aurora) by performing
 * the following steps:
 * 1) Get latest database snapshot
 * 2) ???
 * 3) profit!
 *
 * @param sysid The sysid of the database in question.
 * @return
 */
def call(String sysid) {
    LogManager.setPipelineSteps(this)
    echo "About to failover database for $sysid"

    AmazonRDS rds = new AmazonRDS()

    echo "Fetching latest snapshots"
    rds.getLatestDBClusterSnapshot(sysid)

    // other database failover steps go here
    echo "database was failed over"

}
