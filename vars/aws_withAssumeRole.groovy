import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder
import com.hanegraaff.aws.AWSConfigurator
import com.hanegraaff.exceptions.AWSException
import com.hanegraaff.logging.Log
import com.hanegraaff.logging.LogManager

/**
 *
 * Allows for the supplied closure to access a credential provider (STSAssumeRoleSessionCredentialsProvider)
 * for the roleArn parameter. This is used by code and steps that interact with AWS
 *
 * Example:
 *
 * steps{
 *    aws_withAssumeRole("arn:aws:iam::1234567890:role/abc", "us-east-1"){assumedCreds ->
 *       script{
 *          env.latestRDSSnapshot = aws_rds_findLatestAuroraSnapshot("snapshot-prefix-name", assumedCreds, "us-east-1")
 *       }
 *    }
 * }
 *
 * The global variable will throw an AWSException in case of any Amazon
 * errors.
 *
 * @param roleArn String representing the role arn that will be assumed.
 * @param region The AWS region of the STS service used for the assume role operation
 * @param cl Closure with access to the assumed Credentials Provider
 */

def call(String roleArn, String regionName, Closure<?> cl){

    LogManager.setPipelineSteps(this)
    STSAssumeRoleSessionCredentialsProvider assumedCreds = null

    try{

        Log.logToJenkinsConsole "Assuming role to: $roleArn using region: $regionName"

        Regions region =  (regionName != null)?AWSConfigurator.fromString(regionName):AWSConfigurator.getCurrentRegion()

        AWSCredentialsProvider localCreds = AWSConfigurator.getCredentialsProvider()
        AWSSecurityTokenService stsClient =
                new AWSSecurityTokenServiceClientBuilder()
                        .withCredentials(localCreds).withRegion(region).build()

        assumedCreds =
                new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, UUID.randomUUID().toString())
                        .withStsClient(stsClient).build()
    } catch (Exception e){
        Log.logToJenkinsConsole "There was an error assuming role because"
        throw new AWSException("Error assuming role: $roleArn", e)
    }

    Log.logToJenkinsConsole "Executing closure"
    cl(assumedCreds)
}