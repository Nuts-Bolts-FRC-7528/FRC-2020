package com.team7528.frc2020.Robot.common;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Initiates both the motor controllers and joystick
 */

public class RobotMap {

    //Drive train
    public static WPI_TalonSRX m_leftAft = new WPI_TalonSRX(0);
    public static WPI_TalonSRX m_leftFront = new WPI_TalonSRX(1);
    public static WPI_TalonSRX m_rightAft = new WPI_TalonSRX(2);
    public static WPI_TalonSRX m_rightFront = new WPI_TalonSRX(3);

    //Operator interface
    public static Joystick m_joy = new Joystick(0); //For chassis driver
    public static XboxController m_gamepad = new XboxController(1); //For manipulator
}