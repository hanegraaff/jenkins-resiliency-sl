import com.hanegraaff.logging.Log
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
//@NonCPS
def call(String sysid) {
    LogManager.setPipelineSteps(this)
    Log.log "About to failover database for $sysid"

    getLatestSnapShot()

    // other database failover steps go here
    Log.log "database was failed over"

}

@NonCPS
def getLatestSnapShot(){
    AmazonRDS rds = new AmazonRDS()
    Log.log "Fetching latest snapshots"
    rds.getLatestDBClusterSnapshot(sysid)
}
