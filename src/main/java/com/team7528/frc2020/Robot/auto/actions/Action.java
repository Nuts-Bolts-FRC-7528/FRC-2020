package com.team7528.frc2020.Robot.auto.actions;

/**
 * This interface sets the base methods that an Action is required to have. All Actions must override the methods in
 * this interface
 */
public interface Action {
    /**
     * Returns whether or not the action is finished
     */
    boolean finished();

    /**
     * Called by runAction in AutoModeBase iteratively until finished is true. Iterative logic goes into this method
     */
    void update();

    /**
     * Runs code when the action completes
     */
    void done();

    /**
     * Runs code when the action starts
     */
    void start();
}