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

    public void teleopPeriodic() {

        SEEK_R = seek_r;
        SEEK_L = seek_l;
        DISENGAGE = IsTargetValid;

        boolean steer_r = m_gamepad.getBButton();
        boolean steer_l = m_gamepad.getXButton();
        boolean IsTargetValid = m_gamepad.getAButton();

    }

    public void turret() {

        final double SEEK_R = 0.03;
        final double SEEK_L = 0.03;
        final double DISENGAGE = 0.0;

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
        double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

        if (tv < 1.0)
        {
            IsTargetValid = false;
            seek_r = 0.0;
            seek_l = 0.0;
            return;
        }

        IsTargetValid = true;

    }
}