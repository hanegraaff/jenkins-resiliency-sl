package com.hanegraaff.exceptions

import org.junit.Before
import org.junit.Test

/**
 * Tests the various custom exceptions, including the base: ResiliencyException
 */
class TestExceptions {
    @Before
    void setup(){

    }

    /**
     * Tests that when the exception is constructed a certain way, it will
     * print (toString()) in a predictable way
     */
    @Test
    void test_ResiliencyException_ErrorMessageWithCause() {
        Exception cause = new Exception("This is the cause")
        ResiliencyException re = new ResiliencyException("Test Exception", cause)

        assert re.printMessage() == "<ResiliencyException> Test Exception. Caused by: This is the cause"
    }

    /**
     * Tests that when the exception is constructed without a cause, it will
     * still print in a predictable way
     */
    @Test
    void test_ResiliencyException_ErrorMessageWithoutCause() {
        ResiliencyException re = new ResiliencyException("Test Exception")

        assert re.printMessage() == "<ResiliencyException> Test Exception. Caused by: null"
    }


    /**
     * Tests that when the exception is constructed a certain way, it will
     * print (toString()) in a predictable way
     */
    @Test
    void test_AWSException_ErrorMessageWithCause() {
        Exception cause = new Exception("This is the cause")
        AWSException re = new AWSException("Test Exception", cause)

        assert re.printMessage() == "<AWSException> Test Exception. Caused by: This is the cause"
    }
}
