package com.team7528.lib;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Spark;

/**
 * This class represents a REV Blinkin LED Driver and provides useful
 * functions for using one
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Blinkin {
    private double PATTERN;
    private Spark m_blinkin;

    public Blinkin(int PWMChannel) {
        m_blinkin = new Spark(PWMChannel);
    }

    /**
     * Runs the blinkin loop and sends the pattern to the Blinkin.
     *
     * <br>
     * Additionally, detects if an E-Stop has been pressed and sets LEDs accordingly
     */
    public void periodic() {
        m_blinkin.set(PATTERN);

        if(DriverStation.getInstance().isEStopped()) { //If Emergency Stop is pressed by driver or FTA
            error(); //Flash red
        }
    }

    /**
     * Sets LEDs to team alliance color
     *
     * @param solidColor If true, the LED will show as one solid color. If false, it will show as a light chase
     */
    public void setToTeamColor(boolean solidColor) {
        if(DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue) { //If on blue alliance
            if(solidColor) { //If we want a solid color
                PATTERN = 0.87; //Set pattern to solid blue
            } else { //Else we don't want solid color
                PATTERN = -0.29; //Set pattern to light chase blue
            }
        } else if(DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Red) { //If on red alliance
            if(solidColor) { //If we want a solid color
                PATTERN = 0.61; //Set pattern to solid red
            } else { //Else we don't want solid color
                PATTERN = -0.31; //Set pattern to light chase red
            }
        } else {
            error();
        }
    }

    /**
     * Uses the strobe red light pattern to indicate to the driver that a fatal
     * error has occurred, or an E-Stop has been pressed by driver or FTA
     */
    public void error() {
        PATTERN = -0.11;
    }

    /**
     * Should be called in robotInit(). If connected to an FMS (ie we are in competition),
     * sets the lights to the alliance. If not, uses a rainbow strobe pattern
     */
    public void init() {
        if(DriverStation.getInstance().isFMSAttached()) { //If we are on a competition field (FMS is attached)
            setToTeamColor(true); //Set to alliance color
        } else { //If we are NOT hooked to FMS
            rainbow(); //Set to rainbow palette
        }
    }

    /**
     * Sets LEDs to use strobe gold pattern
     */
    public void strobeGold() {
        PATTERN = -0.07;
    }

    /**
     * Sets the LEDs to use a rainbow palette
     */
    public void rainbow() {
        PATTERN = -0.99;
    }
}
