package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.drumSpinner;
import static com.team7528.frc2020.Robot.common.RobotMap.m_gamepad;

/**
 * Represents our Power Cell Holder. When start is pressed, it shifts between positions
 * like a revolver. The amount of ticks between positions can be adjusted with the
 * ticksBetweenPositions variable. This variable, as well as the tolerance, kP, and kI
 * values should be tuned prior to use.
 */
@SuppressWarnings("FieldCanBeLocal")
public class PowerCellHolder {
    private static final double ticksBetweenPositions = 250.0; //Number of encoder ticks between positions
    private static final double integrator_limit = 1.0; //Max error sum for integrator
    private static final double tolerance = 10; //Tolerance for how many ticks it can be away
    private static final double kP = 1.0; //Proportional Constant
    private static final double kI = 0.0; //Integrator Constant
    private static double setpoint,error,errorSum;

    private static boolean isGoingToNextPosition = false; //Should rename this to something less dumb


    public static void init() {
        drumSpinner.configFactoryDefault(); //Config Talon to factory default
        drumSpinner.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder); //Config Talon to use a quad encoder
        drumSpinner.setSelectedSensorPosition(0,0,10); //Reset Encoder position

        //Broadcast PI terms (and tolerance) to Shuffleboard
        SmartDashboard.putNumber("Power Cell Holder P term", kP);
        SmartDashboard.putNumber("Power Cell Holder I term", kI);
        SmartDashboard.putNumber("Power Cell Holder Tolerance", tolerance);
    }

    /**
     * Reset encoder position to 0 ticks
     */
    public static void resetEncoder() {
        drumSpinner.setSelectedSensorPosition(0,0,10);
    }

    public static void periodic() {

        if(m_gamepad.getStartButtonPressed() && !isGoingToNextPosition) {
            isGoingToNextPosition = true;
            setpoint = drumSpinner.getSelectedSensorPosition() + ticksBetweenPositions;
        }

        if(isGoingToNextPosition) {
            //PI to next position here
            double drive; //Value to be applied to the motor

            error = setpoint - drumSpinner.getSelectedSensorPosition(); //Collect error
            errorSum += error; //Increment error sum for integrator

            if(errorSum > integrator_limit) { //If error sum is too large
                errorSum = integrator_limit; //Set error sum to max value
            } else if(errorSum < -integrator_limit) { //If error sum is below the minimum value
                errorSum = -integrator_limit; //Set it to the max negative value
            }

            drive = kP * error + kI * errorSum; //Calculate PI loop
            drumSpinner.set(ControlMode.PercentOutput,drive); //Drive motor based on PI results

            if(Math.abs(error) <= tolerance) { //If we are within our tolerance level
                isGoingToNextPosition = false; //Disable PI loop
                errorSum = 0; //Reset integrator for next go around
            }
        }
    }

    /**
     * Reports telemetry to Driver Station. In this case, reports whether or not we're spinning
     */
    public static void reportTelemetry() {
        SmartDashboard.putBoolean("DRUM SPIN", isGoingToNextPosition);
    }
}
