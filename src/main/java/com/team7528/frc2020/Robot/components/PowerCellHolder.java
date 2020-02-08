package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team7528.frc2020.Robot.common.RobotMap.*;

/**
 * Represents our Power Cell Holder. When start is pressed, it shifts between positions
 * like a revolver. The amount of ticks between positions can be adjusted with the
 * ticksBetweenPositions variable. This variable, as well as the tolerance, kP, and kI
 * values should be tuned prior to use.
 *
 * <br>
 * NOTE: The holder should be configured with the actuator extended over an empty power cell
 * slot for this to work right.
 *
 * @author Ethan Hanlon
 */
@SuppressWarnings("FieldCanBeLocal")
public class PowerCellHolder {
    private static final double ticksBetweenPositions = 250.0; //Number of encoder ticks between positions
    private static final double integrator_limit = 1.0; //Max error sum for integrator
    private static final double tolerance = 10; //Tolerance for how many ticks it can be away
    private static final double kP = 1.0; //Proportional Constant
    private static final double kI = 0.0; //Integrator Constant
    private static double setpoint,error,errorSum;

    private static boolean waitingToChangePosition = false; //Should rename this to something less dumb


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

    /**
     * Periodic logic for the Power Cell Holder. Uses a PI loop to maintain position and
     * retracts and extends the actuator for
     */
    public static void periodic() {
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

        //If start is pressed and we are NOT already moving
        if(m_gamepad.getStartButtonPressed() && !waitingToChangePosition) {
            SmartDashboard.putBoolean("DRUM READY", true); //Alert operator
            waitingToChangePosition = true;
        }

        if(Math.abs(error) <= tolerance) { //If we are within our tolerance level
            SmartDashboard.putBoolean("DRUM READY", false); //Alert operator
        }

        if(waitingToChangePosition) { //If we want to be moving
            if(!Flywheel.justShot) { //And we didn't just shoot
                setpoint = drumSpinner.getSelectedSensorPosition() + ticksBetweenPositions; //Add to PI setpoint
                waitingToChangePosition = false;
            }
        }
    }

    /**
     * Unused
     */
    public static void reportStatistics() {
    }
}
