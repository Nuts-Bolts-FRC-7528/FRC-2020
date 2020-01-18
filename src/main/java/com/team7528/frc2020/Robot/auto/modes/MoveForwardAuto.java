package com.team7528.frc2020.Robot.auto.modes;


import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.WaitAction;

/**
 * Runs the motors forward for 5 seconds and then halts until the end of the sandstorm period
 */
public class MoveForwardAuto extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeDoneException {
//        runAction(new DriveForwardAction(.2,5));
        runAction(new WaitAction(10));
    }
}