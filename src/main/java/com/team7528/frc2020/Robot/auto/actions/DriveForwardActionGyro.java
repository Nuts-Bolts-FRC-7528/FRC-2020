package com.team7528.frc2020.Robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

@SuppressWarnings("FieldCanBeLocal")
public class DriveForwardActionGyro implements Action {
    //Instantiating variables
    private double desiredGyroAngle; //Setpoint
    private double movingPower; //power of motors
    private int movingSeconds; //time for moving
    private final double kP = 0.07; //P constant
    private double error;
    private double turn_power;
    private double startTime; //FPGA timestamp at the start of the WaitAction
    /**
     * Constructor for DriveForwardActionGyro
     *
     * @param angle The angle of the gyroscope to turn to
     * @param power The power level of the drivetrain to drive at
     * @param seconds The amount of time to drive forward
     */
    public DriveForwardActionGyro(double angle, double power, int seconds) {
        desiredGyroAngle = angle;
        movingPower = power;
        movingSeconds = seconds;
    }
    /**
     *
     * @return If the desired amount of seconds has passed
     */
    @Override
    public boolean finished() {
        return (Timer.getFPGATimestamp() - startTime >= movingSeconds);
    }
    /**
     * Adjusts to correct the angle
     *
     */
    @Override
    public void update() {
        error = desiredGyroAngle - gyroScope.getAngle(); //the difference between the desired angle and the current angle
        turn_power = kP * error; //how much power to turn with
        m_drive.arcadeDrive(movingPower, turn_power); //driving the robot
    }
    @Override
    public void done() {

    }
    /**
     * Sets the startTime variable equal to current FPGA time
     */
    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
    }
}