package com.team7528.frc2020.Robot.components;


import static com.team7528.frc2020.Robot.common.RobotMap.flywheelSpinner;

@SuppressWarnings("ALL")
public class Flywheel implements Component {

    private static StringBuilder stats = new StringBuilder(); // StringBuilder for statistics
    private static double currentRPM; // The flywheel's current RPM
    private static int loopCount; // Helps print statistics 5 times per second



    public static void init() {

    }

    public static void periodic() {

        currentRPM = flywheelSpinner.get(); // Gets the flywheel's current RPM






        loopCount++; // Increments loopCount
        if (loopCount >= 10) {
            reportStatistics(); // Reports statistics
            loopCount = 0;
        }
    }

    public static void reportStatistics() {
        stats.append("Current flywheel RPM: ").append(currentRPM);
    }

}