package com.team7528.frc2020.Robot.auto;

/**
 * This exception gets raised if the AutoMode is already completed in the middle of performing it.
 * It gets raised by the isActiveThrowsException() method in AutoModeBase
 * If you're seeing this exception in your code, it's possible you're taking too long - recall that sandstorm only lasts 15 seconds
 *
 * @see AutoModeBase
 */
public class AutoModeDoneException extends Exception {
    private static final long serialVersionUID = 1L; //This is like a version number of sorts
    //While the above line is not entirely necessary, as Exception implements Serializable it should be defined

    public AutoModeDoneException() {
        super("Auto mode already completed"); //This calls Exception's constructor, which will print out this as the stack trace
    }
}