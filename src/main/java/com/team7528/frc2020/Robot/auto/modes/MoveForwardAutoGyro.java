package com.team7528.frc2020.Robot.auto.modes;


import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.DriveForwardActionGyro;
import com.team7528.frc2020.Robot.auto.actions.WaitAction;

/**
 * Runs the motors forward for 5 seconds and then halts
 * Uses gyroscope
 */
public class MoveForwardAutoGyro extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new DriveForwardActionGyro(0,.2,5));
        runAction(new WaitAction(10));
    }
}