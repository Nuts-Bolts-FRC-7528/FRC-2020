package com.team7528.frc2020.Robot.auto;

/**
 * This class actually executes each automode. It gets instantiated each time the program wants to execute an automode
 */
public class AutoModeExecutor {
    private AutoModeBase autoMode;
    private Thread thread = null;

    /**
     * Constructor for the AutoModeExecutor that prepares it to run an AutoModeBase
     * @param mode AutoMode to run when autonomousInit() is called
     * @see AutoModeBase
     */
    public AutoModeExecutor(AutoModeBase mode) {
        autoMode = mode;
    }

    /**
     * Stops the automode by running the AutoModeBase's stop() method, preventing any more Actions from being executed
     */
    public void stop() {
        if(autoMode != null) {
            autoMode.stop();
        }
    }

    /**
     * Creates a new thread of the AutoModeBase and runs it
     */
    public void start() {
        if(thread == null) {
            thread = new Thread(autoMode);
            if(autoMode != null) {
                autoMode.run();
            }
            thread.start();
        }
    }
}
