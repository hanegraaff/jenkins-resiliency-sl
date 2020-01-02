# Jenkins Resiliency Shared Libray

## Overview
A Jenkins shared library that facilitates the failover of cloud based application from a primary region to a failover one.

This library offers two things:
1) A set of global variables that can be invoked from a recovery pipeline. Each global variable represents a step that is part of the recovery process, like identifying the most recent database snapshot or turning off an EC2 instance.
2) A framework (the classes in the ```src``` folder) used to implement and extend the set of global variables.

## Prerequisites
1) AWS SDK Jenkins plugin must be available.
2) AWS credentials  accessible by Jenkins and configured in one of the following ways: 

   https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
3) Jenkins Markdown formatter ("Manage Jenkins" → "Configure Global Security" → "Markup Formatter") should be set to HTML. This will render the shared library documentation properly in the Jenkins console.

## Build environment
This project was built and tested using the following configurations:

1) Ubuntu 18.04.3 LTS
2) Groovy Version: 2.5.8 JVM: 11.0.5 Vendor: Private Build OS: Linux
3) IntelliJ IDEA 2019.3.1 (Community Edition)
4) Jenkins ver. 2.204.1


# Using this library in a pipeline

The library can be used exactly like any other Jenkins shared library

1) Configure the shared library in Jenkins and give it an alias. In this case we call it ```jenkins-sl```.
2) import the library in your pipeline:
```groovy
@Library('resiliency-sl@/master') _
```

## Sample Pipeline
Here is an example pipeline that uses this library.

```groovy
@Library('resiliency-sl@feature/initial-version') _

pipeline {
	agent any
	stages {
		stage ('Failover Database') {
			steps{
			    aws_withAssumeRole("arn:aws:iam::1234567890:role/abc", "us-west-2"){assumedCreds ->
			        script{
			           env.latestRDSSnapshot = aws_rds_findLatestAuroraSnapshot("cbj", assumedCreds, "us-west-2")
			        } 
			    }
				
				// perform other failover steps here
				//...
			    
			}
		}
		stage ('Start EC2 Instance') {
			steps{
				script{
					def state = aws_ec2_startEC2Instance("i-1234567890", null, 'us-west-2')
					echo "ec2 state is $state"
				}
			}
		}
    }
    post {
        failure {
            echo "Pipeline Failed!!!"
        }
    }
}
```

# Library Global Variables

This section describes the global variables exposed by this library.

## aws_withAssumeRole
Allows for the code inside the supplied closure to access an AWS credentials provider for an assumed role.

### Paramters

1) **roleArn**: A String representing the ARN of the role to be assumed
2) **region**: The region of the STS service used for the assume role operation

### Sample use

```Groovy
steps{
    aws_withAssumeRole("arn:aws:iam::1234567890:role/abc", "us-east-1"){assumedCreds ->
        script{
            env.latestRDSSnapshot = aws_rds_findLatestAuroraSnapshot("snapshot-prefix-name", assumedCreds, "us-east-1")
        }
    }
}
```


### GroovyDoc
```groovy
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

def call(String roleArn, String regionName, Closure<?> cl)
```

## aws_rds_startEC2Instance
Starts an EC2 instance given its instance ID and region

### Parameters
1) **instanceID** EC2 Instance ID to start
2) **credProvider** AWS Credentials provider. If null, it will use a locally configured one.
3) **region** The EC2 region to use

### Sample use
```Groovy
def stae = aws_rds_startEC2Instance("i-1234567890abde", null, "us-east-1")
```

### GroovyDoc
```groovy
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
def call(String instanceID, AWSCredentialsProvider credProvider, String region)
```

## aws_rds_findLatestAuroraSnapshot
Finds the latest snapshot based on alphabetical order for an RDS Aurora Database, specifically it will return the first snapshot after sorting in descending order.

For example if my snapshots are:
1) snapshot_20191229
2) snapshot_20191230
3) snapshot_20191231

this step will return "snapshot_20191231"

### Parameters
1) **prefix** The prefix used filter snapshots. For example a prefix of "abc" will only consider snapshots beginning with "abc"
2) **credProvider** AWS Credentials provider. If null, it will use a locally configured one.
3) **region** The RDS region to use

### Sample use
```Groovy
def latest_snapshot = aws_rds_findLatestAuroraSnapshot("snapshot-prefix-name", null, "us-east-1")
```

### GroovyDoc
```groovy
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
def call(String prefix, AWSCredentialsProvider credProvider, String region)
```

# Library framework
Each global variable is built on top of a framework based on the Java AWS SDK, and is implemented by the classes in ```src```folder.
This section provides a high level overview of the package structure

## Logging package
```groovy
com.hanegraaff.logging
```

Provides basic logging capabilities by logging to STDOUT or the Jenkins Console.

### Logging to STDOUT
```groovy
import com.hanegraaff.logging.Log

Log.log "Some Message"
```

will result in the message appearing to STDOUT

### Logging to Jenkins Console
When logging to the Jenkins console we must first use the ```LogManager``` to set the ```steps``` object. This must be done from within a Global Variable definition.

```groovy
import com.hanegraaff.logging.LogManager
import com.hanegraaff.logging.Log

// Sample Global Variable
def call(){
   LogManager.setPipelineSteps(this)
   Log.logToJenkinsConsole "Something important"
}
```
Will log the message to the Jenkins console. 


## Exceptions package
```groovy
com.hanegraaff.exceptions
```

This package contains all the custom application exceptions.
These exceptions are used to represent different categories of errors, and when low level is exception is caught is typically rethrown as a custom exception.

To be clear, these exceptions are coarse and exists solely to categorize the set of errors that this library is willing to generate.

Exceptions in this library make use of chaining, where the underlining cause is wrapped in the custom exception. For additional information on exception chaining see this link:

https://docs.oracle.com/javase/tutorial/essential/exceptions/chained.html


As of this version, there is only one custom exception: ```AWSException```, which represents any error thrown by the SDK.

### ResiliencyException
This is the base class for all custom exceptions. It overrides the ```toString()``` to print errors in a more consistent way.

```groovy
@Override
String toString(){
   def message = getMessage()
   def cause = getCause()?.getMessage()
   def className = this.getClass().getSimpleName()

   return "<$className> $message. Caused by: $cause"
}
``` 
### AWSException
Here is what the AWSException looks like:
```groovy
/**
 * An Exception caused by an AWS Error.
 * This exception exists mainly to distinguish this
 * from another category of errors
 */
class AWSException extends ResiliencyException {
    AWSException(String message){
        super(message)
    }

    AWSException(String message, Throwable cause){
        super(message, cause)
    }
}
```

And here is an example of how to use it:

```groovy
DescribeDBClusterSnapshotsRequest request = new DescribeDBClusterSnapshotsRequest()
DescribeDBClusterSnapshotsResult response

AmazonRDS rdsClient = //... Initialize the client

request.withSnapshotType("manual")

try {
    response = rdsClient.describeDBClusterSnapshots(request)
}
catch(Exception e){
    throw new AWSException("Error reading database snapshots", e)
}
```

## AWS package
```groovy
com.hanegraaff.aws
```

Currently contains a single class, called ```AWSConfigurator``` which contains various utility functions used to facilitate interacting with AWS SDK.

## Resiliency package
```groovy
com.hanegraaff.resiliency
```

Contains the classes that expose the resiliency functions used by the global variables.
Each AWS service is encapsulated into its own class.

For example RDS Resiliency functions, like identifying the latest RDS Cluster Snapshot for a given prefix, are implemented in the ```AmazonRDSResliency``` class.

Each class in this  package inherits from the ```BaseResiliency``` class. This class forces initialization in two steps, to avoid CPS issues with class constructors.
For more information, see this link:

https://wiki.jenkins.io/display/JENKINS/Pipeline+CPS+method+mismatches