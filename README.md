# Clarity BackEnd Code Challlenge

This application solves the Clarity's backend code challenge.

##Project Description

This application is in charge of processing a log file about connections between hosts and extracting certain information about them.

The structure of the input file logs is:

\<timestamp\> \<hostname1\> \<hostname2\>

E.g: 1565647204351 Aadvik Matina

The application has two execution modes:

1. time-range: given a specified time range, all file logs generated within that time range will be processed. From these logs we will obtain:
    * List of hosts that have requested a connection to a particular host.
2. loop: the file will be processed every hour, filtering by the logs generated this last period of time. From these logs we will obtain:
    * List of hosts that have requested a connection to a particular host.
    * List of hosts that have received a connection from a given host.
    * Host that has requested connection a greater number of times to other hosts.
    
For simplicity, the results obtained will be displayed on the console.

##Running Application

To compile and run the application you need to install: 
1. Java JDK (https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) and add it to the path.
2. Maven (https://maven.apache.org/download.cgi) and add it to the path (https://maven.apache.org/install.html).

Then move to the root dir of the project and run `mvn clean install` wich will build the project.

A folder called target will be generated. Inside of it there will be a clarity\<version\>.jar file with the compiled project.

Then, you just need to run `java -jar clarity<version>.jar <args>` and the application will be executed.

## App Arguments

To run the application, you need to specify a series of input arguments.

| Long Name                    | Short name       | Required | Description                                                                                                                             |
| -----------------------------|------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `--help`                     | `-h`             | No                            | List all arguments and their descriptions                                                                          |
| `--type <arg>`               | `-t <arg>`       | Yes                           | Type of execution, valid values are `time-range` and `loop`                                                        |
| `--hostname <arg>`           | `-hn <arg>`      | Yes                           | Hostname analyzed in the input file logs                                                                           |
| `--path <arg>`               | `-p <arg>`       | Yes                           | Input file path                                                                                                    |
| `--initTimestamp <arg>`      | `-i <arg>`       | Only with `--type time-range` | Lower limit of the time range of the processed input logs                                                          |
| `--endTimestamp <arg>`       | `-e <arg>`       | Only with `--type time-range` | Upper limit of the time range of the processed input logs                                                          |
| `--performanceMetrics`       | `-pm`            | No                            | If checked, some metrics about memory consumption and execution time will be recorded and printed with the results |

Some valid input argument examples are:
* `java -jar clarity.jar -t time-range -i 1545647204351 -e 1575647204351 -hn Alsatia -p input-file-10000.txt` 
 (It will process al logs between the time range 1545647204351 - 1575647204351 related to host Alsatia in the input file input-file-10000.txt)
* `java -jar clarity.jar -t loop -hn Alsatia -p input-file-10000.txt` (It will process every hour all logs generated that period related to host Alsatia in the input file input-file-10000.txt)
* `java -jar clarity.jar -t loop -hn Alsatia -p input-file-10000.txt -pm` (It will do the same as in the last case but it will also print some performance metrics)
* `java -jar clarity.jar -h` (It will print a list of arguments and its description)
* `java -jar clarity.jar` (the same as with -h)