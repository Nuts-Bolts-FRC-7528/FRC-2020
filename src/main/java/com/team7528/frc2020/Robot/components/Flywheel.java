package com.team7528.frc2020.Robot.components;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the flywheel component
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Flywheel implements Component {

    private static boolean justShot; // Whether or not we just shot a ball
    private static final double h1 = 11.25; // The height the limelight is mounted at
    private static final double h2 = 98.25; // The height of the target
    private static final double a1 = 7528; // The angle the limelight is mounted at
    private static final double kD = 0.07; // Constant for the flywheel speed
    private static final double k_speedToRPM = 600 / 360.0; // Conversion from the speed parameter to RPM
    private static final double k_flywheelTolerance = 100; // The flywheel tolerance
    private static final double k_gearRatio = 1 / 4.0;
    private static double desiredRPM; // As it says, the desired RPM
    private static double currentRPM; // The flywheel's current RPM
    private static double d; // The distance to the target
    private static double a2; // The angle from the limelight
    private static int loopCount; // Helps print statistics 5 times per second
    private static int actuatorLoopCount; // Makes sure we don't push the actuator in before we shoot
    private static NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight"); // The limelight NetworkTable, used to set a2
    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics

    /**
     * Resets the iteration counter (loopCount)
     */
    public static void init() {
        loopCount = 0; // Resets the loopCount
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        if (justShot) {
            if (actuatorLoopCount >= 20) { // Gives the actuator a second to push the ball
                reset();
            } else {
                actuatorLoopCount++;
            }
        }
        a2 = limelightTable.getEntry("ty").getDouble(0); // Sets a2, the y position of the target
        currentRPM = flywheelSpinner.getSelectedSensorVelocity() / k_gearRatio; // Gets the flywheel's current RPM
        d = (h2-h1) / Math.tan(a1+a2); // Finds the distance
        desiredRPM = 8000 * d * kD / k_gearRatio; // Sets the desired RPM

        if (m_gamepad.getStartButtonPressed()) { // If the start button is pressed ...
            if (Math.abs(desiredRPM - currentRPM) <= k_flywheelTolerance) { // ... and we're close enough to the desired RPM ...
                if (!justShot) { // ... and we haven't just shot ...
                    shoot(); // ... then shoot
                }
            } else { // If we aren't at the desired RPM ...
                flywheelSpinner.set(desiredRPM / k_speedToRPM); // ... set the motor speed to the desired RPM
            } // This is the speed parameter ^^; k_speedToRPM converts what we set it to into RPM
        }

        loopCount++; // Increments loopCount
        if (loopCount >= 10) { // If a fifth of a second has passed ...
            reportStatistics(); // ... reports statistics ...
            loopCount = 0; // ... and reset the loopCount
        }
    }

    /**
     * Reports the current flywheel RPM, then resets the String Builder (stats)
     */
    private static void reportStatistics() {
        stats.append("Current flywheel RPM: ").append(currentRPM); // The current flywheel RPM
        System.out.println(stats); // Print the stats variable (current flywheel RPM)
        stats.setLength(0); // And clear the stats variable to get it ready for the next print
    }

    /**
     * Shoots the ball
     */
    private static void shoot() {
        ballSetter.set(DoubleSolenoid.Value.kForward); // Push the ball into the shooter
        justShot = true; // and set justShot to true
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