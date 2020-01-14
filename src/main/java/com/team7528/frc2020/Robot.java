package com.team7528.frc2020;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class Robot extends TimedRobot {

    private WPI_TalonSRX m_leftFront;
    private WPI_TalonSRX m_leftAft;
    private WPI_TalonSRX m_rightFront;
    private WPI_TalonSRX m_rightAft;

    private DifferentialDrive m_drive;

    private Joystick m_joy;

    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

    @Override
    public void robotInit() {

        StringBuilder _initSb = new StringBuilder();

        m_leftFront = new WPI_TalonSRX(0);
        m_rightFront = new WPI_TalonSRX(1);
        m_leftAft = new WPI_TalonSRX(2);
        m_rightAft = new WPI_TalonSRX(3);

        m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

        m_leftFront.configFactoryDefault();
        m_rightFront.configFactoryDefault();
        m_leftAft.configFactoryDefault();
        m_rightAft.configFactoryDefault();

        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

        m_joy = new Joystick(0);

        File file = new File(Robot.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        _initSb.append("ROBOT.JAVA LAST REVISED:").append(sdf.format(file.lastModified()));

        _initSb.append("\n-----\nLEFT FRONT DRIVETRAIN FIRM:").append(m_leftFront.getFirmwareVersion());
        _initSb.append("\nLEFT AFT DRIVETRAIN FIRM:").append(m_leftAft.getFirmwareVersion());
        _initSb.append("\nRIGHT FRONT DRIVETRAIN FIRM:").append(m_rightFront.getFirmwareVersion());
        _initSb.append("\nRIGHT AFT DRIVETRAIN FIRM:").append(m_rightAft.getFirmwareVersion());

        System.out.println(_initSb);

        Shuffleboard.getTab("DRIVETRAIN").add(m_drive);

    }

    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();

    }

    @Override
    public void teleopPeriodic() {

        m_drive.arcadeDrive(-m_joy.getY(), m_joy.getX());

        looperCounter++;
        if (looperCounter >= 10) { printStats(); looperCounter = 0; }
    }
    private void printStats() {
        _sb.append("**********");
        _sb.append("\ntimestamp").append(sdf.format(Instant.now().getEpochSecond()));
        _sb.append("\tleft-front").append(m_leftFront.get());
        _sb.append("\tright-front").append(m_rightFront.get());
        _sb.append("\tleft-aft").append(m_leftAft.get());
        _sb.append("\tright-aft").append(m_rightAft.get());
        _sb.append("\n**********");

        System.out.println(_sb.toString());

    }
}