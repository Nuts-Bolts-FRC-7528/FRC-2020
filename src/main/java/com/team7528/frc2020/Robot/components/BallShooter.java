package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.GenericHID;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

public class BallShooter extends Component  {

    @Override
    public void init() {
        turretRotator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
        flywheelSpinner.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
    }

    @Override
    public void periodic() {

    }

    @Override
    public void reportStatistics() {
        turretRotator.set(ControlMode.PercentOutput,m_gamepad.getX(GenericHID.Hand.kRight));
        hood.set(ControlMode.PercentOutput,m_gamepad.getY(GenericHID.Hand.kRight));
        flywheelSpinner.set(ControlMode.PercentOutput,m_gamepad.getTriggerAxis(GenericHID.Hand.kRight));
    }
}
