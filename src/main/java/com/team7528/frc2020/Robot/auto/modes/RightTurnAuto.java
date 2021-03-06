package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.TurnAction;

/**
 * This turns the robot 90 degrees, or to the right
 */
public class RightTurnAuto extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new TurnAction(90.0));
    }
}
