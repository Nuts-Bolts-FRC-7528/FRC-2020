package com.team7528.frc2020.Robot.common;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Initiates both the motor controllers and joystick
 */

public class RobotMap {

    //Drive train
    public static WPI_TalonSRX m_leftAft = new WPI_TalonSRX(0);
    public static WPI_TalonSRX m_leftFront = new WPI_TalonSRX(1);
    public static WPI_TalonSRX m_rightAft = new WPI_TalonSRX(2);
    public static WPI_TalonSRX m_rightFront = new WPI_TalonSRX(3);

    //Creates a new variable for how the wheels of the robot will move
    public static DifferentialDrive m_drive;


    //PowerCell Shooter
    public static WPI_TalonSRX turretRotator = new WPI_TalonSRX(62);
    public static WPI_TalonSRX flywheelSpinner = new WPI_TalonSRX(63);
    public static WPI_TalonSRX hood = new WPI_TalonSRX(15);

    //Operator interface
    public static Joystick m_joy = new Joystick(0); //For chassis driver
    public static XboxController m_gamepad = new XboxController(1); //For manipulator

    //Gyroscope
    public static AnalogGyro gyroScope = new AnalogGyro(0);
}