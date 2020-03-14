package com.team7528.frc2020.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.team7528.frc2020.Robot.auto.AutoModeExecutor;
import com.team7528.frc2020.Robot.auto.modes.*;
import com.team7528.frc2020.Robot.components.Flywheel;
import com.team7528.lib.GitVersionGetter;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.team7528.frc2020.Robot.common.RobotMap.*;
import static com.team7528.frc2020.Robot.auto.actions.DriveForwardActionGyro.ypr;
import static com.team7528.frc2020.Robot.components.Flywheel.d;

@SuppressWarnings({"SpellCheckingInspection", "FieldCanBeLocal"})
public class Robot extends TimedRobot {

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
    private AutoModeExecutor shootMoveBackAuto = new AutoModeExecutor(new ShootMoveBackAuto());

    private Double fineControlSpeedDouble;

    private SendableChooser<AutoModeExecutor> autoPicker = new SendableChooser<>();
    private SendableChooser<Double> fineControlSpeed = new SendableChooser<>();
    private SendableChooser<Double> deadBandOptions = new SendableChooser<>();
    private SendableChooser<Boolean> flywheelEncoderChooser = new SendableChooser<>();

    private final double kTurretAlignmentP = 0.05;
    private final double kTurretSpeedLimit = 0.2;
    private final int kTurretLimit = 4800;

    /**
     * Initiates motor controller set up
     */

    @Override
    public void robotInit() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        StringBuilder _initSb = new StringBuilder(); //Holds startup data

        //Print info about the repository at large on robot startup
        _initSb.append("\nROBOT REPOSITORY: ").append(GitVersionGetter.MAVEN_NAME);
        _initSb.append("\nGIT COMMIT NUMBER: ").append(GitVersionGetter.GIT_REVISION);
        _initSb.append("\nLAST GIT COMMIT HASH: ").append(GitVersionGetter.GIT_SHA);
        _initSb.append("\nLAST GIT COMMIT DATE: ").append(GitVersionGetter.GIT_DATE);
        _initSb.append("\nROBOT CODE BUILD DATE: ").append(GitVersionGetter.BUILD_DATE);
        _initSb.append("\n-----\n");

        //Initialize components
        Flywheel.init();

        //Turret setup
        turretRotator.configFactoryDefault();
        turretRotator.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        turretRotator.setSelectedSensorPosition(0,0,10);
        turretRotator.setInverted(true);
        turretRotator.setSensorPhase(true);
        turretRotator.configReverseSoftLimitThreshold(-kTurretLimit,10);
        turretRotator.configForwardSoftLimitThreshold(kTurretLimit,10);
        turretRotator.configForwardSoftLimitEnable(true,10);
        turretRotator.configReverseSoftLimitEnable(true,10);


        //Defines a new differential drive
        m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

        //Reset flywheel motors
        leftFlywheelMaster.configFactoryDefault();
        rightBackupFlywheelMaster.configFactoryDefault();
        leftFlywheelSlave.configFactoryDefault();
        rightFlywheelSlave.configFactoryDefault();

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
        autoPicker.setDefaultOption("Shoot and Move Back", shootMoveBackAuto);
        autoPicker.addOption("Do Nothing", doNothingAuto);
        autoPicker.addOption("Move Forward (Gyro)", moveForwardAutoGyro);
        autoPicker.addOption("Move Forward (Encoder)", moveForwardAutoEncoder);
        autoPicker.addOption("Move Forward (Feet)", driveForwardAutoFeet);
        autoPicker.addOption("Turn Right (One Wheel)", oneWheelTurnRightAuto);
        autoPicker.addOption("Turn Around (One Wheel)", oneWheelTurnBackAuto);
        autoPicker.addOption("Turn Left (One Wheel)", oneWheelTurnLeftAuto);
        autoPicker.addOption("Turn Right", rightTurnAuto);
        autoPicker.addOption("Turn Around", uTurnAuto);
        autoPicker.addOption("Turn Left", leftTurnAuto);
        autoPicker.addOption("Shoot & Move Back", shootMoveBackAuto);
        Shuffleboard.getTab("DRIVETRAIN").add("Auto Mode", autoPicker);

        //Flywheel encoder chooser
        flywheelEncoderChooser.setDefaultOption("Primary",false);
        flywheelEncoderChooser.addOption("Backup",true);
        Shuffleboard.getTab("DRIVETRAIN").add("Backup Flywheel Encoder", flywheelEncoderChooser);

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

        SmartDashboard.putNumber("Yaw",ypr[0]);

        SmartDashboard.putNumber("Left Encoder", m_leftFront.getSelectedSensorPosition());
        SmartDashboard.putNumber("Right Encoder", m_rightFront.getSelectedSensorPosition());
        SmartDashboard.putNumber("Turret Encoder", turretRotator.getSelectedSensorPosition());
        SmartDashboard.putNumber("Flywheel Velocity", rightBackupFlywheelMaster.getSelectedSensorVelocity());

        if(m_gamepad.getStickButtonPressed(GenericHID.Hand.kRight)) {
            limelightTable.getEntry("ledMode").setNumber(0);
            limelightTable.getEntry("camMode").setNumber(0);
        }

        if(m_gamepad.getStickButtonPressed(GenericHID.Hand.kLeft)) {
            limelightTable.getEntry("ledMode").setNumber(1);
            limelightTable.getEntry("camMode").setNumber(1);
        }
    }

    @Override
    public void autonomousInit() {

        m_drive.setSafetyEnabled(false);

        //Reset encoders to 0
        m_leftFront.setSelectedSensorPosition(0,0,10);
        m_rightFront.setSelectedSensorPosition(0,0,10);

        //Set up for which encoder is chosen
        Flywheel.isUsingBackupEncoder = flywheelEncoderChooser.getSelected();

        //Start Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.start();

        //Defines the value for dead band
        m_drive.setDeadband(deadBandOptions.getSelected());

        //Set LED to alliance color
        m_blinkin.setToTeamColor(false);

        //Initialize Components
        Flywheel.init();
    }

    /**
     * The code will be active during autonomous periodic
     */
    @Override
    public void autonomousPeriodic() {
        Flywheel.periodic();
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

        if(m_gamepad.getBumper(GenericHID.Hand.kRight)) { //Deploy intake (DEBUG)
            intake.set(DoubleSolenoid.Value.kForward);
        }
        if(m_gamepad.getBumper(GenericHID.Hand.kLeft)) { //Retract intake (DEBUG)
            intake.set(DoubleSolenoid.Value.kReverse);
        }

        //Drive flywheel (DEBUG)
        leftFlywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));
        rightBackupFlywheelMaster.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));
        leftFlywheelSlave.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));
        rightFlywheelSlave.set(ControlMode.PercentOutput, m_gamepad.getY(GenericHID.Hand.kRight));

        //Drive intake, divide inputs by 5 as the motor can move very fast (DEBUG)
        intakeMotor.set(TalonFXControlMode.PercentOutput,m_gamepad.getY(GenericHID.Hand.kLeft)/5.0);

        //Move pulleys (DEBUG)
        if(m_gamepad.getAButton()) {
            horizontalPulley.set(ControlMode.PercentOutput,-0.25);
        } else {
            horizontalPulley.set(ControlMode.PercentOutput,0);

        }

        //If trough aft sensor is tripped, spin vertical pulley
        /*if(!troughAft.get()) {
            verticalPulley.set(ControlMode.PercentOutput,0.25);
        } else {
            verticalPulley.set(ControlMode.PercentOutput,0);
        }

        //If trough front sensor is tripped, spin horizontal pulley
        if(!troughFront.get()) {
            horizontalPulley.set(ControlMode.PercentOutput,-0.25);
        } else {
            horizontalPulley.set(ControlMode.PercentOutput,0);
        }*/

        if ((m_gamepad.getBButton() || m_gamepad.getXButton()) && limelightTable.getEntry("tv").getDouble(0) == 1) { //If either B or X is pressed
            double seek_adjust = kTurretAlignmentP * limelightTable.getEntry("tx").getDouble(0);
            if (seek_adjust > kTurretSpeedLimit) {
                seek_adjust = kTurretSpeedLimit;
            } else if (seek_adjust < -kTurretSpeedLimit) {
                seek_adjust = -kTurretSpeedLimit;
            }
            turretRotator.set(ControlMode.PercentOutput, seek_adjust);
            SmartDashboard.putNumber("seek_adjust_", -seek_adjust);
        } else if(m_gamepad.getXButton()) { //If X is pressed, go left with 20% power
            turretRotator.set(ControlMode.PercentOutput, -kTurretSpeedLimit);
        } else if (m_gamepad.getBButton()) { //If B is pressed, go right with 20% power
            turretRotator.set(ControlMode.PercentOutput, kTurretSpeedLimit);
        } else { //If no buttons pressed, do not move the turret
            turretRotator.set(ControlMode.PercentOutput, 0.0);
        }
    }

    @Override
    public void disabledInit() {
        m_blinkin.rainbow(); //Sets blinkin to use rainbow pattern

        //Stops Auto
        AutoModeExecutor chosenAuto = autoPicker.getSelected();
        chosenAuto.stop();
        driveForwardAutoFeet.stop();
    }
}