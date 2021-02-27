package com.team7528.frc2020.Robot.auto.actions;

import com.team7528.frc2020.Robot.common.RobotMap;
import edu.wpi.first.wpilibj.Timer;

public class SimpleDriveAction implements Action {
    private double startTime; // FPGA timestamp at the start of the WaitAction
    private double timeToDrive; // Amount of time to wait in seconds
    private double drivePercent;

    public SimpleDriveAction(double seconds, double power) {
        timeToDrive = seconds;
        drivePercent = power;
    }

    @Override
    public boolean finished() {
        return (Timer.getFPGATimestamp() - startTime >= timeToDrive);
    }

    @Override
    public void update() {
        RobotMap.m_drive.arcadeDrive(0,drivePercent); //Drive the chassis
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
    }
}
