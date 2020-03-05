package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the flywheel component. Also handles calculating the distance to the target using a Limelight.
 * Note that the flywheel encoder velocity has to be reversed each time we use it, as it is out of phase with
 * the direction it is actually moving in. The setPhase() method appears to do nothing in this regard, so we
 * simply need to invert it each time it is used.
 *
 * @author Matthew Correia
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class Flywheel {

    /*   [DISTANCE CONSTANTS]   */
    private static final double a1 = 37.5; //The angle the limelight is mounted at
    private static final double h1 = 30; //The height the limelight is mounted at
    private static final double h2 = 98.25; //The height of the target

    /*   [FLYWHEEL PIDF CONSTANTS]   */
    private static final double kP = 0.0001; //P constant for PID loop
    private static final double kI = 0.007; //I constant for PID loop
    private static final double kD = 0.0; //D constant for PID loop
    private static final double kF = 0.005; //Feedforward Setpoint for PID loop
    private static final double kIntegratorZone = 1000; //Within this many ticks of the setpoint, errorSum will increase
    private static final double k_flywheelTolerance = 100; //The flywheel tolerance
    private static final double velocitySetpoint = 20000; //Desired encoder velocity

    /*   [VARIABLES]   */
    public static double d; //The distance to the target
    private static double a2; //The angle from the limelight
    private static double error; //The difference between desiredRPM & currentRPM
    private static double errorRate; //The rate of change for the error
    private static double errorSum; //The sum of the errors
    private static double previousError; //The previous iteration's error
    private static double speed; //The speed to set for the flywheel motor

    /**
     * Sets Phoenix motor settings
     */
    public static void init() {
        //Reset Flywheel Falcons to factory default
        flywheelMaster.configFactoryDefault();
        flywheelSlave.configFactoryDefault();

        //Master settings
        flywheelMaster.setNeutralMode(NeutralMode.Brake); //Set neutral mode to break
        flywheelMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor); //Use Falcon 500 Integrated Encoder
        flywheelMaster.setSelectedSensorPosition(0,0,10); //Reset encoder position

        //Slave settings
        flywheelSlave.follow(flywheelMaster); //Follow master mode
        flywheelSlave.setInverted(true); //Invert motor to go along with master controller
        flywheelSlave.setNeutralMode(NeutralMode.Brake); //Set neutral mode to break
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        a2 = limelightTable.getEntry("ty").getDouble(0); //Sets a2, the y position of the target
        d = Math.round((h2-h1) / Math.tan(Math.toRadians(a1+a2))); //Calculates distance using a tangent

        if(m_gamepad.getStartButtonReleased()) { //Reset errorSum to 0 when start is released
            errorSum = 0;
        }

        shooting(); //Determine when we should be shooting
        reportStatistics(); //Report telemetry to the Driver Station
    }

    /**
     * Sets up shooting when we are holding start
     */
    private static void shooting() {
        if (m_gamepad.getStartButton()) { //If the start button is pressed
            if (Math.abs(velocitySetpoint - -flywheelMaster.getSelectedSensorVelocity()) <= k_flywheelTolerance) { // ... and we're close enough to the desired RPM ...
                SmartDashboard.putBoolean("AUTO SHOOT READY", true); //Alert driver that the shooting is ready
                runConveyor(); //Convey balls into the flywheel
            } else { //If we are NOT within our tolerance
                SmartDashboard.putBoolean("AUTO SHOOT READY", false); //Alert the operator
            }
            PID(); //Calculate PIDF loop
            flywheelMaster.set(ControlMode.PercentOutput, -speed); //Run the motor at the calculated level
        } else { //If start button is NOT pressed
            flywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kLeft)); //Set flywheel manually with joystick
            SmartDashboard.putBoolean("AUTO SHOOT READY", false); //Alert operator
        }
    }

    /**
     * Calculate PID outputs
     */
    private static void PID() {
        error = (velocitySetpoint - -flywheelMaster.getSelectedSensorVelocity()); //Calculate error
        if(Math.abs(error) <= kIntegratorZone) { //If we are within our integrator zone
            errorSum += error * 0.02; //Add the error to the error sum
        }
        errorRate = (error - previousError) / 0.02; //Find the rate of change of the error
        speed = (kF * velocitySetpoint + kP * error + kI * errorSum + kD * errorRate) / 100.0; //Calculate PIDF

        if(speed < 0) { //If speed is negative
            speed = 0; //Set speed to 0 (The flywheel should not run in reverse)
        }

        previousError = error; //Set previous error to the current error so we can use it in the next iteration
    }

    /**
     * Reports Telemetry to the Driver Station including the PIDF speed, setpoint, error, errorSum,
     * and flywheel master motor encoder velocity
     */
    private static void reportStatistics() {
        SmartDashboard.putNumber("Speed value", speed);
        SmartDashboard.putNumber("Desired RPM", velocitySetpoint);
        SmartDashboard.putNumber("Error", error);
        SmartDashboard.putNumber("ErrorSum", errorSum);
        SmartDashboard.putNumber("Flywheel Velocity (Master)", -flywheelMaster.getSelectedSensorVelocity());
    }

    /**
     * Shoots the ball
     */
    public static void runConveyor() {
        ballConveyor.set(0.2);
    }
}