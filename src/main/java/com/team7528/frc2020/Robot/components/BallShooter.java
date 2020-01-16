package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

public class BallShooter {
    private static WPI_TalonSRX turretRotator = new WPI_TalonSRX(62);
    private static WPI_TalonSRX flywheelSpinner = new WPI_TalonSRX(63);
    private static XboxController xboxController = new XboxController(1);

    public static void init() {
        turretRotator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
        flywheelSpinner.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
    }

    public static void iterate() {

        int dPadDirection = xboxController.getPOV();
        double leftTrigger = xboxController.getTriggerAxis(GenericHID.Hand.kLeft);

        flywheelSpinner.set(ControlMode.PercentOutput,leftTrigger);
        turretRotator.set(ControlMode.PercentOutput,dPadDirection);

    }

}
