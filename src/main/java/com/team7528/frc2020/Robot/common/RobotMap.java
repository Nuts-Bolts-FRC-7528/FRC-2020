package com.team7528.frc2020.Robot.common;

import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.ColorSensorV3;
import com.team7528.lib.Blinkin;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Initiates both the motor controllers and joystick
 */

public class RobotMap {

    //Drive train
    public static WPI_TalonFX m_leftAft = new WPI_TalonFX(1);
    public static WPI_TalonFX m_leftFront = new WPI_TalonFX(2);
    public static WPI_TalonFX m_rightAft = new WPI_TalonFX(3);
    public static WPI_TalonFX m_rightFront = new WPI_TalonFX(4);
    public static DifferentialDrive m_drive;

    //Power Cell Shooter
    public static TalonSRX horizontalPulley = new TalonSRX(15);
    public static TalonSRX verticalPulley = new TalonSRX(7);
    public static WPI_TalonSRX turretRotator = new WPI_TalonSRX(20);
    public static TalonSRX leftFlywheelMaster = new TalonSRX(51);
    public static TalonSRX rightBackupFlywheelMaster = new TalonSRX(50);
    public static VictorSPX leftFlywheelSlave = new VictorSPX(9);
    public static VictorSPX rightFlywheelSlave = new VictorSPX(14);

    //Power Cell holder sensors
    public static DigitalInput troughFront = new DigitalInput(1);
    public static DigitalInput troughAft = new DigitalInput(2);

    //Operator interface
    public static Joystick m_joy = new Joystick(0); //For chassis driver
    public static XboxController m_gamepad = new XboxController(1); //For manipulator

    // Climbing
    public static TalonSRX winchMaster = new TalonSRX(1); // Motor type variable
    public static TalonSRX winchSlave = new TalonSRX(2);
    public static DoubleSolenoid climberElevatorPiston = new DoubleSolenoid(3,4);

    //Gyroscope
    public static PigeonIMU gyroScope = new PigeonIMU(50);

    //Intake
    public static DoubleSolenoid intake = new DoubleSolenoid(2,0);
    public static TalonFX intakeMotor = new TalonFX(6);
    public static DigitalInput ballCounter = new DigitalInput(0);

    //LEDs
    public static Blinkin m_blinkin = new Blinkin(0);

    //Vision
    public static NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight"); // The limelight NetworkTable, used to set a2
}