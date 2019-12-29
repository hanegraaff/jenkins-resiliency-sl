package com.hanegraaff

import com.hanegraaff.jenkins.IStepExecutor
import com.hanegraaff.resiliency.rds.AmazonRDS
import org.junit.Before
import org.junit.Test

/**
 * Example test class
 */
class TestAmazonRDS {
    private IStepExecutor _steps

    @Before
    void setup(){

    }

    @Test
    void test_no_snapshots(){
        AmazonRDS amazonRDS = new AmazonRDS()

        def latestSnapshot = amazonRDS.getLatestDBClusterSnapshot("xxx")

        assert latestSnapshot == ""

    }

}