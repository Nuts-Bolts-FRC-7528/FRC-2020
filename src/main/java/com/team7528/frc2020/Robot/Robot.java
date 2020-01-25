package com.team7528.frc2020.Robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.team7528.frc2020.Robot.auto.AutoModeExecutor;
import com.team7528.frc2020.Robot.auto.modes.*;
import com.team7528.frc2020.Robot.components.BallShooter;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

public class Robot extends TimedRobot {

    //Prints out stats regarding the string builder
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

    //Lets you pick an autonomous code
    private AutoModeExecutor doNothingAuto = new AutoModeExecutor(new DoNothingAuto());
    private AutoModeExecutor moveForwardAutoGyro = new AutoModeExecutor(new MoveForwardAutoGyro());
    private AutoModeExecutor moveForwardAutoEncoder = new AutoModeExecutor(new MoveForwardAutoEncoder());
    private AutoModeExecutor driveForwardAutoDegrees = new AutoModeExecutor(new DriveForwardAutoDegrees());
    private AutoModeExecutor oneWheelTurnRightAuto = new AutoModeExecutor(new OneWheelTurnRightAuto());
    private AutoModeExecutor oneWheelTurnBackAuto = new AutoModeExecutor(new OneWheelTurnBackAuto());
    private AutoModeExecutor oneWheelTurnLeftAuto = new AutoModeExecutor(new OneWheelTurnLeftAuto());
    private AutoModeExecutor leftTurnAuto = new AutoModeExecutor(new LeftTurnAuto());
    private AutoModeExecutor uTurnAuto = new AutoModeExecutor(new UTurnAuto());
    private AutoModeExecutor rightTurnAuto = new AutoModeExecutor(new RightTurnAuto());


    private SendableChooser<AutoModeExecutor> autoPicker = new SendableChooser<AutoModeExecutor>();

    /**
     * Initiates motor controller set up
     */

    @Override
    public void robotInit() {

        //Defines a new string builder
        StringBuilder _initSb = new StringBuilder();

        //Defines the value for dead band
        m_drive.setDeadband(.05);

        //Defines a new differential drive
        m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

        //Resets talons to factory default
        m_leftFront.configFactoryDefault();
        m_rightFront.configFactoryDefault();
        m_leftAft.configFactoryDefault();
        m_rightAft.configFactoryDefault();

        //Initialize encoders
        m_leftFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_rightFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_leftFront.setSensorPhase(false);
        m_rightFront.setSensorPhase(true); //Right side encoder is inverted

        //Initialize components
        BallShooter.init();

        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Defines that the back motors follows the front motors
        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

        //Auto code choosing
        autoPicker.setDefaultOption("Do Nothing", doNothingAuto);
        autoPicker.addOption("Move Forward (Gyro)", moveForwardAutoGyro);
        autoPicker.addOption("Move Forward (Encoder)", moveForwardAutoEncoder);
        autoPicker.addOption("Move Forward (Degrees)", driveForwardAutoDegrees);
        autoPicker.addOption("Turn Right (One Wheel)", oneWheelTurnRightAuto);
        autoPicker.addOption("Turn Around (One Wheel)", oneWheelTurnBackAuto);
        autoPicker.addOption("Turn Left (One Wheel)", oneWheelTurnLeftAuto);
        autoPicker.addOption("Turn Right", rightTurnAuto);
        autoPicker.addOption("Turn Around", uTurnAuto);
        autoPicker.addOption("Turn Left", leftTurnAuto);
        SmartDashboard.putData(autoPicker);

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

    }
    @Override
    public void autonomousInit() {
        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Start Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.start();
    }
    /**
     * The code will be active during teleop periodic
     */

    @Override
    public void autonomousPeriodic() {
        //Prints Stats during auto
        looperCounter++;
        if (looperCounter >= 10) {
            printStats(); looperCounter = 0;
        }
    }

    /**
     * Stops auto
     */
    @Override
    public void teleopInit() {
        //Stops Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.stop();
    }
    @Override
    public void teleopPeriodic() {

        //Sets up arcade drive
        m_drive.arcadeDrive(-m_joy.getY(), m_joy.getX());

        //Periodic logic for components
        BallShooter.periodic();

        //Prints out diagnostics
        looperCounter++;
        if (looperCounter >= 10) {
            printStats(); looperCounter = 0;
        }
    }

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

        BallShooter.reportStatistics();

    }
}