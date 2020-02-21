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

    private boolean seek_r;
    private boolean seek_l;
    private boolean disengage;
    private double seek_adjust;

    /**
     * This code will be used to set up how much power will be use to spin and what buttons will be used to operate turret
     */

    public void periodic() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0); //
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0); //
        double kP = .50; // Value for kP variable

        // Setup for seeking right, left, and disengaging

        if (m_gamepad.getBButtonPressed()) { // right
            seek_r = true;
        }
        if (seek_r) {
            turretRotator.set(.20);
        }

        if (m_gamepad.getXButtonPressed()) { // left
            seek_l = true;
        }
        if (seek_l) {
            turretRotator.set(-.20);
        }

        if ((seek_r || seek_l) && tv == 1) {
            turretRotator.set(seek_adjust);
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