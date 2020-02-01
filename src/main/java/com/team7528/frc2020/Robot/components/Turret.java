package com.team7528.frc2020.Robot.components;


import edu.wpi.first.networktables.NetworkTableInstance;
import static com.team7528.frc2020.Robot.common.RobotMap.m_gamepad;

public class Turret {

    private boolean IsTargetValid = false;
    private double seek_r = 0.0;
    private double seek_l = 0.0;

    private boolean DISENGAGE;
    private double SEEK_R;
    private double SEEK_L;

    public void periodic() {

        SEEK_R = seek_r;
        SEEK_L = seek_l;
        DISENGAGE = IsTargetValid;

        if (m_gamepad.getBButtonPressed()) {
            seek_r = 0.3;
        }
        if (m_gamepad.getXButtonPressed()) {
            seek_l = 0.3;
        }
        if (m_gamepad.getAButtonPressed()) {

        }

    }

    public void turret() {

        final double SEEK_R = 0.03;
        final double SEEK_L = 0.03;
        final double DISENGAGE = 0.0;

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

        if (tv < 1) {
            IsTargetValid = false;
            seek_r = 0.3;
            seek_l = 0.3;
            return;
        }

        if (tx == 0.4) {
            double seek_cmd = tx * SEEK_R;
        } else {
            double seek_cmd = tx * SEEK_L;
        }
    }
}