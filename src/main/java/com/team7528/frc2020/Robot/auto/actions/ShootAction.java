package com.team7528.frc2020.Robot.auto.actions;

import com.team7528.frc2020.Robot.components.Flywheel;
import com.team7528.frc2020.Robot.components.PowerCellIntake;

/**
 * Shoots all the balls we're currently holding
 */
public class ShootAction implements Action {
    @Override
    public boolean finished() {
        return PowerCellIntake.powerCellCount == 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void done() {
        Flywheel.shootOverride = false;
    }

    @Override
    public void start() {
        Flywheel.shootOverride = true;
    }
}
