package com.team7528.frc2020.Robot.auto.actions;

import static com.team7528.frc2020.Robot.auto.actions.DriveForwardActionGyro.ypr;
import static com.team7528.frc2020.Robot.common.RobotMap.gyroScope;
import static com.team7528.frc2020.Robot.common.RobotMap.m_drive;

/**
 * Turns to a certain angle
 */
@SuppressWarnings("FieldCanBeLocal")
public class OneWheelTurnAction implements Action {

    private final double kP = 0.07; // P constant
    private double desiredAngle; // The angle we want
    private double error;  // The difference between the desired angle and the difference between the current angle and the starting angle
    private double startingAngle; // The angle the gyro starts with
    private double turn_power; // How much power to turn with

    public OneWheelTurnAction(double angle) {
        desiredAngle = angle;
    }

    @Override
    public boolean finished() {
        return Math.abs(gyroScope.getYawPitchRoll(ypr).value - startingAngle + 1) >= desiredAngle; // 1 degree of tolerance
    }

    @Override
    public void update() {
        error = desiredAngle - (gyroScope.getYawPitchRoll(ypr).value - startingAngle);
        turn_power = kP * error; //how much power to turn with
        m_drive.arcadeDrive(0, turn_power); //driving the robot
    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        ypr = new double[]{0.0, 0.0, 0.0};
        startingAngle = gyroScope.getYawPitchRoll(ypr).value;
    }
}
