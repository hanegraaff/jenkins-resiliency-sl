package com.hanegraaff.exceptions

/**
 * Base class for all custom exceptions in this library
 * It's main purpose is to override the "toString()" method
 * to display errors in a more consistent way
 */
class ResiliencyException extends Exception{

    /**
     *
     * Constructor
     *
     */
    ResiliencyException(String message){
        super(message)
    }

    ResiliencyException(String message, Throwable cause){
        super(message, cause)
    }

    /**
     * alternative override to "toString()"
     * Overriding methods can cause CPS errors in Jenkins
     *
     * @return a string with a properly formatted error
     */
    String printMessage(){
        def message = getMessage()
        def cause = getCause()?.getMessage()
        def className = this.getClass().getSimpleName()

        return "<$className> $message. Caused by: $cause"
    }
}
