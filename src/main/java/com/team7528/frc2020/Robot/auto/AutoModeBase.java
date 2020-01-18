package com.team7528.frc2020.Robot.auto;

import com.team7528.frc2020.Robot.auto.actions.Action;

/**
 * Defines a base for all automodes. All automodes extend this class
 */
public abstract class AutoModeBase implements Runnable {
    private boolean active = false;
    private double updateRate = 1.0 / 50.0;

    /**
     * Defines things to actually occour for the automode. All things the automode does lives in this method.
     * As it is an abstract method, routine() gets defined by all */
    protected abstract void routine() throws AutoModeDoneException;

    /**
     * Runs the automode. This is called in Robot.java in autonomousInit
     */
    public void run() {
        active = true; //Set the automode's active flag to true
        try {
            routine(); //Tries running the routine
        } catch(Exception e) {
            e.printStackTrace(); //If the routine has any error, print its stack trace
        }
        stop(); //After the routine is complete, it stops the program
        System.out.println("Auto mode complete");
    }

    /**
     * Accessor method for the active flag
     * @return Whether or not the current automode is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns if the automode is active or not, and throws an AutoModeDoneException if the automode is not done
     *
     * @return Whether or not the current automode is active
     * @throws AutoModeDoneException This exception is thrown if the automode is already done or the robot has shifted to teleop
     */
    private boolean isActiveThrowsException() throws AutoModeDoneException {
        if(!active) {
            throw new AutoModeDoneException();
        }

        return active;
    }

    /**
     * Runs each individual action. Automodes need to call this in order to carry out an action.
     * @param action The action that the automode is taking at the seconds (usually a new instance of that action, ie runAction(new [Action]())
     * @throws AutoModeDoneException This exception is thrown if the automode is already done or the robot has shifted to teleop
     */
    protected void runAction(Action action) throws AutoModeDoneException {
        isActiveThrowsException(); //Make sure the automode isn't done already
        action.start(); //Runs the Action's start() method

        while (isActiveThrowsException() &&  !action.finished()) { //While the action is NOT done and the automode IS currently active
            action.update(); //Run the Action's update() method
            long waitTime = (long) (updateRate * 1000.0);

            try {
                Thread.sleep(waitTime); //Attempts to put the thread to sleep
            } catch (Exception e) {
                e.printStackTrace(); //Print out any stack trace if necessary
            }
        }

        action.done(); //After the action is completed, run the Action's done() method
    }

    /**
     * Sets the automode's stop flag to false, preventing any more Actions from being executed
     */
    void stop() {
        active = false;
    }
}