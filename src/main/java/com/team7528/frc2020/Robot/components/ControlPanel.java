package com.team7528.frc2020.Robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import static com.team7528.frc2020.Robot.common.RobotMap.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;

public class ControlPanel {
    public static void init() {

    }
    public static void periodic() {

        if(m_gamepad.getPOV() ==180) { // D-Pad down
            String gameData;
            gameData = DriverStation.getInstance().getGameSpecificMessage(); //Add method to determine color
            if(gameData.length() > 0) {
                switch (gameData.charAt(0)) {
                    case 'B' :
                        //Blue case code
                        if (colors.getColor() != Color.kBlue) {
                            controlPanelWheel.set(ControlMode.PercentOutput,1);
                        }
                        break;
                    case 'G' :
                        //Green case code
                        if (colors.getColor() != Color.kGreen) {
                            controlPanelWheel.set(ControlMode.PercentOutput,1);
                        }
                        break;
                    case 'R' :
                        //Red case code
                        if (colors.getColor() != Color.kRed) {
                            controlPanelWheel.set(ControlMode.PercentOutput,1);
                        }
                        break;
                    case 'Y' :
                        //Yellow case code
                        if (colors.getColor() != Color.kYellow) {
                            controlPanelWheel.set(ControlMode.PercentOutput,1);
                        }
                        break;
                    default :
                        //This is corrupt data
                        System.out.println("This is corrupt data");
                        break;
                }
            } else if (colors.getColor() == Color.kYellow);{
                controlPanelWheel.set(ControlMode.PercentOutput,1);
                

            }
        }
        if(m_gamepad.getPOV() == 0) { // D-Pad Up
            if(m_wheelEjector.get() ==DoubleSolenoid.Value.kForward) {
                m_wheelEjector.set(DoubleSolenoid.Value.kReverse); // If piston extended, retract
            }
            if (m_wheelEjector.get() == DoubleSolenoid.Value.kReverse) {
                m_wheelEjector.set(DoubleSolenoid.Value.kForward); // If piston isn't out, shoot out
            }
        }
    }
    public static void reportSatistics() {

    }

}
