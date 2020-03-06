package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.ShootAction;
import com.team7528.frc2020.Robot.auto.actions.SimpleDriveAction;

public class ShootMoveBackAuto extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new ShootAction());
        runAction(new SimpleDriveAction(5,-0.25));
    }
}
