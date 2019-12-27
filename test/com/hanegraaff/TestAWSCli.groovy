package com.hanegraaff

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Example test class
 */
class TestAWSCli {
    private IStepExecutor _steps

    @Before
    void setup(){
        _steps = Mockito.mock(IStepExecutor.class)
    }

    @Test
    void test_invoke() {
        AWSCli cli = new AWSCli(this._steps)

        Mockito.when(_steps.sh()).thenReturn(0)

        int result = cli.invoke("some cli command")

        assert result == 0

    }

}