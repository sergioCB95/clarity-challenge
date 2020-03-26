package com.sergio.data.manager;

import com.sergio.data.ConnectionLog;
import com.sergio.exception.InputFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionLogDataManagerTest {

    ConnectionLogDataManager connectionLogDataManager = ConnectionLogDataManager.getInstance();;

    @Test
    public void parseString_correctLog() {
        ConnectionLog connectionLog = connectionLogDataManager.parseString("1565647204351 Aadvik Matina");

        assertEquals(1565647204351L, connectionLog.getTimestamp());
        assertEquals("Aadvik", connectionLog.getHostname1());
        assertEquals("Matina", connectionLog.getHostname2());
    }

    @Test
    public void parseString_incorrectArgNumber() {
        assertThrows(InputFormatException.class, () -> connectionLogDataManager.parseString("1565647204351 Aadvik Matina Pepita"));
    }

    @Test
    public void parseString_ErrorParsingTS() {
        assertThrows(NumberFormatException.class, () -> connectionLogDataManager.parseString("test Aadvik Matina"));
    }
}
