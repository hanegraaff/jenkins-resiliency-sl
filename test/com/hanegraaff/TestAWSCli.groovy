package com.hanegraaff


import com.hanegraaff.AWSCli
import com.hanegraaff.IStepExecutor;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Example test class
 */
public class TestAWSCli {
    private IStepExecutor _steps

    @Before
    void setup()
        _steps = org.mockito.Mockito.mock(com.hanegraaff.IStepExecutor.class)
    }

    @Test
    void test_invoke() {
        AWSCli cli = new AWSCli(this._steps);

        org.mockito.Mockito.when(_steps.sh()).thenReturn(0)

        int result = cli.invoke("aws s3 ls")

        assert result == 1

    }

}