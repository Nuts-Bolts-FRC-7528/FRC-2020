package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * This class controls the PowerCell intake and everything associated with it
 *
 * @author Robert Garcia
 */
public class PowerCellIntake {
public static int ballAmount = 0;
    /**
     * This is the general code for the ball intake, eject, and raising/lowering the piston
     */
    public static void periodic() {
        if (!ballCounter.get()) {

        }
        if(m_gamepad.getBumper(GenericHID.Hand.kRight)) { //if right bumper is pressed
            //intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput,-1); //moves wheels to intake balls
        }
        if(m_gamepad.getBumper(GenericHID.Hand.kLeft)) { //if left bumper is pressed
//            intake.set(DoubleSolenoid.Value.kReverse); //drops piston
            intakeMotor.set(ControlMode.PercentOutput, 1); //moves wheel to eject balls
        }
        if(m_gamepad.getYButton()) { //if Y button is pressed
//            intake.set(DoubleSolenoid.Value.kForward); //raises piston
            intakeMotor.set(ControlMode.PercentOutput, 0); //does not move wheels
        }
    }

    public static void init() {

    }

    public static void reportStatistics() {
        //System.out.println("Intake Motor Power Level " + intakeMotor.get());
    }
}
