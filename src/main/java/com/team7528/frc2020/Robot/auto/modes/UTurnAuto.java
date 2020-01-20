package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.UTurnAction;

/**
 * This turns the robot 180 degrees, or backward
 */
public class UTurnAuto extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new UTurnAction(180.0));
    }
}
