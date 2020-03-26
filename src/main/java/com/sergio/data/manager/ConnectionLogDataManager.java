package com.sergio.data.manager;

import com.sergio.data.ConnectionLog;
import com.sergio.exception.InputFormatException;

/**
 * Input connection logs entity data manager
 */
public class ConnectionLogDataManager {

    private static ConnectionLogDataManager dataManager;

    private ConnectionLogDataManager() {};

    /**
     * Get Singleton instance
     * @return
     */
    public static ConnectionLogDataManager getInstance() {
        if (dataManager == null) {
            dataManager = new ConnectionLogDataManager();
        }
        return dataManager;
    }

    /**
     * Parse input connection log String
     * @param input
     * @return
     */
    public ConnectionLog parseString(String input) {
        String[] inputFields = input.split(" ");
        if(inputFields.length != 3) {
            throw new InputFormatException("input log " + input + " does not have the proper format");
        }
        return new ConnectionLog(Long.parseLong(inputFields[0]), inputFields[1], inputFields[2]);
    }
}
