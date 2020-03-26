package com.sergio.constant;

/**
 * Input args names
 */
public enum InputArgNames {
    HELP("help"),
    TYPE("type"),
    INIT_TIMESTAMP("initTimpestamp"),
    END_TIMESTAMP("endTimpestamp"),
    HOSTNAME("hostname"),
    PATH("path"),
    PERFORMANCE_METRICS("performanceMetrics");

    private String value;

    private InputArgNames (String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
