package com.sergio.logic;

import com.sergio.constant.ExecutionTypes;
import com.sergio.data.InputArgs;
import com.sergio.performance.PerformanceMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;

public class ProcessDispatcherTest {

    ProcessDispatcher processDispatcher = null;

    @BeforeEach
    void BeforeEach() {
        processDispatcher = null;
    }

    @Test
    void timeRangeProcess_processWithoutMonitoring() throws IOException {
        InputArgs inputArgs = new InputArgs();
        inputArgs.setPerformanceMetrics(false);

        processDispatcher = new ProcessDispatcher(inputArgs);

        ProcessDispatcher processDispatcherSpy = spy(processDispatcher);
        ProcessDispatcher.ProcessFunction function = mock(ProcessDispatcher.ProcessFunction.class);

        PerformanceMetrics mockPerformanceMetrics = mock(PerformanceMetrics.class);
        doReturn(mockPerformanceMetrics).when(processDispatcherSpy).getPerformanceMetrics();

        processDispatcherSpy.execProcess(function);

        verify(function, times(1)).process();
        verify(mockPerformanceMetrics, times(0)).startingExecution();
        verify(mockPerformanceMetrics, times(0)).endingExecution();
        verify(mockPerformanceMetrics, times(0)).monitoringMemory(new AtomicBoolean(true));
    }

    @Test
    void timeRangeProcess_processWithMonitoring() throws IOException {
        InputArgs inputArgs = new InputArgs();
        inputArgs.setPerformanceMetrics(true);
        AtomicBoolean stillMonitoring = new AtomicBoolean(true);

        processDispatcher = new ProcessDispatcher(inputArgs);

        ProcessDispatcher processDispatcherSpy = spy(processDispatcher);
        ProcessDispatcher.ProcessFunction function = mock(ProcessDispatcher.ProcessFunction.class);

        PerformanceMetrics mockPerformanceMetrics = mock(PerformanceMetrics.class);
        doReturn(mockPerformanceMetrics).when(processDispatcherSpy).getPerformanceMetrics();
        doNothing().when(mockPerformanceMetrics).startingExecution();
        doNothing().when(mockPerformanceMetrics).endingExecution();
        doNothing().when(mockPerformanceMetrics).monitoringMemory(new AtomicBoolean(true));

        processDispatcherSpy.execProcess(function, stillMonitoring);

        verify(function, times(1)).process();
        verify(mockPerformanceMetrics, times(1)).startingExecution();
        verify(mockPerformanceMetrics, times(1)).endingExecution();
        verify(mockPerformanceMetrics, times(1)).monitoringMemory(stillMonitoring);
    }

    @Test
    void dispatch_timeRange() throws IOException, InterruptedException {
        InputArgs inputArgs = new InputArgs();
        inputArgs.setExecutionType(ExecutionTypes.TIME_RANGE);
        inputArgs.setInitTimestamp(0L);
        inputArgs.setEndTimestamp(10L);
        inputArgs.setHostname("test");
        inputArgs.setPath("/path");

        processDispatcher = new ProcessDispatcher(inputArgs);
        ProcessDispatcher processDispatcherSpy = spy(processDispatcher);
        ProcessDispatcher.ProcessFunction function = mock(ProcessDispatcher.ProcessFunction.class);

        doReturn(function).when(processDispatcherSpy)
                .processConnectedHostInfoFunc(0L, 10L, "test", "/path");
        doNothing().when(processDispatcherSpy).execProcess(function);
        doNothing().when(processDispatcherSpy).loopProcess(1000L * 60 * 60);

        processDispatcherSpy.dispatch();
        verify(processDispatcherSpy, times(1))
                .processConnectedHostInfoFunc(0L, 10L, "test", "/path");
        verify(processDispatcherSpy, times(1)).execProcess(function);
        verify(processDispatcherSpy, times(0)).loopProcess(1000L * 60 * 60);
    }

    @Test
    void dispatch_loop() throws IOException, InterruptedException {
        InputArgs inputArgs = new InputArgs();
        inputArgs.setExecutionType(ExecutionTypes.LOOP);
        inputArgs.setInitTimestamp(0L);
        inputArgs.setEndTimestamp(10L);
        inputArgs.setHostname("test");
        inputArgs.setPath("/path");

        processDispatcher = new ProcessDispatcher(inputArgs);
        ProcessDispatcher processDispatcherSpy = spy(processDispatcher);
        ProcessDispatcher.ProcessFunction function = mock(ProcessDispatcher.ProcessFunction.class);

        doReturn(function).when(processDispatcherSpy)
                .processConnectedHostInfoFunc(0L, 10L, "test", "/path");
        doNothing().when(processDispatcherSpy).execProcess(function);
        doNothing().when(processDispatcherSpy).loopProcess(1000L * 60 * 60);

        processDispatcherSpy.dispatch();
        verify(processDispatcherSpy, times(0))
                .processConnectedHostInfoFunc(0L, 10L, "test", "/path");
        verify(processDispatcherSpy, times(0)).execProcess(function);
        verify(processDispatcherSpy, times(1)).loopProcess(1000L * 60 * 60);
    }
}
