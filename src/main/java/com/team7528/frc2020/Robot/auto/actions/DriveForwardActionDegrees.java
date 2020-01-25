package com.team7528.frc2020.Robot.auto.actions;

import static com.team7528.frc2020.Robot.common.RobotMap.m_leftFront;
import static com.team7528.frc2020.Robot.common.RobotMap.m_rightFront;

@SuppressWarnings("FieldCanBeLocal")
public class DriveForwardActionDegrees implements Action {

    private double degreesToMove; // How many ticks to move
    private final double inchesToDegrees = 1.0 / 360 * 1.0 / 4.0 * 6;
    // Annotation for inches to degrees: 1 rotation / 360 degrees * gear ratio * wheel size
    private final double kP = 0.07; // P constant
    private final double kI = 0.07; // I constant
    private final double kD = 0.07; // D constant
    private int degreesMoved; // How many ticks we have moved
    private int previousPositionLeft; // The previous encoder position on the left
    private int previousPositionRight; // The previous encoder position on the right
    private double speed; // The speed to move at
    private double error; // The distance between where we are and where we want to be
    private double previousError; // The previous iteration's error
    private double check;

    /**
     * Converts inches parameter to degrees
     *
     * @param inches How far we want to go, in inches
     */
    public DriveForwardActionDegrees(double inches) {
        degreesToMove = inches * inchesToDegrees;
    }

    @Override
    public boolean finished() {
        return error == 0; // Returns true when we have moved how far we wanted to
    }

    @Override
    public void update() {
        if (error == 5) { // Checks to see if we just started ...
            previousError = 5; // and sets previousError to our desired distance
        } else if (error == 0) { //Checks to see if we are there ...
            previousError = 0; // and sets previousError to zero
        } else { // Otherwise ...
            previousError = error; // set previousError to error
        }
        if (previousError >= 1) { // If the previousError is greater than or equal to 1 ...
            check = 0; // Set check to 0
        } else if (previousError <= -1) { // If the previousError is less than or equal to -1 ...
            check = 0; // Set check to 0
        } else {
            check = previousError;
        }
        error = degreesToMove - degreesMoved; // Sets the error equal to how far we want to have moved minus how far we have moved
        speed = kP * error + kI * check + kD * (error - previousError) / 2; // Sets the speed based off the error & the previous error
        degreesMoved += ((m_leftFront.getSelectedSensorPosition() - previousPositionLeft) + (m_rightFront.getSelectedSensorPosition() - previousPositionRight)) / 2;
        previousPositionLeft = m_leftFront.getSelectedSensorPosition(); // Gets the previous left encoder position
        previousPositionRight = m_rightFront.getSelectedSensorPosition(); // Gets the previous right encoder position
        m_leftFront.set(speed); // Drives the robot
        m_rightFront.set(speed); // Drives the robot


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