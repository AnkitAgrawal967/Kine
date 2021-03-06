package com.kine.executor

import java.util.concurrent.*

class DefaultPriorityExecutorSupplier():ExecutorSupplier {
    /*
    * Number of cores to decide the number of threads
    */
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    /*
    * thread pool executor for background tasks
    */
    private var forBackgroundTasks: PriorityThreadPoolExecutor

    /*
    * thread pool executor for light weight background tasks
    */
    private var forLightWeightBackgroundTasks: ThreadPoolExecutor

    /*
    * thread pool executor for main thread tasks
    */
    private var callbackExecutor: Executor


    init {
        // setting the thread factory
        val backgroundPriorityThreadFactory: ThreadFactory = createThreadFactory()

        // setting the thread pool executor for mForBackgroundTasks;

        forBackgroundTasks = PriorityThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            backgroundPriorityThreadFactory
        )

        // setting the thread pool executor for mForLightWeightBackgroundTasks;
        forLightWeightBackgroundTasks = ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>(),
            backgroundPriorityThreadFactory
        )

        // setting the thread pool executor for mMainThreadExecutor;
        callbackExecutor = createCallbackExecutor()
    }

    private fun createCallbackExecutor(): Executor = CallbackExecutor()

    private fun createThreadFactory(): ThreadFactory = PriorityThreadFactory(Thread.NORM_PRIORITY)

    /*
    * returns the thread pool executor for background task
    */
    override fun forNetworkTasks(): ThreadPoolExecutor {
        return forBackgroundTasks
    }

    /*
    * returns the thread pool executor for light weight background task
    */
   override fun forParsingTasks(): ThreadPoolExecutor {
        return forLightWeightBackgroundTasks
    }

    /*
    * returns the thread pool executor for main thread task
    */
    override fun forCallbackTasks(): Executor {
        return callbackExecutor
    }
}