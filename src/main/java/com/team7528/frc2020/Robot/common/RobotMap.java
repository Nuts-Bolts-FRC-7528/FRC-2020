package com.team7528.frc2020.Robot.common;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Initiates all of the motor controllers and joystick
 */

public class RobotMap {
    public static WPI_TalonSRX m_leftAft = new WPI_TalonSRX(0);
    public static WPI_TalonSRX m_leftFront = new WPI_TalonSRX(1);
    public static WPI_TalonSRX m_rightAft = new WPI_TalonSRX(2);
    public static WPI_TalonSRX m_rightFront = new WPI_TalonSRX(3);

    public static Joystick m_joy = new Joystick(0);
}