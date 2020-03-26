package com.sergio.constant;

/**
 * Input args names shortened
 */
public enum ShortInputArgNames {
    HELP("h"),
    TYPE("t"),
    INIT_TIMESTAMP("i"),
    END_TIMESTAMP("e"),
    HOSTNAME("hn"),
    PATH("p"),
    PERFORMANCE_METRICS("pm");

    private String value;

    private ShortInputArgNames (String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
