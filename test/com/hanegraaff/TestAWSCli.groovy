package com.hanegraaff

import com.hanegraaff.AWSCli
import com.hanegraaff.IStepExecutor;
import com.hanegraaff.ioc.ContextRegistry;
import com.hanegraaff.ioc.IContext;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Example test class
 */
public class TestAWSCli {
    private IContext _context;
    private com.hanegraaff.IStepExecutor _steps;

    @Before
    void setup() {
        _context= org.mockito.Mockito.mock(com.hanegraaff.ioc.IContext.class)
        _steps = org.mockito.Mockito.mock(com.hanegraaff.IStepExecutor.class)

        org.mockito.Mockito.when(_context.getStepExecutor()).thenReturn(_steps)

        ContextRegistry.registerContext(_context);
    }

    @Test
    void test_invoke() {
        AWSCli cli = new AWSCli(this._steps);

        org.mockito.Mockito.when(_steps.sh()).thenReturn(0)

        int result = cli.invoke("aws s3 ls")

        assert result == 1

    }

}