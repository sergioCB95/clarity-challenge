package com.sergio;

import com.sergio.data.InputArgs;
import com.sergio.logic.ProcessDispatcher;
import com.sergio.data.manager.InputArgsDataManager;


/**
 * App class
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            // Getting input arguments
            InputArgs inputArgs = InputArgsDataManager
                    .getInstance()
                    .parseArgs(args);

            if (!inputArgs.isHelp()) {
                // Dispatching process
                new ProcessDispatcher(inputArgs).dispatch();
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
