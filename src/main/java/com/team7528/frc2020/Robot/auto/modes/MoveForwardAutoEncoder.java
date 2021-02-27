package com.team7528.frc2020.Robot.auto.modes;


import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.DriveForwardActionEncoder;
import com.team7528.frc2020.Robot.auto.actions.WaitAction;

/**
 * Runs the motors forward for 5 seconds and then halts
 * Uses encoders
 */
public class MoveForwardAutoEncoder extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new DriveForwardActionEncoder(.2,5));
        runAction(new WaitAction(10));
    }
}