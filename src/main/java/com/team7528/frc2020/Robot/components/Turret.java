package com.team7528.frc2020.Robot.components;

import edu.wpi.first.networktables.NetworkTableInstance;
import static com.team7528.frc2020.Robot.common.RobotMap.m_gamepad;
import static com.team7528.frc2020.Robot.common.RobotMap.turretRotator;

/**
 * @author Luis Carrillo
 *
 * Code will operate by spinning either left or right until a target is detected
 */

public class Turret { // a class meant for the turret rotation

    private static StringBuilder stats = new StringBuilder();
    private static double currentRPM;
    private static int loopCount;
    private static double desiredRPM;
    private static double kP;
    private static double seek_adjust;
    private boolean seek_r;
    private boolean seek_l;
    private boolean disengage;

    public void init() {
        loopCount = 0; // Resets the loopCount
        desiredRPM = 60; // Sets the desired RPM
        kP = .50;

    }

    /**
     * This code will be used to set up how much power will be use to spin and what buttons will be used to operate turret
     */

    public void periodic() {

        currentRPM = turretRotator.getSelectedSensorPosition();
        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);

        // Setting up the rotation of turret

        if (currentRPM == desiredRPM) {
            if (tv == 1) {
                turretRotator.stopMotor();
            } else if (tv == 0) {
                turretRotator.configFactoryDefault();
            }
            loopCount++; // Increments loopCount
            if (loopCount >= 10) {
                Component.reportStatistics(); // Reports statistics
                loopCount = 0; // Reset the loopCount
            }

            // Setup for seeking right, left, and disengaging

            if (m_gamepad.getBButtonPressed()) { // right
                seek_r = true;
            }
            if (seek_r) {
                turretRotator.set(.90);
            }

            if (m_gamepad.getXButtonPressed()) { // left
                seek_l = true;
            }
            if (seek_l) {
                turretRotator.set(-.90);
            }

            if ((seek_r || seek_l) && tv == 1) {
                seek_adjust = kP * tx;
            }

            if (m_gamepad.getAButtonPressed()) { // disengage
                disengage = true;
                seek_r = false;
                seek_l = false;
            }
            if (disengage) {
                turretRotator.stopMotor();
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