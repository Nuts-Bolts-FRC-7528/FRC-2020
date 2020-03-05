package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * @author Luis Carrillo
 *
 * Code will operate by spinning either left or right until a target is detected
 */

public class Turret { // a class meant for the turret rotation

    private static boolean seek_r;
    private static boolean seek_l;
    private static boolean disengage = true;
    private static double seek_adjust;

    /**
     * This code will be used to set up how much power will be use to spin and what buttons will be used to operate turret
     */

    public static void periodic() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0); //
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0); //
        double sensorPosition = turretRotator.getSelectedSensorPosition();
        double kP = .50; // Value for kP variable
        SmartDashboard.putBoolean("seek_l",seek_l);
        SmartDashboard.putBoolean("seek_r",seek_r);

        // Setup for seeking right, left, and disengaging
        //Changed to use R Bumper instead of X - Grace

        //turretRotator.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));

//        if (m_gamepad.getBButtonPressed()) {
//            seek_r = true;
//            seek_l = false;
//            disengage = false;
//        }
//
//        if (seek_r) {
//            turretRotator.set(ControlMode.PercentOutput,.10);
//        }
//
//        if (m_gamepad.getXButtonPressed()) { // left
//            seek_l = true;
//            seek_r = false;
//            disengage = false;
//        }
//        if (seek_l) {
//            turretRotator.set(ControlMode.PercentOutput,-.10);
//        }
//
//        if (m_gamepad.getAButtonPressed()) { // disengage
//            disengage = true;
//            seek_r = false;
//            seek_l = false;
//        }
//
//        if (disengage) {
//            turretRotator.set(ControlMode.PercentOutput,0);
//        }

//        if (m_gamepad.getBButtonPressed()) {
//            turretRotator.set(ControlMode.PercentOutput,.10);
//        }
//
//        if (m_gamepad.getXButtonPressed()) {
//            turretRotator.set(ControlMode.PercentOutput,-.10);
//        }
//
//        if (m_gamepad.getAButtonPressed()) {
//
//        }

        if ((seek_r || seek_l) && tv == 1) {
            turretRotator.set(seek_adjust);
            seek_adjust = kP * tx;
        }

        if (sensorPosition <= -5000) {
            turretRotator.set(0);
        }
        if (sensorPosition <= 5000) {
            turretRotator.set(0);
        }
    }

    public static void init() {
        turretRotator.configFactoryDefault();
        turretRotator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        turretRotator.setSelectedSensorPosition(0,0,10);
    }
}