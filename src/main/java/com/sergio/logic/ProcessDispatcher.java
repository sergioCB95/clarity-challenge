package com.sergio.logic;

import com.sergio.constant.ExecutionTypes;
import com.sergio.data.InputArgs;
import com.sergio.performance.PerformanceMetrics;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Dispatcher of the different processes executed by the app
 */
public class ProcessDispatcher {
    private InputArgs inputArgs = null;
    private LogProcessor logProcessor = null;

    public ProcessDispatcher(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
    }

    /**
     * Dispatch execution process depending of the execution type specified in the app input args
     * @throws IOException
     * @throws InterruptedException
     */
    public void dispatch() throws IOException, InterruptedException {

        // if execution type == TIME_RANGE
        if(inputArgs.getExecutionType().equals(ExecutionTypes.TIME_RANGE)) {
            // Process input log files filtered by a timestamp range
            // Get connected hosts to the specified hostname
            execProcess(
                    processConnectedHostInfoFunc(
                            inputArgs.getInitTimestamp(),
                            inputArgs.getEndTimestamp(),
                            inputArgs.getHostname(),
                            inputArgs.getPath()
                    )
            );
        // if execution type == LOOP
        } else if (inputArgs.getExecutionType().equals(ExecutionTypes.LOOP)) {
            // Execute loop process every hour
            long millisInHour = 1000 * 60 * 60;
            loopProcess(millisInHour);
        }
    }

    /**
     * Execute input log process in an infinite loop in an specified frequency
     * @param millis
     * @throws IOException
     * @throws InterruptedException
     */
    public void loopProcess(Long millis) throws IOException, InterruptedException {
        Instant instant = null;

        // Infinite loop
        while (true) {

            // Get timestamp 1 hour ago
            instant = instant == null
                    ? Instant.now()
                    : Instant.ofEpochMilli(instant.toEpochMilli() - millis);

            // Process input log file filtering logs generated last hour and get all info about specified hostname
            execProcess(
                    processAllHostInfoFunc(
                            instant.toEpochMilli() - millis,
                            instant.toEpochMilli(),
                            inputArgs.getHostname(),
                            inputArgs.getPath()
                    )
            );

            // Wait an hour to execute againg
            Thread.sleep(millis);
        }
    }

    /**
     * Execute input log process function
     * @param func
     * @throws IOException
     */
    public void execProcess(ProcessFunction func) throws IOException {
        execProcess(func, new AtomicBoolean());
    }

    /**
     * Execute an specified process function  and record metrics if specified in configuration
     * @param func
     * @param stillMonitoring
     * @throws IOException
     */
    void execProcess(ProcessFunction func, AtomicBoolean stillMonitoring) throws IOException {

        PerformanceMetrics performanceMetrics = getPerformanceMetrics();

        // If measuring performance, get initial time
        if (inputArgs.isPerformanceMetrics()) {
            performanceMetrics.startingExecution();
            performanceMetrics.monitoringMemory(stillMonitoring);
        }

        // Executing log processing
        func.process();

        // If measuring performance, get end time and calculate execution time
        stillMonitoring.set(false);
        if (inputArgs.isPerformanceMetrics()) {
            performanceMetrics.endingExecution();
            System.out.println("Execution time: " + performanceMetrics.getFinalExecutionTime() + "ms");
        }
    }

    /**
     * Give input log process function to get info about all connected hosts to a specified host in a specified timestamp range
     * @param initTimestamp
     * @param endTimestamp
     * @param hostname
     * @param path
     * @return
     */
    public ProcessFunction processConnectedHostInfoFunc(
            Long initTimestamp,
            Long endTimestamp,
            String hostname,
            String path
    ) {
        return new LogProcessor(
                initTimestamp,
                endTimestamp,
                hostname,
                path
        )::processConnectedHostInfo;
    }

    /**
     * Give input log process function to get all info about an specified host in a specified timestamp range
     * @param initTimestamp
     * @param endTimestamp
     * @param hostname
     * @param path
     * @return
     */
    public ProcessFunction processAllHostInfoFunc(
            Long initTimestamp,
            Long endTimestamp,
            String hostname,
            String path
    ) {
        return new LogProcessor(
                initTimestamp,
                endTimestamp,
                hostname,
                path
        )::processAllHostInfo;
    }

    public PerformanceMetrics getPerformanceMetrics() {
        return PerformanceMetrics.getInstance();
    }

    interface ProcessFunction {
        public void process() throws IOException;
    }
}
