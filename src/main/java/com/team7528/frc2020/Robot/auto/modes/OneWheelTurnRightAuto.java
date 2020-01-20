package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.OneWheelTurnAction;

/**
 * This turns the robot 90 degrees, or to the right
 */

public class OneWheelTurnRightAuto extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new OneWheelTurnAction(90.0));
    }
}
