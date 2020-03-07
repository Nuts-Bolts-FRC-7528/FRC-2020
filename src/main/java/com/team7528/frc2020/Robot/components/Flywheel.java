package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;
import static com.team7528.frc2020.Robot.components.PowerCellIntake.powerCellCount;

/**
 * Class for the flywheel component. Also handles calculating the distance to the target using a Limelight.
 * Note that the flywheel encoder velocity has to be reversed each time we use it, as it is out of phase with
 * the direction it is actually moving in. The setPhase() method appears to do nothing in this regard, so we
 * simply need to invert it each time it is used.
 *
 * @author Matthew Correia
 */
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
    private static final double kFlywheelTolerance = 100; //The flywheel tolerance
    private static final double kVelocitySetpoint = 20000; //Desired encoder velocity
    private static final double kDip = 2000; //The amount that the motor velocity would dip when a power cell runs through

    /*   [RANGE CONSTANTS]   */
    private static final double kMinThreePoint = 12; //Minimum distance you're likely to get an inner port shot from (PLACEHOLDER VALUE)
    private static final double kMaxThreePoint = 12; //Maximum distance you're likely to get an inner port shot from (PLACEHOLDER VALUE)
    private static final double kMinViableRange = 5; //Minimum distance you'll make it into the outer port from (PLACEHOLDER VALUE)
    private static final double kMaxViableRange = 20; //Maximum distance you'll make it into the outer port from (PLACEHOLDER VALUE)

    /*   [VARIABLES]   */
    public static double d; //The distance to the target
    private static double error; //The difference between desiredRPM & currentRPM
    private static double errorSum; //The sum of the errors
    private static double previousError; //The previous iteration's error
    private static double speed; //The speed to set for the flywheel motor
    public static boolean shootOverride = false; //Allows other classes to start shooting
    public static boolean isUsingBackupEncoder = false;
    //Shuffleboard entry to alert the operator if they're in the 3 point range
    public static NetworkTableEntry threePointDistanceEntry = Shuffleboard.getTab("DRIVETRAIN").
            add("3-POINT RANGE",false).getEntry();
    //Shuffleboard entry to alert the operator if they're in the viable range to make any power port shot
    public static NetworkTableEntry viableDistanceEntry = Shuffleboard.getTab("DRIVETRAIN").
            add("VIABLE RANGE",false).getEntry();
    private static boolean velocityDipped; //If the flywheel velocity has dipped

    /**
     * Sets Phoenix motor settings
     */
    public static void init() {
        leftFlywheelMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);
        rightBackupFlywheelMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,0,10);

        if (!isUsingBackupEncoder) { //If it's not using a backup encoder, follow left master and invert right
            rightBackupFlywheelMaster.follow(leftFlywheelMaster);
            leftFlywheelSlave.follow(leftFlywheelMaster);
            rightFlywheelSlave.follow(leftFlywheelMaster);

            rightBackupFlywheelMaster.setInverted(true);
            rightFlywheelSlave.setInverted(true);
            leftFlywheelMaster.setInverted(false);
            leftFlywheelSlave.setInverted(false);
        } else { //If it is using a backup encoder, follow , follow right backup and invert left
            leftFlywheelMaster.follow(rightBackupFlywheelMaster);
            rightFlywheelSlave.follow(rightBackupFlywheelMaster);
            leftFlywheelSlave.follow(rightBackupFlywheelMaster);

            leftFlywheelMaster.setInverted(true);
            leftFlywheelSlave.setInverted(true);
            rightBackupFlywheelMaster.setInverted(false);
            rightFlywheelSlave.setInverted(false);
        }
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        //The angle from the limelight
        double a2 = limelightTable.getEntry("ty").getDouble(0); //Sets a2, the y position of the target
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
        if (m_gamepad.getStartButton() || shootOverride) { //If the start button is pressed
            if (!(isUsingBackupEncoder)) {
                alertOperator(leftFlywheelMaster);
            } else {
                alertOperator(rightBackupFlywheelMaster);
            }
        }  else { //If start button is NOT pressed
            if (!(isUsingBackupEncoder)) {
                leftFlywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kLeft)); //Set flywheel manually with joystick
            } else {
                rightBackupFlywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kLeft));
            }
            SmartDashboard.putBoolean("AUTO SHOOT READY", false); //Alert operator
        }
    }

    /**
     * Calculate PID outputs
     */
    private static void PID() {
        if(!isUsingBackupEncoder) {
            error = (kVelocitySetpoint - -leftFlywheelMaster.getSelectedSensorVelocity()); //Calculate error
        } else {
            error = (kVelocitySetpoint - -rightBackupFlywheelMaster.getSelectedSensorVelocity());
        }
        if(Math.abs(error) <= kIntegratorZone) { //If we are within our integrator zone
            errorSum += error * 0.02; //Add the error to the error sum
        }
        //The rate of change for the error
        double errorRate = (error - previousError) / 0.02; //Find the rate of change of the error
        speed = (kF * kVelocitySetpoint + kP * error + kI * errorSum + kD * errorRate) / 100.0; //Calculate PIDF

        if(speed < 0) { //If speed is negative
            speed = 0; //Set speed to 0 (The flywheel should not run in reverse)
        }

        previousError = error; //Set previous error to the current error so we can use it in the next iteration
    }

    /**
     * Reports Telemetry to the Driver Station including the PIDF speed, setpoint, error, errorSum,
     * and flywheel master motor encoder velocity. Also tells operator when they're in the range
     * for an outer port and/or inner port shot
     */
    private static void reportStatistics() {
        SmartDashboard.putNumber("Speed value", speed);
        SmartDashboard.putNumber("Desired RPM", kVelocitySetpoint);
        SmartDashboard.putNumber("Error", error);
        SmartDashboard.putNumber("ErrorSum", errorSum);
        SmartDashboard.putNumber("Flywheel Velocity (Master)", -leftFlywheelMaster.getSelectedSensorVelocity());

        if(d >= kMinThreePoint && d <= kMaxThreePoint) { //If we are in the range for a 3-point shot
            threePointDistanceEntry.setBoolean(true); //Alert operator
        } else { //If we are not in the range for a 3-point shot
            threePointDistanceEntry.setBoolean(false); //Alert operator
        }

        if(d >= kMinViableRange && d <= kMaxViableRange) { //If we are in the viable range for any power port shot
            viableDistanceEntry.setBoolean(true); //Alert operator
        } else { //If we cannot make a shot into the power port
            viableDistanceEntry.setBoolean(false); //Alert operator
        }
    }

    /**
     * Conveys balls into the flywheel
     */
    public static void runConveyor() {
        horizontalPulley.set(ControlMode.PercentOutput, 0.2);
        verticalPulley.set(ControlMode.PercentOutput, 0.2);
    }

    /**
     * Alerts operator whether the flywheel is at the desired velocity,
     * and runs the conveyors if they are
     * @param motor
     */
    private static void alertOperator(TalonSRX motor) {
        if (Math.abs(kVelocitySetpoint - -motor.getSelectedSensorVelocity()) <= kFlywheelTolerance) { // ... and we're close enough to the desired RPM ...
            SmartDashboard.putBoolean("AUTO SHOOT READY", true); //Alert driver that the shooting is ready
            runConveyor(); //Convey balls into the flywheel
        } else { //If we are NOT within our tolerance
            SmartDashboard.putBoolean("AUTO SHOOT READY", false); //Alert the operator
        }
        if (velocityDipped && -motor.getSelectedSensorVelocity() >= kVelocitySetpoint) { //If our velocity has dipped and we're back to 20000 ...
            powerCellCount--; //... remove a ball ...
            velocityDipped = false; //... and set velocityDipped to false
        }

        if (-motor.getSelectedSensorVelocity() <= kDip) { //If the current velocity is around how low it dips ...
            velocityDipped = true; //... then set velocityDipped to true
        }
        PID(); //Calculate PIDF loop
        motor.set(ControlMode.PercentOutput, -speed); //Run the motor at the calculated level
    }
}