package com.team7528.frc2020.Robot.components;

/**
 * Skeleton abstract class for a component (ie subsystem) of the robot
 */
public interface Component {
    /**
     * Setup code for the component goes here. This method should be called in
     * autonomousInit() (at the start of the match) or in robotInit()
     */
    static void init() {
    }

    /**
     * Periodic logic for the component. This method should be called in
     * autonomousPeriodic() or teleopPeriodic()
     */
    static void periodic() {
    }

    /**
     * Reports statistics to the Driver Station (ie printouts, Shuffleboard,
     * etc)
     */
    static void reportStatistics() {
    }
}
