package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.LeftTurnAction;

/**
 * This turns the robot 270 degrees, or to the left
 */
public class LeftTurnAuto extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new LeftTurnAction(270.0));
    }
}
