package com.sergio.constant;

/**
 * Execution types enum
 */
public enum ExecutionTypes {

    TIME_RANGE("time-range"),
    LOOP("loop");

    private String value;

    private ExecutionTypes (String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExecutionTypes parse(String executionType) {
        if(TIME_RANGE.getValue().equals(executionType)) {
            return TIME_RANGE;
        } else if (LOOP.getValue().equals(executionType)) {
            return LOOP;
        } else return null;
    }

    public static String info() {
        return TIME_RANGE.getValue() + ": script processes logs between a timestamp range. --initTimestamp and --endTimestamp are mandatories. \n" +
                LOOP.getValue() + ": script runs every hour processing last hour generated logs (based on its timestamp). " +
                "This process will last until it is explicitly stopped";
    }
}
