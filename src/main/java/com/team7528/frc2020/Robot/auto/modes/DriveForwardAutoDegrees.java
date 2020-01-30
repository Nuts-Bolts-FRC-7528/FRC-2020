package com.team7528.frc2020.Robot.auto.modes;

import com.team7528.frc2020.Robot.auto.AutoModeBase;
import com.team7528.frc2020.Robot.auto.AutoModeDoneException;
import com.team7528.frc2020.Robot.auto.actions.DriveForwardActionDegrees;
import com.team7528.frc2020.Robot.auto.actions.WaitAction;

/**
 * Drives forward 5 feet, then waits
 */
public class DriveForwardAutoDegrees extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeDoneException {
        runAction(new DriveForwardActionDegrees(5)); // Drives 5 feet forward
        runAction(new WaitAction(15)); // Then waits for the rest of autonomous (set as 15 just to be safe)
    }
}