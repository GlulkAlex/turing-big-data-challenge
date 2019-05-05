package big_data//test.main 

import java.time.{ 
  Instant
, LocalDateTime 
}
import java.util.concurrent.{ 
    Executors, 
    ScheduledExecutorService,
    ScheduledThreadPoolExecutor,
    TimeUnit,
    Callable,
    Future
}
import java.lang.{ Runnable }

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
//
import com.fortysevendeg.lambdatest._
import com.fortysevendeg.lambdatest.LambdaTest

import Utilities.{ run_Delayed_Task }


/**
show test:definedTestNames
testOnly big_data.LambdaTest_event_Delay
*/
class LambdaTest_event_Delay 
  extends LambdaTest 
{
  /*import Data_Flow.{ 
    Event_Delay
  , event_Delay
  }*/
    /*
    // convertor
    implicit val to_LocalDateTime = ( 
        value: String 
    ) => LocalDateTime.parse( value )*/
    //?implicit val timer/*: Timer*/ = new java.util.Timer() 
  
  val zero_Delay = FiniteDuration.apply( 0l, MILLISECONDS )
  val simulation_Tick/*: Duration*/ = java.time.Duration
    // java.time
    .ofMillis( 84l )
  val simulation_Tick_Duration = scala.concurrent.duration.Duration
    //.apply()
    .create( 84l, MILLISECONDS )

  var start_Event: LocalDateTime = LocalDateTime.MIN
  // val timestamp = System.currentTimeMillis / 1000
  var start_TimeStamp: Instant = Instant.MIN//EPOCH
    //Instant.now()
    
  //?start_Event = "2015-11-23T03:57"//last_Turbine_Event.date
  
  val act: LambdaAct = label( 
    "event_Delay test"
  //, tags = Set( "SKIP" , "ignore" ) 
  ) {
    test(
      "callbacks chain of tasks\n" + 
      "with Timer\n" + 
      "where each expected to run | emit after its delay in seconds"
    , tags = Set( "SKIP" , "ignore" ) 
    ) {
        implicit val timer/*: Timer*/ = new java.util.Timer() 
        
      start_TimeStamp = Instant.now() 
        // System.currentTimeMillis // ( ???: Long ) / 1000
      // or FiniteDuration.apply( )
      val expected_Result = new FiniteDuration( 
            length = 0l, 
            unit = MILLISECONDS 
        )
        println( s"initial start_TimeStamp: ${start_TimeStamp}" ) 
        // callbacks chain:
        val task_1 = () => run_Delayed_Task( 
            task = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_1: start_TimeStamp: ${start_TimeStamp}" ) 
                // clean up ?
                timer.cancel()
            },
            delay = 3000l
        )//( new java.util.Timer() )
        val task_2 = () => run_Delayed_Task( 
            task = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_2: start_TimeStamp: ${start_TimeStamp}" ) 
                // callback
                task_1()
            },
            delay = 2000l
        )//( new java.util.Timer() )
        val task_3 = () => run_Delayed_Task( 
            task = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_3: start_TimeStamp: ${start_TimeStamp}" ) 
                // callback
                task_2()
            },
            delay = 1000l
        )//( new java.util.Timer() )
        
        task_3()
      // return
      assertEq( 
        zero_Delay
        , expected_Result
        , "expected 0 delay"
      )
    } + 
    test(
      "callbacks chain of tasks\n" + 
      "with executor\n" + 
      "where each expected to run | emit after its delay in seconds",
    //tags = Set( "SKIP" , "ignore" ) 
    ) {
        //?private final
        val executor: ScheduledExecutorService = Executors
            //.newSingleThreadScheduledExecutor()
            // or
            .newScheduledThreadPool(1)
        val expected_Result = 0
        
        start_TimeStamp = Instant.now() 
        println( s"initial start_TimeStamp: ${start_TimeStamp}" ) 
        // callbacks chain:
        val task_1: java.util.concurrent.Callable[
            //Unit
            Instant
        ] = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_1: start_TimeStamp: ${start_TimeStamp}" ) 
                start_TimeStamp
            }
        val task_2: java.util.concurrent.Callable[
            //Unit
            Instant 
        ] = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_2: start_TimeStamp: ${start_TimeStamp}" ) 
                start_TimeStamp
            }
        val task_3: java.util.concurrent.Callable[
            //Unit
            Instant 
        ] = () => { 
                start_TimeStamp = Instant.now()
                println( s"task_3: start_TimeStamp: ${start_TimeStamp}" ) 
                /// @toDo: @fixIt: no matter how to 
                /// clean up the thread pool when the last task has completed
                /// it prevents an application from terminating
                // clean up ?
                // and finally
                executor
                    //?.shutdown()
                    .shutdownNow()
                start_TimeStamp
            }
        /*
        final Runnable beeper = new Runnable() {
            public void run() { System.out.println("beep"); }
        };
        final ScheduledFuture<?> beeperHandle =
            scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        */
// V Future.get(long timeout,
//       TimeUnit unit)
// throws InterruptedException,
//       ExecutionException,
//       TimeoutException

// V Callable.call()
// throws Exception
// Computes a result, or throws an exception if unable to do so.
        executor.schedule( 
            //callable = 
            task_1, 
            //command = task_1, 
            //delay = 
            1, 
            //unit = 
            TimeUnit.SECONDS)
        executor.schedule( 
            task_2, 
            2, 
            TimeUnit.SECONDS)
        val task_3_Scheduled_Future/*ScheduledFuture[Any]*/ = executor.schedule( task_3, 3, TimeUnit.SECONDS)
        // blocking ? like Await ? 
        // Await.ready(awaitable = key_Push_Handler, atMost = 15.seconds)
        val task_3_Results = task_3_Scheduled_Future
            // It lets an application to terminate
            .get( 5l, TimeUnit.SECONDS)
        println( s"task_3_Results: ${task_3_Results}" ) 
        // return
        assertEq( 
            0
            , expected_Result
            , "expected 0 delay"
        )
    } + 
    test(
        "tasks will be scheduled by timer almost immediately | instantly\n" + 
        "but each expected to start after its own delay time",
        tags = Set( "SKIP" , "ignore" ) 
    ) {
        implicit val timer/*: Timer*/ = new java.util.Timer() 
        
      start_TimeStamp = Instant.now() 
      //val same_Time_Event = "2015-11-23T03:57"
        println( s"start tasks loop at TimeStamp: ${start_TimeStamp}" ) 
      ( 1 to 7 )
        .foldLeft[ LambdaAct ]( 
          assert( true, "starting from `true`" )
        ){ case ( 
            result: LambdaAct, 
            i: Int
          ) => {
            // tasks will be scheduled almost immediately | instantly ?
            // but each start at its own time ?
            run_Delayed_Task( 
                task = () => { 
                    start_TimeStamp = Instant.now()
                    println( s"${i}: start_TimeStamp: ${start_TimeStamp}" ) 
                    // clean up ?
                    if( i == 7 ){ timer.cancel() }
                },
                delay = i * 1000l
            )//( new java.util.Timer() )
            // clean up ?
            // Unexpected exception: Timer already cancelled.
            //timer.cancel()
            // return
            result +
            assert( 
              simulation_Tick_Duration > zero_Delay
              , "expected 0 delay"
            )
          }
        }
      }

  }
}
// companion 
object LambdaTest_event_Delay
  extends App 
{
  run( "Data_Flow event_Delay test", new LambdaTest_event_Delay )
}
