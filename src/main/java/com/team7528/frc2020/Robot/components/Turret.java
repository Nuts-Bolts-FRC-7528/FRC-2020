package com.team7528.frc2020.Robot.components;

import edu.wpi.first.networktables.NetworkTableInstance;
import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Code will operate by spinning either left or right until a target is detected
 */

public class Turret {

    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics
    private static double currentRPM;
    private static int loopCount;
    private static double desiredRPM;
    private boolean seek_r;
    private boolean seek_l;
    private boolean disengage;

    public void init() {
        loopCount = 0; // Resets the loopCount
        desiredRPM = 1000; // Sets the desired RPM
    }

    /**
     * This code will be used to set up how much power will be use to spin and what buttons will be used to operate turret
     */

    public void periodic() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);

        // Setting up the rotation of turret

        if (currentRPM == desiredRPM) {
            if (tv == 1) {
                drumSpinner.configFactoryDefault();
            } else if (tv == 0) {
                drumSpinner.getMotorOutputPercent();
            }
            loopCount++; // Increments loopCount
            if (loopCount >= 10) {
                reportStatistics(); // Reports statistics
                loopCount = 0; // Reset the loopCount
            }

            // Setup for seeking right, left, and disengaging

            if (m_gamepad.getBButtonPressed()) { // right
                seek_r = true;
            }

            if (seek_r) {
                drumSpinner.configFactoryDefault();
            } else {
                drumSpinner.setInverted(true);
            }

            if (m_gamepad.getXButtonPressed()) { // left
                seek_l = true;
            }

            if (seek_l) {
                drumSpinner.configFactoryDefault();
            } else {
                drumSpinner.setInverted(true);
            }

            if (m_gamepad.getAButtonPressed()) { // disengage
                disengage = true;
            }

        }

    }

    // printing out statistics

    public static void reportStatistics() {
        stats.append("Current turret RPM: ").append(currentRPM);
        System.out.println();
        stats.setLength(0);
    }

}