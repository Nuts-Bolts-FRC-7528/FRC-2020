package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Class for the climbing manipulator
 */
public class Climber {

    public static void init() {
        winchMaster.configFactoryDefault(); //Format motor controller to factory default
        winchSlave.configFactoryDefault(); //Format motor controller to factory default
        winchSlave.follow(winchMaster); //Make the slave follow the master
        winchSlave.setInverted(true); //Make the slave inverted
        climberElevatorPiston.set(DoubleSolenoid.Value.kReverse); //Sets the piston to be in
    }

    public static void periodic() {
        if (m_gamepad.getBumper(GenericHID.Hand.kRight)) { //If the right bumper is pressed ...
            climberElevatorPiston.set(DoubleSolenoid.Value.kForward); //... then extend the manipulator
        } else if (m_gamepad.getBumper(GenericHID.Hand.kLeft)) { //If the left bumper is pressed ...
            winchMaster.set(ControlMode.PercentOutput, 0.5); //... then retract the winch at 50% speed
        }
    }
}