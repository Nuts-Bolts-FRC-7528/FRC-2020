package com.team7528.frc2020.Robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

/**
 * Does nothing for a given amount of seconds
 *
 * @see Action
 */
public class WaitAction implements Action {
    private double timeToWait; //Amount of time to wait in seconds
    private double startTime; //FPGA timestamp at the start of the WaitAction

    /**
     * Constructor for a new WaitAction
     *
     * @param seconds The amount of time to wait in seconds
     */
    public WaitAction(double seconds) {
        timeToWait = seconds;

    }

    /**
     * Finished() returns whether or not the program is done. In this case, it simply returns whether or not the amount
     * of seconds since the time we started the WaitAction is greater than or equal to the amount of time we told
     * it to wait for, or in layman's terms: if it's waited for the amount of time we want it to.
     *
     * @return If the amount of time we want the automode to wait for has passed
     */
    @Override
    public boolean finished() {
        return (Timer.getFPGATimestamp() - startTime >= timeToWait);
    }

    @Override
    public void update() {
        //Nothing, as no iterative logic is needed here
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp(); //Sets startTime to the current FPGA timestamp
    }
}