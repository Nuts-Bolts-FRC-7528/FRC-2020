package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * This class controls the PowerCell intake and everything associated with it
 *
 * @author Robert Garcia
 */
public class PowerCellIntake {
    private static double error;
    private static double previousError;
    private static final double k_distance = 0.07; // Constant for the flywheel speed
    private static final double kD = 0.07; // D constant for PID loop
    private static final double k_gearRatio = 1 / 4.0; // The gear ratio for the flywheel
    private static final double kI = 0.07; // I constant for PID loop
    private static final double kP = 0.07; // P constant for PID loop
    private static final double k_speedToRPM = 600 / 360.0; // Conversion from the speed parameter to RPM
    private static double currentVelocity; // current velocity
    public static double d; // The distance to the target
    private static double desiredVelocity; // The name states its purpose, READ!
    private static double errorRate; // The rate of change for the error
    private static double errorSum; // The sum of the errors
    private static double speed; // The speed to set for the flywheel motor
    private static boolean isReadingBall = false;

    public static int powerCellCount = 0;
    /**
     * This is the general code for the ball intake, eject, and raising/lowering the piston
     */
    public static void periodic() {
        //if ballCounter is false then a ball has come in
        if (!ballCounter.get()) {
            powerCellCount ++;
        }
        if(m_gamepad.getBumper(GenericHID.Hand.kRight)) { //if right bumper is pressed
            //intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput,-1); //moves wheels to intake balls

            if(!ballCounter.get() && !isReadingBall) { //If ball sensor is reading ball being intaked
                powerCellCount++; //Increase power cell count by one
                isReadingBall = true; //Prevent reading multiple power cells at once
            }
        }
        if(m_gamepad.getBumper(GenericHID.Hand.kLeft)) { //if left bumper is pressed
            //intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput, 1); //moves wheel to eject balls

            if(!ballCounter.get() && !isReadingBall) { //If ball sensor is reading ball being outtaked
                powerCellCount--; //Subtract power cell count by one
                isReadingBall = true; //Prevent reading multiple power cells at once
            }
        }
        if(m_gamepad.getYButton()) { //if Y button is pressed
            //intake.set(DoubleSolenoid.Value.kForward); //raises piston
            intakeMotor.set(ControlMode.PercentOutput, 0); //does not move wheels

        }
        
        if(ballCounter.get() && isReadingBall) {
            isReadingBall = false;
        }
    }
    private static void PID() {
        currentVelocity = intakeMotor.getSelectedSensorVelocity();
        error = (desiredVelocity - currentVelocity) / k_speedToRPM;
        if (Math.abs(error) <= 500) { // If the error is less than or equal to 500 ...
            errorSum += error; // Add it to the error sum
        }
        if (errorSum >= 1201) { // If the error sum gets too high ...
            errorSum = 1200; // ... set it to the maximum
        } else if (errorSum <= -2000) {
            errorSum = -2000;
        }
        errorRate = (error - previousError) / 0.02;
        speed = (kP * error + kI * errorSum + kD * errorRate);
        desiredVelocity = speed * d * k_distance / k_gearRatio; // Sets the desired RPM
        previousError = error;
    }
    public static void init() {

    }

    public static void reportStatistics() {
        //System.out.println("Intake Motor Power Level " + intakeMotor.get());
    }
}
