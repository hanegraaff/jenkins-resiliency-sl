import com.amazonaws.auth.AWSCredentialsProvider
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.logging.Log
import com.hanegraaff.logging.LogManager
import com.hanegraaff.resiliency.AmazonEC2Resiliency
import com.hanegraaff.resiliency.AmazonRDSResiliency

/**
 * Starts an EC2 instance given the Instance ID. The return value will be a string representing
 * the instance new state
 *
 * @param instanceID Instance ID of the EC2 instance that will be started
 * @param credProvider Optional credentials provider if an assume-role operation is used.
 *        If null, it will use a default (local) provider
 * @param region Optional name of the EC2 region to use. if null,
 *        it will use a default region
 * @return the name of the new state of the EC2 instance
 */
def call(String instanceID, AWSCredentialsProvider credProvider, String region) {
    LogManager.setPipelineSteps(this)
    Log.logToJenkinsConsole "Starting Instance: $instanceID in region: $region"

    try {
        AmazonEC2Resiliency res = new AmazonEC2Resiliency(credProvider)
        res.initializeClient(region)

        def state = res.startEC2Instance(instanceID)

        Log.logToJenkinsConsole "Instance: $instanceID state is now: $state"
        return state
    }
    catch(AWSException awe){
        Log.logToJenkinsConsole "There was an error starting ec2: $awe"
        throw awe
    }
}
