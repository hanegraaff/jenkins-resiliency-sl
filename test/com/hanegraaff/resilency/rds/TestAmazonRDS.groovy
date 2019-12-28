package com.hanegraaff

import com.hanegraaff.AWSCli
import com.hanegraaff.IStepExecutor
import com.hanegraaff.resiliency.rds.AmazonRDS
import org.junit.Before
import org.junit.Test
import static org.mockito.Mockito.*;

/**
 * Example test class
 */
class TestAmazonRDS {
    private IStepExecutor _steps

    @Before
    void setup(){

    }

    @Test
    void test_invoke() {
        AmazonRDS amazonRDS = new AmazonRDS()
        /*AWSCli cli = new AWSCli(this._steps)

        when(this._steps.sh(anyString())).thenReturn(  2)

        println("my return value is: " + this._steps.sh("hello"))

        def result = cli.invoke("some cli command")


        assert result == 2*/

    }

}