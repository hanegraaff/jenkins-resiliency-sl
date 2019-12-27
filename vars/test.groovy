import com.hanegraaff.AWSCli
import com.hanegraaff.StepExecutor

def call(cmd){
    echo "invoking method: $cmd"

    AWSCli cli = new AWSCli(new StepExecutor(this))

    cli.invoke(cmd)
}




