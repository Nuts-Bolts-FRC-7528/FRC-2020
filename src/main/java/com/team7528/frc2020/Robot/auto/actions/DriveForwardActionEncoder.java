package com.team7528.frc2020.Robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

@SuppressWarnings("FieldCanBeLocal")
public class DriveForwardActionEncoder implements Action {
    //Instantiating variables
    private final double kP = 0.07; // P constant
    private double error;
    private double movingPower; // Power of motors
    private double startTime; // FPGA timestamp at the start of the WaitAction
    private double turn_power;
    private int movingSeconds; // Time for moving
    /**
     * Constructor for DriveForwardActionEncoder
     *
     * @param power The power level of the drivetrain to drive at
     * @param seconds The amount of time to drive forward
     */
    public DriveForwardActionEncoder(double power, int seconds) {
        movingPower = power;
        movingSeconds = seconds;
    }
    /**
     * Returns if the desired amount of seconds has passed
     * 
     * @return If the desired amount of seconds has passed
     */
    @Override
    public boolean finished() {
        return (Timer.getFPGATimestamp() - startTime >= movingSeconds);
        // (current timestamp) - (starting time for action) is greater than or equal to (time to move)
    }
    /**
     * Adjusts to correct the velocity
     *
     */
    @Override
    public void update() {
        error = m_leftFront.getSelectedSensorVelocity() - m_rightFront.getSelectedSensorVelocity(); //gets the difference between the motor velocities
        turn_power = kP * error; //how much power to turn with
        if (turn_power >= 0.5) {
            turn_power = 0.5;
        } else if (turn_power <= -0.5) {
            turn_power = -0.5;
        }
        m_drive.arcadeDrive(movingPower, turn_power); //driving the robot
    }
    @Override
    public void done() {
        m_drive.setSafetyEnabled(true);
    }
    /**
     * Sets the startTime variable equal to current FPGA time
     */
    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        m_drive.setSafetyEnabled(false);
    }
}