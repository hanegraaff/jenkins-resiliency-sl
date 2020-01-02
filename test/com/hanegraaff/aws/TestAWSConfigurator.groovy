package com.hanegraaff.aws

import com.amazonaws.regions.Regions
import org.junit.Before
import org.junit.Test

/**
 * Tests the AWSConfigurator class
 */
class TestAWSConfigurator {
    @Before
    void setup(){

    }

    /**
     * Tests that when the string "US_EAST_1" is supplied, Regions.US_EAST_1 is returned
     */
    @Test
    void test_fromString_US_EAST_1() {

        Regions region = AWSConfigurator.fromString("US_EAST_1")
        assert region == Regions.US_EAST_1
    }

    /**
     * Tests that when the string "US-EAST-1" is supplied, Regions.US_EAST_1 is returned
     */
    @Test
    void test_fromString_US_EAST_1_Alt() {

        Regions region = AWSConfigurator.fromString("US-EAST-1")
        assert region == Regions.US_EAST_1
    }

    /**
     * Tests that when the string "us_east_1" (lowercase) is supplied, Regions.US_EAST_1 is returned
     */
    @Test
    void test_fromString_US_EAST_1_Lowercase() {

        Regions region = AWSConfigurator.fromString("us_east_1")
        assert region == Regions.US_EAST_1
    }


    /**
     * Tests that when the string "us-west-2" (lowercase) is supplied, Regions.US_WEST_2 is returned
     */
    @Test
    void test_fromString_US_WEST_2_Lowercase() {

        Regions region = AWSConfigurator.fromString("us-west-2")
        assert region == Regions.US_WEST_2
    }


    /**
     * Tests that when an invalid region name is supplied, an IllegalStateException is thrown
     */
    @Test(expected = IllegalArgumentException.class)
    void test_fromString_invalidRegion() {
        Regions region = AWSConfigurator.fromString("xxx")
    }
}
