package com.team7528.frc2020.Robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

public class Robot extends TimedRobot {

    private DifferentialDrive m_drive; //Creates a new variable for how the wheels of the robot will move

    //Prints out stats regarding the string builder
    private StringBuilder _sb = new StringBuilder();
    private int looperCounter = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

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

        //Defines that the back motors follows the front motors
        m_leftAft.follow(m_leftFront);
        m_rightAft.follow(m_rightFront);

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

    /**
     * The code will be active during teleop periodic
     */

    @Override
    public void teleopPeriodic() {

        //Sets up arcade drive
        m_drive.arcadeDrive(-m_joy.getY(), m_joy.getX());

        //Prints out diagnostics
        looperCounter++;
        if (looperCounter >= 10) { printStats(); looperCounter = 0; }
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

    }
}