package com.team7528.frc2020.Robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.team7528.frc2020.Robot.auto.AutoModeExecutor;
import com.team7528.frc2020.Robot.auto.modes.*;
import com.team7528.frc2020.Robot.components.BallShooter;
import com.team7528.frc2020.Robot.components.PowerCellHolder;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

@SuppressWarnings("FieldCanBeLocal")
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

    private Double fineControlSpeedDouble;
    private Double deadBandOption;

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
        m_leftFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_rightFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        m_leftFront.setSensorPhase(false);
        m_rightFront.setSensorPhase(true); //Right side encoder is inverted

        //Initialize components
        BallShooter.init();
        PowerCellHolder.init();

        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Defines that the back motors follows the front motors
        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

        // Invert the right motors
        m_rightFront.setInverted(true);

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
        SmartDashboard.putData("Auto Mode", autoPicker);

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
        CameraServer.getInstance().startAutomaticCapture();
    }

    @Override
    public void robotPeriodic() {
        m_blinkin.periodic(); //Set blinkin pattern during robot operation
    }

    @Override
    public void autonomousInit() {
        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);
        PowerCellHolder.resetEncoder();

        //Start Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.start();

        //Defines the value for dead band
        deadBandOption = deadBandOptions.getSelected();

        m_drive.setDeadband(deadBandOption);

        //Set LED to alliance color
        m_blinkin.setToTeamColor(false);
    }

    /**
     * The code will be active during autonomous periodic
     */
    @Override
    public void autonomousPeriodic() {
        //Prints Stats during auto
        looperCounter++;
        if (looperCounter >= 10) {
            printStats();
            looperCounter = 0;
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
        fineControlSpeedDouble = fineControlSpeed.getSelected();
    }

    /**
     * This code will be active during teleop periodic
     */
    @Override
    public void teleopPeriodic() {
        //Periodic logic for components
        BallShooter.periodic();

        //Sets up for fine control

        if (m_joy.getPOV() == 0) { //Forward
            m_drive.arcadeDrive(fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 90) { //Right
            m_drive.arcadeDrive(0,fineControlSpeedDouble);
        } else if (m_joy.getPOV() == 180) { //Backward
            m_drive.arcadeDrive(-fineControlSpeedDouble,0);
        } else if (m_joy.getPOV() == 270) { //Left
            m_drive.arcadeDrive(0,-fineControlSpeedDouble);
        } else {
            //Sets up arcade drive
            m_drive.arcadeDrive(-m_joy.getY(), m_joy.getX());
        }
        //Prints out diagnostics
        looperCounter++;
        if (looperCounter >= 10) {
//            printStats();
            looperCounter = 0;
        }
        PowerCellHolder.reportTelemetry();
    }

    @Override
    public void disabledInit() {
        m_blinkin.rainbow(); //Sets blinkin to use rainbow pattern
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