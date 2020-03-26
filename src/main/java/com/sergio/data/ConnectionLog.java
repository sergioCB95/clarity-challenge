package com.sergio.data;

/**
 * Input connection log entity
 */
public class ConnectionLog {
    long timestamp = 0;
    String hostname1 = "";
    String hostname2 = "";

    public ConnectionLog() {};

    public ConnectionLog(long timestamp, String hostname1, String hostname2) {
        this.timestamp = timestamp;
        this.hostname1 = hostname1;
        this.hostname2 = hostname2;
    };

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostname1() {
        return hostname1;
    }

    public void setHostname1(String hostname1) {
        this.hostname1 = hostname1;
    }


    public String getHostname2() {
        return hostname2;
    }

    public void setHostname2(String hostname2) {
        this.hostname2 = hostname2;
    }

    @Override
    public String toString() {
        return "ConnectionLog{" +
                "timestamp=" + timestamp +
                ", hostname1='" + hostname1 + '\'' +
                ", hostname2='" + hostname2 + '\'' +
                '}';
    }
}
