package com.hanegraaff.exceptions

/**
 * An Exception caused by an AWS Error.
 * This exception exists mainly to distinguish this
 * from another category of errors
 *
 */
class AWSException extends ResiliencyException {
    AWSException(String message){
        super(message)
    }

    AWSException(String message, Throwable cause){
        super(message, cause)
    }
}
