package com.hanegraaff

import org.junit.Before
import org.junit.Test
import static org.mockito.Mockito.*;

/**
 * Example test class
 */
class TestAWSCli {
    private IStepExecutor _steps

    @Before
    void setup(){
        this._steps = mock(IStepExecutor.class)
    }

    @Test
    void test_invoke() {
        AWSCli cli = new AWSCli(this._steps)

        when(this._steps.sh(anyString())).thenReturn(  2)

        println("my return value is: " + this._steps.sh("hello"))

        def result = cli.invoke("some cli command")


        assert result == 1

    }

}