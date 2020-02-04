package com.team7528.frc2020.Robot.components;

import edu.wpi.first.networktables.NetworkTableInstance;
import static com.team7528.frc2020.Robot.common.RobotMap.m_gamepad;

public class Turret {

    private boolean seek_r;
    private boolean seek_l;

    public void periodic() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);

        if (tv == 1) {
        }

        if (m_gamepad.getBButtonPressed()) {
            seek_r = true;
        } else
        if (m_gamepad.getXButtonPressed()) {
            seek_l = true;
        }
        if (m_gamepad.getAButtonPressed()) {
        }

    }

}