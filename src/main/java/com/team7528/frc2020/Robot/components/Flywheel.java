package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the flywheel component
 *
 * @author Matthew Correia
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Flywheel implements Component {

    public static boolean justShot; // Whether or not we just shot a ball
    private static final double a1 = 104; // The angle the limelight is mounted at
    private static final double h1 = 11.25; // The height the limelight is mounted at
    private static final double h2 = 20.25/*98.25*/; // The height of the target
    private static final double k_distance = 0.07; // Constant for the flywheel speed
    private static final double kD = 0.07; // D constant for PID loop
    private static final double k_flywheelTolerance = 100; // The flywheel tolerance
    private static final double k_gearRatio = 1 / 4.0; // The gear ratio for the flywheel
    private static final double kI = 0.07; // I constant for PID loop
    private static final double kP = 0.07; // P constant for PID loop
    private static final double k_speedToRPM = 600 / 360.0; // Conversion from the speed parameter to RPM
    public static double d; // The distance to the target
    private static double a2; // The angle from the limelight
    private static double currentRPM; // The flywheel's current RPM
    private static double desiredRPM; // As it says, the desired RPM
    private static double error; // The difference between desiredRPM & currentRPM
    private static double errorRate; // The rate of change for the error
    private static double errorSum; // The sum of the errors
    private static double previousError; // The previous iteration's error
    private static double speed; // The speed to set for the flywheel motor
    private static int actuatorLoopCount; // Makes sure we don't push the actuator in before we shoot
    private static int loopCount; // Helps us print statistics 5 times per second
    private static NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight"); // The limelight NetworkTable, used to set a2
    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics

    /**
     * Resets the iteration counter (loopCount)
     */
    public static void init() {
        loopCount = 0; // Resets the iteration counter (loopCount)
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        a2 = limelightTable.getEntry("ty").getDouble(0); // Sets a2, the y position of the target
        currentRPM = flywheelSpinner.getSelectedSensorVelocity() / k_gearRatio; // Gets the flywheel's current RPM
        d = (h2-h1) / Math.tan(Math.toRadians(a1+a2)); // Finds the distance

        PID();
        shooting();
        reportStatistics();
    }

    /**
     * Sets up shooting when we are holding start
     */
    private static void shooting() {
        if (m_gamepad.getStartButton()) { // If the start button is pressed ...
            if (Math.abs(desiredRPM - currentRPM) <= k_flywheelTolerance) { // ... and we're close enough to the desired RPM ...
                if (!justShot) { // ... and we haven't just shot ...
                    shoot(); // ... then shoot
                } else {
                    if (actuatorLoopCount >= 20) { // give the actuator a second to push the ball
                        reset();
                    } else {
                        actuatorLoopCount++; // Increments actuatorLoopCount
                    }
                }
            } else { // If we aren't at the desired RPM ...
                flywheelSpinner.set(ControlMode.Velocity, speed); // ... set the motor speed to the desired RPM
            }
        }
    }

    /**
     * PID loop method
     */
    private static void PID() {
        error = (desiredRPM - currentRPM) / k_speedToRPM;
        if (Math.abs(error) <= 500) { // If the error is less than or equal to 500 ...
            errorSum += error; // Add it to the error sum
        }
        if (errorSum >= 2000) { // If the error sum gets too high ...
            errorSum = 2000; // ... set it to the maximum
        } else if (errorSum <= -2000) {
            errorSum = -2000;
        }
        errorRate = (error - previousError) / 0.02;
        speed = (kP * error + kI * errorSum + kD * errorRate);
        desiredRPM = speed * d * k_distance / k_gearRatio; // Sets the desired RPM
        previousError = error;
    }

    /**
     * Reports the current flywheel RPM, then resets the String Builder (stats)
     */
    private static void reportStatistics() {
        loopCount++;
        if (loopCount >= 10) {
            stats.append("Current flywheel RPM: ").append(currentRPM); // The current flywheel RPM
            System.out.println(stats); // Print the stats variable (current flywheel RPM)
            stats.setLength(0); // And clear the stats variable to get it ready for the next print
            loopCount = 0;
        }
    }

    /**
     * Shoots the ball
     */
    private static void shoot() {
        ballSetter.set(DoubleSolenoid.Value.kForward); // Push the ball into the shooter
        justShot = true; // an-d set justShot to true
    }

    /**
     * Resets the actuator
     */
    private static void reset() {
        ballSetter.set(DoubleSolenoid.Value.kReverse); // Pull in the actuator
        justShot = false; // and set justShot to false
        actuatorLoopCount = 0;
    }
}