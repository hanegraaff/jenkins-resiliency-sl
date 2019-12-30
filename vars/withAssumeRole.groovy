import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder
import com.hanegraaff.aws.AWSConfigurator
import com.hanegraaff.exceptions.AWSException

/**
 *
 * This global variables allows for the supplied closure to access credential provider (STSAssumeRoleSessionCredentialsProvider)
 * of for the roleArn parameter. The code inside the closure will perform some AWS operation
 *
 * Example:
 *
 * withAssumeRole("arn:aws:iam::1234567890:role/role-abd") {AWSCredentialsProvider assumedCreds ->
 *     println(assumedCreds.getCredentials().AWSAccessKeyId)
 *     println(assumedCreds.getCredentials().AWSSecretKey)
 * }
 *
 * The global variable will throw an AWSException in case of any Amazon
 * errors.
 *
 * @param roleArn String representing the role arn that will be assumed.
 * @param cl Closure with access to the assumed Credentials Provider
 */

def withAssumeRole(roleArn, cl){

    try{
        AWSCredentialsProvider localCreds = AWSConfigurator.getCredentialsProvider()

        AWSSecurityTokenService stsClient =
                new AWSSecurityTokenServiceClientBuilder()
                        .withCredentials(localCreds).withRegion(Regions.US_EAST_1).build()

        STSAssumeRoleSessionCredentialsProvider assumedCreds =
                new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, UUID.randomUUID().toString())
                        .withStsClient(stsClient).build()
    } catch (Exception e){
        throw new AWSException(e);
    }

    cl(assumedCreds)
}