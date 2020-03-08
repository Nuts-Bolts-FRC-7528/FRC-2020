package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * This class controls the PowerCell intake and everything associated with it
 *
 * @author Robert Garcia
 */
@SuppressWarnings("FieldCanBeLocal")
public class PowerCellIntake {
    private static double error;
    private static double previousError;
    private static final double k_distance = 0.0; // Constant for the flywheel speed
    private static final double kD = 0.0; // D constant for PID loop
    private static final double k_gearRatio = 1 / 4.0; // The gear ratio for the flywheel
    private static final double kI = 0.0; // I constant for PID loop
    private static final double kP = 0.0; // P constant for PID loop
    private static final double kF = 0.0;
    private static final double k_speedToRPM = 600 / 360.0; // Conversion from the speed parameter to RPM
    private static double currentVelocity; // current velocity
    public static double d; // The distance to the target
    private static double desiredVelocity; // The name states its purpose, READ!
    private static double errorRate; // The rate of change for the error
    private static double errorSum; // The sum of the errors
    private static double speed; // The speed to set for the flywheel motor
    private static boolean isReadingBall = false;
    private static NetworkTableEntry intakeToleranceEntry = Shuffleboard.getTab("DRIVETRAIN").add("In intake tolerance",false).getEntry();

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
            PID();

            //intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput,-speed); //moves wheels to intake balls
            Flywheel.runConveyor(); //Move balls up the trough

            if(!ballCounter.get() && !isReadingBall) { //If ball sensor is reading ball being intaked
                powerCellCount++; //Increase power cell count by one
                isReadingBall = true; //Prevent reading multiple power cells at once
            }
        }
        if(m_gamepad.getBumper(GenericHID.Hand.kLeft)) { //if left bumper is pressed
            PID();

            //intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput, speed); //moves wheel to eject balls

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
        if (Math.abs(desiredVelocity - currentVelocity) <= 100) { //If we're within tolerance ...
            intakeToleranceEntry.setBoolean(true); //... set intakeToleranceEntry to true
        } else { //If we're not within tolerance ...
            intakeToleranceEntry.setBoolean(false); //... set intakeToleranceEntry to false
        }
    }
    private static void PID() {
        currentVelocity = intakeMotor.getSelectedSensorVelocity();
        error = (desiredVelocity - currentVelocity) / k_speedToRPM;
        if (Math.abs(error) <= 1000) { // If the error is less than or equal to 500 ...
            errorSum += error * 0.02; // Add it to the error sum
        }
        if (errorSum >= 1000) { // If the error sum gets too high ...
            errorSum = 1000; // ... set it to the maximum
        } else if (errorSum <= -2000) {
            errorSum = -2000;
        }
        errorRate = (error - previousError) / 0.02; //Change in difference over change in time
        speed = (kF * desiredVelocity + kP * error + kI * errorSum + kD * errorRate);
        desiredVelocity = speed * d * k_distance / k_gearRatio; // Sets the desired RPM
        previousError = error;
    }

    public static void init() {

    }

    public static void reportStatistics() {
        //System.out.println("Intake Motor Power Level " + intakeMotor.get());
    }
}
