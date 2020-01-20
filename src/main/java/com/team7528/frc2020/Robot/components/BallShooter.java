package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.GenericHID;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * This class controls the power cell shooter of our robot
 */
public class BallShooter implements Component  {

    /**
     * Initialize flywheel + turret encoders
     */
    public static void init() {
        turretRotator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
        flywheelSpinner.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
    }


    /**
     * Handles periodic logic for power cell shooter
     */
    public static void periodic() {
        turretRotator.set(ControlMode.PercentOutput,m_gamepad.getX(GenericHID.Hand.kRight));
        hood.set(ControlMode.PercentOutput,m_gamepad.getY(GenericHID.Hand.kRight));
        flywheelSpinner.set(ControlMode.PercentOutput,m_gamepad.getTriggerAxis(GenericHID.Hand.kRight));
    }

    /**
     * Reports statistics for this component
     */
    public static void reportStatistics() {
    }
}
