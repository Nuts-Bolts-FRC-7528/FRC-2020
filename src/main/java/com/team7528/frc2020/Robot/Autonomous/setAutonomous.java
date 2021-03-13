package com.team7528.frc2020.Robot.Autonomous;

import com.team7528.frc2020.Robot.Robot;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class setAutonomous {

    // Create config for Trajectory
    public static TrajectoryConfig config = new TrajectoryConfig(2,4) // These numbers do not matter in terms of Boba Bots
            // Add kinematics to ensure max speed is actually obeyed
            .setKinematics(Robot.kinematics)
            // Voltage Constraint
            .addConstraint(new DifferentialDriveVoltageConstraint(Robot.feedforward, Robot.kinematics, 10));
            Trajectory exampleTrajectory;
            private setAutonomous() {
                try {
                    exampleTrajectory = TrajectoryUtil.fromPathweaverJson(Paths.get("/home/lvuser/deploy/paths/InitiationToTarget.json"));
                } catch(IOException e) {
                    exampleTrajectory = TrajectoryGenerator.generateTrajectory(
                            // Start at the origin facing the +X direction
                            new Pose2d(0,5,new Rotation2d(0)),
                            // Pass through these two interior waypoints, making an 's' curve path
                            List.of(
                                    new Translation2d(1, 5.5),
                                    new Translation2d(2, 4.5)
                            ),
                            // End 3 meters straight ahead of where we started, facing forward
                            new Pose2d(3, 5, new Rotation2d(0)),
                            // Pass config
                            config
                    );


                }
            }
            private static setAutonomous instance;
            public static  setAutonomous getInstance() {
                if (instance == null) instance = new setAutonomous();
                return instance;
            }
            public Trajectory getAutonomousTrajectory() {
                return exampleTrajectory;
            }
          /*public Command getAutonomousCommand()   {
                 return new TrajectoryTracker(exampleTrajectory,true).andThen();
            } */

}
