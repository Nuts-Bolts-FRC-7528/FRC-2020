package com.team7528.frc2020.Robot.common;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;
import com.team7528.lib.Blinkin;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Initiates both the motor controllers and joystick
 */

public class RobotMap {

    //Drive train
    public static WPI_TalonFX m_leftAft = new WPI_TalonFX(0);
    public static WPI_TalonFX m_leftFront = new WPI_TalonFX(1);
    public static WPI_TalonFX m_rightAft = new WPI_TalonFX(2);
    public static WPI_TalonFX m_rightFront = new WPI_TalonFX(3);

    //Creates a new variable for how the wheels of the robot will move
    public static DifferentialDrive m_drive;

    // Control Panel
    public static WPI_TalonSRX controlPanelWheel = new WPI_TalonSRX(0);
    public static DoubleSolenoid m_wheelEjector = new DoubleSolenoid(2,3);
//    public static ColorSensorV3 colors = new ColorSensorV3(I2C.Port.kOnboard);

    //PowerCell Shooter
    public static WPI_TalonSRX turretRotator = new WPI_TalonSRX(1);
    public static WPI_TalonSRX flywheelSpinner = new WPI_TalonSRX(2);
    public static WPI_TalonSRX hood = new WPI_TalonSRX(3);
//    public static DoubleSolenoid ballSetter = new DoubleSolenoid(2,3);

    //PowerCell holder drum spinner
    public static TalonSRX drumSpinner = new TalonSRX(4);

    //Operator interface
    public static Joystick m_joy = new Joystick(0); //For chassis driver
    public static XboxController m_gamepad = new XboxController(1); //For manipulator

    //Gyroscope
    public static AnalogGyro gyroScope = new AnalogGyro(0);

    //Intake
//    public static DoubleSolenoid intake = new DoubleSolenoid(1,2);
    public static VictorSPX intakeMotor = new VictorSPX(0);
    public static DigitalInput ballCounter = new DigitalInput(0);

    //LEDs
    public static Blinkin m_blinkin = new Blinkin(0);
}