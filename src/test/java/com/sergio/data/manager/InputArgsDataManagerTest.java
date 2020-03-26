package com.sergio.data.manager;

import com.sergio.constant.ExecutionTypes;
import com.sergio.constant.InputArgNames;
import com.sergio.constant.ShortInputArgNames;
import com.sergio.data.InputArgs;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InputArgsDataManagerTest {

    InputArgsDataManager inputArgsDataManager = InputArgsDataManager.getInstance();

    @Test
    public void parseArgs_correctTimeRangeArgs() throws ParseException {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                ExecutionTypes.TIME_RANGE.getValue(),
                "-" + ShortInputArgNames.INIT_TIMESTAMP.getValue(),
                "0",
                "-" + ShortInputArgNames.END_TIMESTAMP.getValue(),
                "1765647204351",
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgs inputArgs = inputArgsDataManager.parseArgs(args);

        assertEquals(ExecutionTypes.TIME_RANGE, inputArgs.getExecutionType());
        assertEquals(0, inputArgs.getInitTimestamp());
        assertEquals(1765647204351L, inputArgs.getEndTimestamp());
        assertEquals("Alsatia", inputArgs.getHostname());
        assertEquals("input-file-5M.txt", inputArgs.getPath());
        assertFalse(inputArgs.isHelp());
    }

    @Test
    public void parseArgs_timeRangeWithoutInitTSException() {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                ExecutionTypes.TIME_RANGE.getValue(),
                "-" + ShortInputArgNames.END_TIMESTAMP.getValue(),
                "1765647204351",
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        assertThrows(ParseException.class, () -> spyDataManager.parseArgs(args));
        verify(spyDataManager, times(1)).printHelp();
    }

    @Test
    public void parseArgs_timeRangeEndTSWrongFormatException() {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                ExecutionTypes.TIME_RANGE.getValue(),
                "-" + ShortInputArgNames.INIT_TIMESTAMP.getValue(),
                "0",
                "-" + ShortInputArgNames.END_TIMESTAMP.getValue(),
                "test",
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        assertThrows(NumberFormatException.class, () -> spyDataManager.parseArgs(args));
        verify(spyDataManager, times(1)).printHelp();
    }

    @Test
    public void parseArgs_correctLoopArgs() throws ParseException {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                ExecutionTypes.LOOP.getValue(),
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgs inputArgs = inputArgsDataManager.parseArgs(args);

        assertEquals(ExecutionTypes.LOOP, inputArgs.getExecutionType());
        assertEquals("Alsatia", inputArgs.getHostname());
        assertEquals("input-file-5M.txt", inputArgs.getPath());
        assertFalse(inputArgs.isHelp());
    }

    @Test
    public void parseArgs_WrongTypeException() {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                "test",
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        assertThrows(ParseException.class, () -> spyDataManager.parseArgs(args));
        verify(spyDataManager, times(1)).printHelp();
    }

    @Test
    public void parseArgs_noTypeException() {
        String[] args = new String[] {
                "-" + ShortInputArgNames.HOSTNAME.getValue(),
                "Alsatia",
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        assertThrows(ParseException.class, () -> spyDataManager.parseArgs(args));
        verify(spyDataManager, times(1)).printHelp();
    }

    @Test
    public void parseArgs_noHostnameException() {
        String[] args = new String[] {
                "-" + ShortInputArgNames.TYPE.getValue(),
                ExecutionTypes.LOOP.getValue(),
                "-" + ShortInputArgNames.PATH.getValue(),
                "input-file-5M.txt"
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        assertThrows(ParseException.class, () -> spyDataManager.parseArgs(args));
        verify(spyDataManager, times(1)).printHelp();
    }


    @Test
    public void parseArgs_Help() throws ParseException {
        String[] args = new String[] {
                "-" + ShortInputArgNames.HELP.getValue(),
        };
        InputArgsDataManager spyDataManager = spy(inputArgsDataManager);
        InputArgs inputArgs = spyDataManager.parseArgs(args);
        verify(spyDataManager, times(1)).printHelp();
        assertTrue(inputArgs.isHelp());
    }
}
