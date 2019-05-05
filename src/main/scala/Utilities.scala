package big_data
/*
// In order to evaluate tasks, we'll need a Scheduler
import monix.execution.Scheduler.Implicits.global
// A Future type that is also Cancelable
import monix.execution.CancelableFuture
// Task is in monix.eval
import monix.eval.Task
*/
import java.util.{ Timer, TimerTask }
import scala.util.{ Success, Failure }


object Utilities {
    /**
    custom retry 
    with exponential backoff:
    //
    def retryBackoff[A](
        source: Task[A],
        maxRetries: Int, 
        firstDelay: FiniteDuration
        ): Task[A] = 
        source.onErrorHandleWith {
            case ex: Exception =>
            if (maxRetries > 0){
                // Recursive call, it's OK as Monix is stack-safe
                retryBackoff(source, maxRetries-1, firstDelay*2)
                .delayExecution(firstDelay)
            }else{
                Task.raiseError(ex)
            }
        }*/
    
    /*
    // [from](https://stackoverflow.com/a/48968081/4623097)
    Timer t = new java.util.Timer();
    t.schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    // your code here
                    // close the thread
                    t.cancel();
                }
            }, 
            5000 
    );*/
    /**
    caveat:
    After the last live reference to a Timer object goes away 
    and all outstanding tasks have completed execution, 
    the timer's task execution thread terminates gracefully 
    (and becomes subject to garbage collection). 
    However, 
    this can take arbitrarily long to occur.
    
    By default, 
    the task execution thread does not run as `a daemon thread`, 
    so it is capable 
    >>>of keeping an application from terminating.<<<
    If a caller wants 
    to terminate a timer's task execution thread rapidly, 
    the caller should invoke 
    the timer's cancel method.
    
    /// @toDo: @fixIt: no matter where I close timer 
    /// it prevents an application from terminating after task was executed 
    */
    def run_Delayed_Task( 
        task: () => Unit,
        delay: Long = 1000l
    )( 
        // if no new timer createt at function invocation
        // then `timer.cancel()` stops exection of all processes 
        // that have shared | common timer
        implicit timer: Timer//? = new java.util.Timer() 
    ): Unit = timer
        .schedule( 
            new java.util.TimerTask() {
                override def run(): Unit = {
                    // your code here
                    task()
                    // close the thread
                    //?timer.cancel()
                }
            }, 
            delay
        )
}
