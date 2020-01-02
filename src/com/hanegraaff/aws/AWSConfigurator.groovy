package com.hanegraaff.aws

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions

class AWSConfigurator {

    /**
     * Returns the default credentials provider, namely the one configured locally
     * on the jenkins instance.
     *
     *  https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
     *
     * @return
     */
    static AWSCredentialsProvider getCredentialsProvider(){
        return new DefaultAWSCredentialsProviderChain()
    }

    /**
     * Returns a "Regions" enum representing the current region, or US_EAST_1
     * when not running on an EC2 instance
     *
     * @return
     */
    static Regions getCurrentRegion(){
        Region region = Regions.getCurrentRegion()

        if (region == null){
            return Regions.US_EAST_1
        }
        else{
            return fromString(region.toString())
        }
    }


    /**
     * Determines the "Regions" enums based on the supplied strings. Note that not all regions
     * are mapped.
     *
     * @param regionName
     * @return
     */
    static Regions fromString(String regionName){

        regionName = regionName.toUpperCase()

        switch(regionName) {
            case ["US_EAST_1", "US-EAST-1"]:
                return Regions.US_EAST_1
                break
            case ["US_EAST_2", "US-EAST-2"]:
                return Regions.US_EAST_2
                break
            case ["US_WEST_1", "US-WEST-1"]:
                return Regions.US_WEST_1
                break
            case ["US_WEST_2", "US-WEST-2"]:
                return Regions.US_WEST_2
                break
            default:
                throw new IllegalArgumentException("$regionName is not supported by this shared library")
        }
    }
}
