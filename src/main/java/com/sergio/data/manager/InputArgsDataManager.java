package com.sergio.data.manager;

import com.sergio.constant.ExecutionTypes;
import com.sergio.constant.InputArgNames;
import com.sergio.constant.ShortInputArgNames;
import com.sergio.data.InputArgs;
import org.apache.commons.cli.*;

/**
 * Input args entity data manager
 */
public class InputArgsDataManager {

    private static InputArgsDataManager inputArgsDataManager = null;
    private Options options = new Options();

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    private InputArgsDataManager() {

        // Help option
        Option help = new Option(
                ShortInputArgNames.HELP.getValue(),
                InputArgNames.HELP.getValue(),
                false,
                "Prints this message"
        );
        options.addOption(help);

        // Execution type option
        Option type = new Option(
                ShortInputArgNames.TYPE.getValue(),
                InputArgNames.TYPE.getValue(),
                true,
                "Select type of execution:\n" + ExecutionTypes.info()
        );
        options.addOption(type);

        // Hostname option
        Option hostname = new Option(
                ShortInputArgNames.HOSTNAME.getValue(),
                InputArgNames.HOSTNAME.getValue(),
                true,
                "Hostname filtered"
        );
        options.addOption(hostname);

        // File path option
        Option path = new Option(
                ShortInputArgNames.PATH.getValue(),
                InputArgNames.PATH.getValue(),
                true,
                "Path of input file"
        );
        options.addOption(path);

        // Init timestamp option
        Option initTimestamp = new Option(
                ShortInputArgNames.INIT_TIMESTAMP.getValue(),
                InputArgNames.INIT_TIMESTAMP.getValue(),
                true,
                "(long) Initial timestamp filtered"
        );
        options.addOption(initTimestamp);

        // End timestamp option
        Option endTimestamp = new Option(
                ShortInputArgNames.END_TIMESTAMP.getValue(),
                InputArgNames.END_TIMESTAMP.getValue(),
                true,
                "(long) End timestamp filtered"
        );
        options.addOption(endTimestamp);

        // Performance metrics option
        Option performance = new Option(
                ShortInputArgNames.PERFORMANCE_METRICS.getValue(),
                InputArgNames.PERFORMANCE_METRICS.getValue(),
                false,
                "If checked, some performance metrics will be measured"
        );
        options.addOption(performance);
    }

    /**
     * Get Singleton instance
     * @return
     */
    public static InputArgsDataManager getInstance() {
        if (inputArgsDataManager == null) {
            inputArgsDataManager = new InputArgsDataManager();
        }
        return inputArgsDataManager;
    }

    /**
     * Parse input arguments
     * @param args
     * @return
     * @throws ParseException
     */
    public InputArgs parseArgs(String[] args) throws ParseException {
        InputArgs inputArgs = new InputArgs();

        try {
            // Parse options
            cmd = parser.parse(options, args);

            // if help, print help and stop
            if (cmd.hasOption(InputArgNames.HELP.getValue()) || cmd.getOptions().length == 0) {
                inputArgs.setHelp(true);
                printHelp();
            } else {
                // Check required options
                if (!(
                        cmd.hasOption(InputArgNames.TYPE.getValue())
                                && cmd.hasOption(InputArgNames.HOSTNAME.getValue())
                                && cmd.hasOption(InputArgNames.PATH.getValue())
                )) {
                    throw new ParseException("--" + InputArgNames.TYPE.getValue()
                            + " --" + InputArgNames.HOSTNAME.getValue()
                            + " and --" + InputArgNames.HOSTNAME.getValue() + " need to be specified");
                }

                // Get type and check is correct execution type
                String type = cmd.getOptionValue(InputArgNames.TYPE.getValue());
                ExecutionTypes executionType = ExecutionTypes.parse(type);

                if (executionType == null) {
                    throw new ParseException("--" + InputArgNames.TYPE.getValue() + " value is not valid");
                }

                // if type == TIME_RANGE, check init and end timestamps
                if (executionType.equals(ExecutionTypes.TIME_RANGE) &&
                        !(cmd.hasOption(InputArgNames.INIT_TIMESTAMP.getValue())
                                && cmd.hasOption(InputArgNames.END_TIMESTAMP.getValue()))) {
                    throw new ParseException("--" + InputArgNames.TYPE.getValue() + " " + ExecutionTypes.TIME_RANGE.getValue()
                            + " requires --" + InputArgNames.INIT_TIMESTAMP.getValue()
                            + " and --" + InputArgNames.END_TIMESTAMP.getValue() + " args");
                }

                // Set args in input args entity
                inputArgs.setExecutionType(executionType);
                inputArgs.setHostname(cmd.getOptionValue(InputArgNames.HOSTNAME.getValue()));
                inputArgs.setPath(cmd.getOptionValue(InputArgNames.PATH.getValue()));
                inputArgs.setInitTimestamp(
                        cmd.hasOption(InputArgNames.INIT_TIMESTAMP.getValue())
                                ? Long.parseLong(cmd.getOptionValue(InputArgNames.INIT_TIMESTAMP.getValue()))
                                : 0
                );
                inputArgs.setEndTimestamp(
                        cmd.hasOption(InputArgNames.END_TIMESTAMP.getValue()) ?
                                Long.parseLong(cmd.getOptionValue(InputArgNames.END_TIMESTAMP.getValue()))
                                : 0
                );
                inputArgs.setPerformanceMetrics(cmd.hasOption(InputArgNames.PERFORMANCE_METRICS.getValue()));
            }
        } catch (ParseException | NumberFormatException e) {
            // If error, print help and throw exception
            printHelp();
            throw e;
        }
        return inputArgs;
    }

    /**
     * Prints help about script valid arguments
     */
    public void printHelp(){
        formatter.printHelp("clarity", options);
    }
}
