package com.sergio.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sergio.data.ConnectionLog;
import com.sergio.data.manager.ConnectionLogDataManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

public class LogProcessorTest {

    LogProcessor logProcessor = null;

    @BeforeEach
    void BeforeEach() {
        logProcessor = null;
    }

    @Test
    void checkTimestampRange_UpperBound() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "",
                ""
        );
        assertTrue(logProcessor.checkTimestampRange(new ConnectionLog(10L, "", "")));
    }

    @Test
    void checkTimestampRange_OutOfRange() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "",
                ""
        );
        assertFalse(logProcessor.checkTimestampRange(new ConnectionLog(11L, "", "")));
    }

    @Test
    void updateHostCount_FirstUpdate() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "",
                ""
        );

        logProcessor.updateHostCounts("test", null);

        assertEquals("test", logProcessor.getMaxConnectedHost().get());
        assertEquals(1L, logProcessor.getMaxConnectedCount().get());
    }

    @Test
    void updateHostCount_MaxHostChange() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "",
                ""
        );

        logProcessor.updateHostCounts("test", null);
        logProcessor.updateHostCounts("winner", null);
        logProcessor.updateHostCounts("winner", 1L);

        assertEquals("winner", logProcessor.getMaxConnectedHost().get());
        assertEquals(2L, logProcessor.getMaxConnectedCount().get());
    }

    @Test
    void updateHostCount_MaxHostNoChange() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "",
                ""
        );

        logProcessor.updateHostCounts("test", null);
        logProcessor.updateHostCounts("test", 1L);
        logProcessor.updateHostCounts("looser", null);

        assertEquals("test", logProcessor.getMaxConnectedHost().get());
        assertEquals(2L, logProcessor.getMaxConnectedCount().get());
    }

    @Test
    void checkConnections_noMatch() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "test",
                ""
        );

        LogProcessor spyLogProcessor = spy(logProcessor) ;
        doReturn(1L).when(spyLogProcessor).updateHostCounts("no-match", null);

        spyLogProcessor.checkConnections(new ConnectionLog(0L, "no-match", "neither-this"));

        assertEquals(1, logProcessor.getSendingConnectionHostsCount().get("no-match"));
        assertNull(logProcessor.getSendingConnectionHosts().get("no-match"));
        assertNull(logProcessor.getReceivingConnectionHosts().get("neither-this"));
    }

    @Test
    void checkConnections_ReceivingConnectionMatch() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "match",
                ""
        );

        LogProcessor spyLogProcessor = spy(logProcessor) ;
        doReturn(1L).when(spyLogProcessor).updateHostCounts("test", null);

        spyLogProcessor.checkConnections(new ConnectionLog(0L, "test", "match"));

        assertEquals(1, logProcessor.getSendingConnectionHostsCount().get("test"));
        assertEquals("test", logProcessor.getSendingConnectionHosts().get("test"));
        assertNull(logProcessor.getReceivingConnectionHosts().get("match"));
    }

    @Test
    void checkConnections_SendingConnectionMatch() {
        logProcessor = new LogProcessor(
                0L,
                10L,
                "match",
                ""
        );

        LogProcessor spyLogProcessor = spy(logProcessor) ;
        doReturn(1L).when(spyLogProcessor).updateHostCounts("match", null);

        spyLogProcessor.checkConnections(new ConnectionLog(0L, "match", "test"));

        assertEquals(1, logProcessor.getSendingConnectionHostsCount().get("match"));
        assertNull(logProcessor.getSendingConnectionHosts().get("match"));
        assertEquals("test", logProcessor.getReceivingConnectionHosts().get("test"));
    }

    @Test
    void processConnectedHostInfo_OneFiltered() throws IOException {
        logProcessor = new LogProcessor(
                5L,
                10L,
                "match",
                ""
        );
        String log1 = "0 test match";
        String log2 = "10 match test";
        String log3 = "10 test match";

        ConnectionLog connLog1 = new ConnectionLog(0L, "test", "match");
        ConnectionLog connLog2 = new ConnectionLog(10L, "match", "test");
        ConnectionLog connLog3 = new ConnectionLog(10L, "test", "match");


        LogProcessor spyLogProcessor = spy(logProcessor);
        doReturn(Stream.of(log1, log2, log3)).when(spyLogProcessor).getInputLinesStream();

        ConnectionLogDataManager mockDataManager = mock(ConnectionLogDataManager.class);
        doReturn(mockDataManager).when(spyLogProcessor).getConnectionLogDataManager();
        doReturn(connLog1).when(mockDataManager).parseString(log1);
        doReturn(connLog2).when(mockDataManager).parseString(log2);
        doReturn(connLog3).when(mockDataManager).parseString(log3);

        doReturn(false).when(spyLogProcessor).checkTimestampRange(connLog1);
        doReturn(true).when(spyLogProcessor).checkTimestampRange(connLog2);
        doReturn(true).when(spyLogProcessor).checkTimestampRange(connLog3);

        spyLogProcessor.processConnectedHostInfo();

        assertEquals("test" ,logProcessor.getSendingConnectionHosts().get("test"));
    }

    @Test
    void processAllHostInfo_threeFiltered() throws IOException {
        logProcessor = new LogProcessor(
                5L,
                10L,
                "match",
                ""
        );
        String log1 = "0 test match";
        String log2 = "10 match test";
        String log3 = "10 test match";
        String log4 = "0 test match";
        String log5 = "10 test another";

        ConnectionLog connLog1 = new ConnectionLog(0L, "test", "match");
        ConnectionLog connLog2 = new ConnectionLog(10L, "match", "test");
        ConnectionLog connLog3 = new ConnectionLog(10L, "test", "match");
        ConnectionLog connLog4 = new ConnectionLog(0L, "match", "test");
        ConnectionLog connLog5 = new ConnectionLog(10L, "test", "another");

        LogProcessor spyLogProcessor = spy(logProcessor);
        doReturn(Stream.of(log1, log2, log3, log4, log5)).when(spyLogProcessor).getInputLinesStream();

        ConnectionLogDataManager mockDataManager = mock(ConnectionLogDataManager.class);
        doReturn(mockDataManager).when(spyLogProcessor).getConnectionLogDataManager();
        doReturn(connLog1).when(mockDataManager).parseString(log1);
        doReturn(connLog2).when(mockDataManager).parseString(log2);
        doReturn(connLog3).when(mockDataManager).parseString(log3);
        doReturn(connLog4).when(mockDataManager).parseString(log4);
        doReturn(connLog5).when(mockDataManager).parseString(log5);

        doReturn(false).when(spyLogProcessor).checkTimestampRange(connLog1);
        doReturn(true).when(spyLogProcessor).checkTimestampRange(connLog2);
        doReturn(true).when(spyLogProcessor).checkTimestampRange(connLog3);
        doReturn(false).when(spyLogProcessor).checkTimestampRange(connLog4);
        doReturn(true).when(spyLogProcessor).checkTimestampRange(connLog5);

        doAnswer( (connLog) -> {
            logProcessor.getMaxConnectedHost().set("match");
            logProcessor.getMaxConnectedCount().set(1L);
            logProcessor.getSendingConnectionHostsCount().put("match", 1L);
            logProcessor.getReceivingConnectionHosts().put("test", "test");
            return null;
        }).when(spyLogProcessor).checkConnections(connLog2);
        doAnswer( (connLog) -> {
            logProcessor.getSendingConnectionHostsCount().put("test", 1L);
            logProcessor.getSendingConnectionHosts().put("test", "test");
            return null;
        }).when(spyLogProcessor).checkConnections(connLog3);
        doAnswer( (connLog) -> {
            logProcessor.getMaxConnectedHost().set("test");
            logProcessor.getMaxConnectedCount().set(2L);
            logProcessor.getSendingConnectionHostsCount().put("test", 2L);
            return null;
        }).when(spyLogProcessor).checkConnections(connLog5);

        spyLogProcessor.processAllHostInfo();

        assertEquals("test" ,logProcessor.getSendingConnectionHosts().get("test"));
        assertEquals("test" ,logProcessor.getReceivingConnectionHosts().get("test"));
        assertEquals(2L ,logProcessor.getSendingConnectionHostsCount().get("test"));
        assertEquals(1L ,logProcessor.getSendingConnectionHostsCount().get("match"));
        assertEquals("test" ,logProcessor.getMaxConnectedHost().get());
        assertEquals(2L ,logProcessor.getMaxConnectedCount().get());
    }
}
