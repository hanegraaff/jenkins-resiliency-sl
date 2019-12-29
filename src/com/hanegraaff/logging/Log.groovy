package com.hanegraaff.logging

/**
 *  Main logging class. It will either log to stdout or to the Jenkins
 *  console depending on whether the steps object has been properly
 *  set.
 */
class Log {

    /**
     * Log the contents of the message to either stdout or Jenkins Console
     *
     * @param msg Message to be logged
     */
    static void log(String msg){
        println msg
    }

    static void logToJenkinsConsole(String msg){
        def step = LogManager.getPipelineSteps()

        if (step != null) {
            try {
                step.echo(msg)
            }
            catch (Exception e) {
                println "$msg - could not log to Jenkins console: $e.getMessage()()"
            }
        }
        else{
            println msg
        }
    }
}
