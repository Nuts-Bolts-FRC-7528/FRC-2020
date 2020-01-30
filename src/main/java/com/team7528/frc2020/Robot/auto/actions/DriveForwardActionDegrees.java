package com.team7528.frc2020.Robot.auto.actions;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

@SuppressWarnings("FieldCanBeLocal")
public class DriveForwardActionDegrees implements Action {

    private double degreesToMove; // How many ticks to move
    private final double degreesToInches = 1.0 / 360 * 1.0 / 4.0 * 6;
    // Annotation for degrees to inches: 1 rotation / 360 degrees * gear ratio * wheel size

    private final double kP = 0.07; // P constant
    private final double kI = 0.07; // I constant
    private final double kD = 0.07; // D constant
    private final double kP2 = 0.07; // P constant for encoder velocity

    private int degreesMoved; // How many ticks we have moved

    private int previousPositionLeft; // The previous encoder position on the left
    private int previousPositionRight; // The previous encoder position on the right

    private double speed; // The speed to move at
    private double turn_power; // The speed to turn at

    private double error; // The distance between where we are and where we want to be
    private double previousError; // The previous iteration's error
    private double error2; // The difference between the encoder velocities

    private double check; // Integrator zone

    /**
     * Converts inches parameter to degrees
     *
     * @param inches How far we want to go, in inches
     */
    public DriveForwardActionDegrees(double inches) {
        degreesToMove = inches / degreesToInches;
    }

    @Override
    public boolean finished() {
        return error == 0; // Returns true when we have moved how far we wanted to
    }

    @Override
    public void update() {
        previousError = error; // set previousError to error

        if (previousError >= Math.abs(6 / degreesToInches)) { // If the previousError is greater than or equal to 1 ...
            check = 0; // ... set check to 0
        } else {
            check = previousError;
        }

        error = degreesToMove - degreesMoved; // Sets the error equal to how far we want to have moved minus how far we have moved

        speed = kP * error + kI * check + kD * ((error - previousError) / 0.02); // Sets the speed based off the error & the previous error

        degreesMoved += ((m_leftFront.getSelectedSensorPosition() - previousPositionLeft) + (m_rightFront.getSelectedSensorPosition() - previousPositionRight)) / 2;

        previousPositionLeft = m_leftFront.getSelectedSensorPosition(); // Gets the previous left encoder position
        previousPositionRight = m_rightFront.getSelectedSensorPosition(); // Gets the previous right encoder position

        error2 = m_leftFront.getSelectedSensorVelocity() - m_rightFront.getSelectedSensorVelocity(); //gets the difference between the motor velocities
        turn_power = kP2 * error2; //how much power to turn with

        m_drive.arcadeDrive(speed, turn_power); // Drives the robot


    }

    @Override
    public void done() {

    }

    @Override
    public void start() {
        // Resetting the encoders
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);
    }
}