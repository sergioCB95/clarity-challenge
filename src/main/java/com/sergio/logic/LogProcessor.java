package com.sergio.logic;

import com.sergio.data.ConnectionLog;
import com.sergio.data.manager.ConnectionLogDataManager;
import com.sergio.exception.InputFormatException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Input log file processor
 */
public class LogProcessor {

    long initDatetime;
    long endDatetime;
    String hostname;
    String path;

    ConcurrentHashMap<String, String> sendingConnectionHosts = new ConcurrentHashMap<String, String>();
    ConcurrentHashMap<String, String> receivingConnectionHosts = new ConcurrentHashMap<String, String>();
    ConcurrentHashMap<String, Long> sendingConnectionHostsCount = new ConcurrentHashMap<String, Long>();

    AtomicLong hostnameMaxCountValue = new AtomicLong(0L);
    AtomicReference<String> hostnameMaxCountKey = new AtomicReference<String>(null);


    /**
     * LogProcessor constructor. It sets all configurable parameters of the input log process
     * @param initDatetime
     * @param endDatetime
     * @param hostname
     * @param path
     */
    public LogProcessor(long initDatetime, long endDatetime, String hostname, String path) {
        this.initDatetime = initDatetime;
        this.endDatetime = endDatetime;
        this.hostname = hostname;
        this.path = path;
    }

    /**
     * Read input log file and get a map of hosts connected to the configured hostname in the configured timestamp range
     * @throws IOException
     * @throws NumberFormatException
     * @throws InputFormatException
     */
    public void processConnectedHostInfo() throws IOException, NumberFormatException, InputFormatException {
        getInputLinesStream()
                // Parse input log strings
                .map(getConnectionLogDataManager()::parseString)
                // filter logs between timestamp range
                .filter(this::checkTimestampRange)
                // filter logs connecting to configured hostname
                .filter(connectionLog -> connectionLog.getHostname2().equals(hostname))
                // adding hostnames to map
                .forEach(connectionLog -> sendingConnectionHosts.put(connectionLog.getHostname1(), connectionLog.getHostname1()));

        // Printing result
        printSendingConnectionHostInfo();
    }


    /**
     * Read input log file and get a map of hosts connected to and receiving connection from the configured hostname together with the host that performs more connections in a specific time range
     * @throws IOException
     */
    public void processAllHostInfo() throws IOException {
        getInputLinesStream()
                // Parse input log strings
                .map(getConnectionLogDataManager()::parseString)
                // filter logs between timestamp range
                .filter(this::checkTimestampRange)
                // Check if match any connection requirement and update host connections count
                .forEach(this::checkConnections);

        // Printing result
        printAllConectionHostInfo();
    }

    /**
     * Get a parallelized stream of lines from the input log file
     * @return
     * @throws IOException
     */
    Stream<String> getInputLinesStream() throws IOException {
        return Files.lines(Paths.get(path), StandardCharsets.UTF_8).parallel();
    }

    /**
     * Check if a log is between the configured timestamp range
     * @param connectionLog
     * @return
     */
    boolean checkTimestampRange(ConnectionLog connectionLog) {
        return connectionLog.getTimestamp() >= initDatetime && connectionLog.getTimestamp() <= endDatetime;
    }

    /**
     * check if a lig connects or receives connection from configured hostname and updates host connections count
     * @param connectionLog
     */
    void checkConnections(ConnectionLog connectionLog) {
        sendingConnectionHostsCount.compute(connectionLog.getHostname1(), this::updateHostCounts);
        if (connectionLog.getHostname1().equals(hostname)) {
            receivingConnectionHosts.put(connectionLog.getHostname2(), connectionLog.getHostname2());
        } else if (connectionLog.getHostname2().equals(hostname)) {
            sendingConnectionHosts.put(connectionLog.getHostname1(), connectionLog.getHostname1());
        }
    }

    /**
     * Update host connections count
     * @param host
     * @param count
     * @return
     */
    Long updateHostCounts(String host, Long count) {
        long result = count == null ? 1L : count + 1;
        if (hostnameMaxCountValue.get() < result) {
            hostnameMaxCountKey.set(host);
            hostnameMaxCountValue.set(result);
        }
        return result;
    }

    /**
     * Prints in console all hosts connected to the configured host
     */
    public void printSendingConnectionHostInfo() {
        System.out.println("Hosts connected from " + hostname);
        sendingConnectionHosts.forEach((key, value) -> System.out.println(key));
    }

    /**
     * Prints in console all hosts connected to or receiving connection from the configured host together with the host that established more connections
     */
    public void printAllConectionHostInfo() {
        System.out.println("Host with most connections: " + hostnameMaxCountKey + " with " + hostnameMaxCountValue);

        System.out.println("Hosts sending connection to " + hostname);
        sendingConnectionHosts.forEach((key, value) -> System.out.println(key));

        printSendingConnectionHostInfo();
    }

    public ConnectionLogDataManager getConnectionLogDataManager() {
        return ConnectionLogDataManager.getInstance();
    }


    public ConcurrentHashMap<String, String> getSendingConnectionHosts() {
        return sendingConnectionHosts;
    }

    public ConcurrentHashMap<String, String> getReceivingConnectionHosts() {
        return receivingConnectionHosts;
    }

    public ConcurrentHashMap<String, Long> getSendingConnectionHostsCount() {
        return sendingConnectionHostsCount;
    }

    public AtomicReference<String> getMaxConnectedHost() {
        return hostnameMaxCountKey;
    }

    public AtomicLong getMaxConnectedCount() {
        return hostnameMaxCountValue;
    }
}
