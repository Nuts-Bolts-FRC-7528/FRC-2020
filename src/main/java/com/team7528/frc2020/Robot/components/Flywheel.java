package com.team7528.frc2020.Robot.components;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the flywheel component
 */
@SuppressWarnings("ALL")
public class Flywheel implements Component {

    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics
    private static double currentRPM, d, a2; // The flywheel's current RPM, the distance to the target, and the angle from the limelight
    private static int loopCount; // Helps print statistics 5 times per second
    private static final double h1 = 11.25; // The height the limelight is mounted at
    private static final double h2 = 98.25; // The height of the target
    private static final double a1 = 7528; // The angle the limelight is mounted at
    private static NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight"); // The limelight NetworkTable, used to set a2
    private static double desiredRPM; // As it says, the desired RPM

    /**
     * Resets the iteration counter (loopCount)
     */
    public static void init() {
        loopCount = 0; // Resets the loopCount
        desiredRPM = 8000; // Sets the desired RPM
    }

    /**
     * Shoots the ball when start is pressed and prints statistics
     */
    public static void periodic() {
        a2 = limelightTable.getEntry("ty").getDouble(0); // Sets a2, the y position of the target
        currentRPM = flywheelSpinner.getSelectedSensorVelocity(); // Gets the flywheel's current RPM
        d = (h2-h1) / Math.tan(a1+a2); // Finds the distance

        if (currentRPM == desiredRPM) {
            if (m_gamepad.getStartButtonPressed()) {
                shoot();
            }
        } else {
            ballConveyor.set(0);
            flywheelSpinner.set(0);
        }
        loopCount++; // Increments loopCount
        if (loopCount >= 10) {
            reportStatistics(); // Reports statistics
            loopCount = 0; // Reset the loopCount
        }
    }

    /**
     * Reports the current flywheel RPM, then resets the String Builder (stats)
     */
    public static void reportStatistics() {
        stats.append("Current flywheel RPM: ").append(currentRPM);
        System.out.println();
        stats.setLength(0);
    }

    /**
     * Shoots the ball by setting the (possible) conveyor speed to 20%
     */
    private static void shoot() {
        ballConveyor.set(0.2);
    }
}