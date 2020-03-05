package com.team7528.frc2020.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.team7528.frc2020.Robot.auto.AutoModeExecutor;
import com.team7528.frc2020.Robot.auto.modes.*;
import com.team7528.frc2020.Robot.components.Flywheel;
import com.team7528.frc2020.Robot.components.Turret;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com.team7528.frc2020.Robot.common.RobotMap.*;
import static com.team7528.frc2020.Robot.auto.actions.DriveForwardActionGyro.ypr;
import static com.team7528.frc2020.Robot.components.Flywheel.d;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Robot extends TimedRobot {

    //Prints out stats regarding the string builder
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

    //Pigeon IMU stuff
    PigeonIMU.GeneralStatus genStatus = new PigeonIMU.GeneralStatus();

    //Lets you pick an autonomous code
    private AutoModeExecutor doNothingAuto = new AutoModeExecutor(new DoNothingAuto());
    private AutoModeExecutor moveForwardAutoGyro = new AutoModeExecutor(new MoveForwardAutoGyro());
    private AutoModeExecutor moveForwardAutoEncoder = new AutoModeExecutor(new MoveForwardAutoEncoder());
    private AutoModeExecutor driveForwardAutoFeet = new AutoModeExecutor(new DriveForwardAutoFeet());
    private AutoModeExecutor oneWheelTurnRightAuto = new AutoModeExecutor(new OneWheelTurnRightAuto());
    private AutoModeExecutor oneWheelTurnBackAuto = new AutoModeExecutor(new OneWheelTurnBackAuto());
    private AutoModeExecutor oneWheelTurnLeftAuto = new AutoModeExecutor(new OneWheelTurnLeftAuto());
    private AutoModeExecutor leftTurnAuto = new AutoModeExecutor(new LeftTurnAuto());
    private AutoModeExecutor uTurnAuto = new AutoModeExecutor(new UTurnAuto());
    private AutoModeExecutor rightTurnAuto = new AutoModeExecutor(new RightTurnAuto());

    private Double fineControlSpeedDouble;
    private int totallyUseful;

    private SendableChooser<AutoModeExecutor> autoPicker = new SendableChooser<>();
    private SendableChooser<Double> fineControlSpeed = new SendableChooser<>();
    private SendableChooser<Double> deadBandOptions = new SendableChooser<>();
    /**
     * Initiates motor controller set up
     */

    @Override
    public void robotInit() {

        //Defines a new string builder
        StringBuilder _initSb = new StringBuilder();

        //Defines a new differential drive
        m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

        //Resets talons to factory default
        m_leftFront.configFactoryDefault();
        m_rightFront.configFactoryDefault();
        m_leftAft.configFactoryDefault();
        m_rightAft.configFactoryDefault();

        //Initialize encoders
        m_leftFront.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        m_rightFront.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        m_leftFront.setSensorPhase(true);
        m_rightFront.setSensorPhase(false);

        //imuTalon.configSelectedFeedbackSensor()

        //Initialize Components
        Flywheel.init();
        Turret.init();

        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Defines that the back motors follows the front motors
        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

        // Invert the left motors
        m_leftFront.setInverted(false);
        m_rightFront.setInverted(false);

        //Auto code choosing
        autoPicker.setDefaultOption("Do Nothing", doNothingAuto);
        autoPicker.addOption("Move Forward (Gyro)", moveForwardAutoGyro);
        autoPicker.addOption("Move Forward (Encoder)", moveForwardAutoEncoder);
        autoPicker.addOption("Move Forward (Feet)", driveForwardAutoFeet);
        autoPicker.addOption("Turn Right (One Wheel)", oneWheelTurnRightAuto);
        autoPicker.addOption("Turn Around (One Wheel)", oneWheelTurnBackAuto);
        autoPicker.addOption("Turn Left (One Wheel)", oneWheelTurnLeftAuto);
        autoPicker.addOption("Turn Right", rightTurnAuto);
        autoPicker.addOption("Turn Around", uTurnAuto);
        autoPicker.addOption("Turn Left", leftTurnAuto);
        Shuffleboard.getTab("DRIVETRAIN").add("Auto Mode", autoPicker);

        // Fine Control Speed choosing
        fineControlSpeed.addOption("35% Speed", 0.35);
        fineControlSpeed.addOption("40% Speed", 0.40);
        fineControlSpeed.setDefaultOption("45% Speed", 0.45);
        fineControlSpeed.addOption("50% Speed", 0.50);
        fineControlSpeed.addOption("55% Speed", 0.55);
        fineControlSpeed.addOption("60% Speed", 0.60);
        SmartDashboard.putData("Fine Control Speed", fineControlSpeed);

        // Deadband choosing
        deadBandOptions.setDefaultOption("5%", 0.05);
        deadBandOptions.addOption("10%", 0.10);
        deadBandOptions.addOption("15%", 0.15);
        SmartDashboard.putData("Dead Band", deadBandOptions);

        //Appends the last revision date to the string builder
        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        _initSb.append("ROBOT.JAVA LAST REVISED:").append(sdf.format(file.lastModified()));
        _initSb.append("\n-----\nLEFT FRONT DRIVETRAIN FIRM:").append(m_leftFront.getFirmwareVersion());
        _initSb.append("\nLEFT AFT DRIVETRAIN FIRM:").append(m_leftAft.getFirmwareVersion());
        _initSb.append("\nRIGHT FRONT DRIVETRAIN FIRM:").append(m_rightFront.getFirmwareVersion());
        _initSb.append("\nRIGHT AFT DRIVETRAIN FIRM:").append(m_rightAft.getFirmwareVersion());

        //Prints out initial diagnostics for string builder
        System.out.println(_initSb);

        //Adds data for the shuffleboard regarding the differential drive
        Shuffleboard.getTab("DRIVETRAIN").add(m_drive);

        //Run LED init routine
        m_blinkin.init();

        //Transmits video through cameras
//        CameraServer.getInstance().startAutomaticCapture();

        Shuffleboard.getTab("DRIVETRAIN").add("Left Encoder", -m_leftFront.getSelectedSensorPosition());
        Shuffleboard.getTab("DRIVETRAIN").add("Right Encoder", m_rightFront.getSelectedSensorPosition());
    }

    @Override
    public void robotPeriodic() {
        m_blinkin.periodic(); //Set blinkin pattern during robot operation

        SmartDashboard.putBoolean("m_drive safety",m_drive.isSafetyEnabled());
        SmartDashboard.putNumber("distance", d);

        //Pigeon IMU printouts for testing

        //gyroScope.getGeneralStatus(genStatus);
        //gyroScope.getYawPitchRoll(ypr);
//        System.out.println("Yaw:" + ypr[0]);
        SmartDashboard.putNumber("Yaw",ypr[0]);

//        SmartDashboard.putNumber("Left Front Encoder", -m_leftFront.getSelectedSensorPosition());
//        SmartDashboard.putNumber("Right Front Encoder", m_rightFront.getSelectedSensorPosition());
//        SmartDashboard.putNumber("Distance to target", Flywheel.d); //Display distance from limelight to power port to Shuffleboard (DEBUG)
        SmartDashboard.putNumber("Left Encoder", m_leftFront.getSelectedSensorPosition());
        SmartDashboard.putNumber("Right Encoder", m_rightFront.getSelectedSensorPosition());
        SmartDashboard.putNumber("Turret Encoder", turretRotator.getSelectedSensorPosition());

        if(m_gamepad.getStickButtonPressed(GenericHID.Hand.kRight)) {
            limelightTable.getEntry("ledMode").setNumber(0);
        }

        if(m_gamepad.getStickButtonPressed(GenericHID.Hand.kLeft)) {
            limelightTable.getEntry("ledMode").setNumber(1);
        }
    }

    @Override
    public void autonomousInit() {

        m_drive.setSafetyEnabled(false);

        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Start Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.start();

        //Defines the value for dead band
        m_drive.setDeadband(deadBandOptions.getSelected());

        //Set LED to alliance color
        m_blinkin.setToTeamColor(false);
    }

    /**
     * The code will be active during autonomous periodic
     */
    @Override
    public void autonomousPeriodic() {

        //Prints Stats during auto
        //looperCounter++;
        /*f (looperCounter >= 10) {
            printStats();
            looperCounter = 0;
        }*/
    }

    /**
     * Stops auto
     */

    @Override
    public void teleopInit() {
        //Stops Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.stop();
        fineControlSpeedDouble = fineControlSpeed.getSelected();
        m_drive.setSafetyEnabled(true);
    }

    /**
     * This code will be active during teleop periodic
     */
    @Override
    public void teleopPeriodic() {

        //Sets up for fine control

        if (m_joy.getPOV() == 0) { //Forward
            m_drive.arcadeDrive(-fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 90) { //Right
            m_drive.arcadeDrive(0,-fineControlSpeedDouble);
        } else if (m_joy.getPOV() == 180) { //Backward
            m_drive.arcadeDrive(fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 270) { //Left
            m_drive.arcadeDrive(0,fineControlSpeedDouble);
        } else {
            //Sets up arcade drive
            m_drive.arcadeDrive(m_joy.getY(), -m_joy.getX());
        }

//        if (m_gamepad.getBButton()) {
//            turretRotator.set(ControlMode.PercentOutput,.50);
//        }
//
//        if (m_gamepad.getXButton()) {
//            turretRotator.set(ControlMode.PercentOutput,-.50);
//        }

        //Prints out diagnostics
        looperCounter++;
        if (looperCounter >= 10) {
//            printStats();
            looperCounter = 0;
        }

        // Periodic logic for components
        Flywheel.periodic();
//        System.out.println("debug from robot class");
        Turret.periodic();

//        flywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));
    }

    public void seekRight() {
        if (m_gamepad.getBButton()) {
            if (limelightTable.getEntry("tv").getDouble(0) == 1) {
                double seek_adjust = 0.05 * limelightTable.getEntry("tx").getDouble(0);
                if (seek_adjust > 0.4) {
                    seek_adjust = 0.4;
                } else if (seek_adjust < -0.4) {
                    seek_adjust = -0.4;
                }
                turretRotator.set(ControlMode.PercentOutput, -seek_adjust);
                SmartDashboard.putNumber("seek_adjust_", -seek_adjust);
            } else {
                turretRotator.set(ControlMode.PercentOutput, -.20);
            }
        }
    }

    public void seekLeft() {
        if(m_gamepad.getXButton()) {
            if(limelightTable.getEntry("tv").getDouble(0) == 1) {
                double seek_adjust = 0.05 * limelightTable.getEntry("tx").getDouble(0);
                if(seek_adjust > 0.4) {
                    seek_adjust = 0.4;
                } else if(seek_adjust < -0.4) {
                    seek_adjust = -0.4;
                }
                turretRotator.set(ControlMode.PercentOutput, -seek_adjust);
                SmartDashboard.putNumber("seek_adjust_",-seek_adjust);
            } else {
                turretRotator.set(ControlMode.PercentOutput,.2);
            }
        }
    }

    @Override
    public void disabledInit() {
        m_blinkin.rainbow(); //Sets blinkin to use rainbow pattern

        //Stops Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.stop();
        driveForwardAutoFeet.stop();

        System.out.println(totallyUseful);
    }



    //gyroScope.getYawPitchRoll(ypr);
    //System.out.println("Yaw:" + ypr[0]);

    /**
     * Prints out statistics
     */
    private void printStats() {
        _sb.append("**********");
        _sb.append("\ntimestamp").append(sdf.format(Instant.now().getEpochSecond()));
        _sb.append("\tleft-front").append(m_leftFront.get());
        _sb.append("\tright-front").append(m_rightFront.get());
        _sb.append("\tleft-aft").append(m_leftAft.get());
        _sb.append("\tright-aft").append(m_rightAft.get());
        _sb.append("\n**********");

        //Prints out the string builder
        System.out.println(_sb.toString());
    }
}