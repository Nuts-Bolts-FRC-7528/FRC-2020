package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.WaitAction;

/**
 * Waits for 15 seconds
 */
public class DoNothingAuto extends AutoModeBase {

    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new WaitAction(15));
    }
}