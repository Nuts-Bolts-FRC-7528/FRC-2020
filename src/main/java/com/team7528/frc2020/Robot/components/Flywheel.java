package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the flywheel component
 *
 * @author Matthew Correia
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class Flywheel {

    private static final double a1 = 37.5; // The angle the limelight is mounted at
    private static final double h1 = 30 /* Non-block Height: 11.25 */ ; // The height the limelight is mounted at
    private static final double h2 = 98.25; // The height of the target
    private static final double k_distance = 0.07; // Constant for the flywheel speed
    private static final double k_flywheelTolerance = 100; // The flywheel tolerance
    private static final double k_gearRatio = 1 / 4.0; // The gear ratio for the flywheel
    private static final double kP = 0.036; // P constant for PID loop
    //Ku = 0.1, 0.06
    //Tu = 0.3
    private static final double kI = 0.24/*0.0837*/; // I constant for PID loop
    private static final double kD = 0.00135/*0.0012659625*/; // D constant for PID loop
    private static final double kF = /*1.0/2*/0000;
    private static double a2; // The angle from the limelight
    private static double currentRPM; // The flywheel's current RPM
    public static double d; // The distance to the target
    private static double desiredRPM; // As it says, the desired RPM
    private static double error; // The difference between desiredRPM & currentRPM
    private static double errorRate; // The rate of change for the error
    private static double errorSum; // The sum of the errors
    private static double previousError; // The previous iteration's error
    private static double speed; // The speed to set for the flywheel motor
    private static int loopCount; // Helps us print statistics 5 times per second
    private static int shootingLoop, shootingLoopTwo;
    private static NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight"); // The limelight NetworkTable, used to set a2
    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics
    private static double encoderVelocity; // the velocity of the flywheel
    private static double dips; // the amount the rpm dips
    public static int ballAmount; // The amount of balls in the flywheel

    /**
     * Resets the iteration counter (loopCount)
     */
    public static void init() {
        loopCount = 0; // Resets the iteration counter (loopCount)
        flywheelMaster.configFactoryDefault();
        flywheelSlave.configFactoryDefault();
        flywheelSlave.follow(flywheelMaster);
        flywheelSlave.setInverted(true);
        flywheelMaster.setNeutralMode(NeutralMode.Brake);
        flywheelSlave.setNeutralMode(NeutralMode.Brake);
        flywheelMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        flywheelMaster.setSelectedSensorPosition(0,0,10);
        flywheelMaster.configOpenloopRamp(0.5,10);
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        a2 = limelightTable.getEntry("ty").getDouble(0); // Sets a2, the y position of the target
        currentRPM = flywheelMaster.getSelectedSensorVelocity() / k_gearRatio; // Gets the flywheel's current RPM
        d = Math.round((h2-h1) /* 2.56*/ / Math.tan(Math.toRadians(a1+a2))); // Finds the distance

        shooting();
        reportStatistics();

//        flywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));
    }

    /**
     * Sets up shooting when we are holding start
     */
    private static void shooting() {
        if (m_gamepad.getStartButton()) { // If the start button is pressed ...
            if (Math.abs(desiredRPM - currentRPM) <= k_flywheelTolerance) { // ... and we're close enough to the desired RPM ...
                shoot(); // ... then shoot
            }
            PID();
            flywheelMaster.set(ControlMode.PercentOutput, speed); // ... set the motor speed to the desired RPM
            shootingLoop++;
        } else {
            flywheelMaster.set(ControlMode.PercentOutput, 0);
        }
        shootingLoopTwo++;
    }

    /**
     * PID loop method
     */
    private static void PID() {
        error = (desiredRPM - currentRPM);
        errorSum += error * 0.02; // Add the error to the error sum
        if (errorSum >= 1) { // If the error sum gets too high ...
            errorSum = 1; // ... set it to the maximum
        } else if (errorSum <= -1) {
            errorSum = -1;
        }
        errorRate = (error - previousError) / 0.02;
        speed = (kF * desiredRPM + kP * error + kI * errorSum + kD * errorRate) / 100.0;
//        desiredRPM = speed * d * k_distance / k_gearRatio; // Sets the desired RPM
        desiredRPM = 10000; // Sets the desired RPM
        previousError = error;
    }

    /**
     * Reports the current flywheel RPM, then resets the String Builder (stats)
     */
    private static void reportStatistics() {
        loopCount++;
        if (loopCount >= 10) {
            stats.append("Current flywheel RPM: ").append(currentRPM); // The current flywheel RPM
//            System.out.println(stats); // Print the stats variable (current flywheel RPM)
            stats.setLength(0); // And clear the stats variable to get it ready for the next print
            loopCount = 0;
        }
        SmartDashboard.putBoolean("Gamepad Start button pressed", m_gamepad.getStartButton());
        SmartDashboard.putNumber("Iterations with shooting() called", shootingLoopTwo);
        SmartDashboard.putNumber("Iterations with shooting() used", shootingLoop);
        SmartDashboard.putNumber("Speed value", speed);
        SmartDashboard.putNumber("Desired RPM", desiredRPM);
    }

    /**
     * Shoots the ball
     */
    public static void shoot() {
        ballConveyor.set(0.2);
    }
}