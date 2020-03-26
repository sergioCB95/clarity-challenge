package com.sergio.data;

import com.sergio.constant.ExecutionTypes;

/**
 * Input args entity
 */
public class InputArgs {

    private long initTimestamp = 0L;
    private long endTimestamp = 0L;
    private String hostname = "";
    private String path = "";
    private boolean performanceMetrics = false;
    private ExecutionTypes executionType = null;
    private boolean help = false;

    public long getInitTimestamp() {
        return initTimestamp;
    }

    public void setInitTimestamp(long initTimestamp) {
        this.initTimestamp = initTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isPerformanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(boolean performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ExecutionTypes getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionTypes executionType) {
        this.executionType = executionType;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }
}
