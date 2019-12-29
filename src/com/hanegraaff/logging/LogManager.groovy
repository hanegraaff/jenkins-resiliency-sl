package com.hanegraaff.logging;

/**
 * This class holds a static instance of the Jenkins step object that
 * we can use to log the jenkins console.
 *
 * If supplied here, the logger class defined in this package will use
 * it to call the "echo" method on it.
 */
public class LogManager {

    static def _steps

    /**
     * Sets the Jenkins step object
     *
     * @param steps Jenkins step object. The "this" object in the pipeline code
     */
    static void setPipelineSteps(steps) {
        this._steps = steps
    }

    /**
     * Gets the Jenkins pipeline object if set
     *
     * @return
     */
    static def getPipelineSteps() {
        return this._steps
    }
}
