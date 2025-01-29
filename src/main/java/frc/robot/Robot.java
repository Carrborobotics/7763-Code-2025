// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot;

// import edu.wpi.first.util.sendable.SendableRegistry;
// import edu.wpi.first.wpilibj.TimedRobot;
// import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj.XboxController;
// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

// /**
//  * The methods in this class are called automatically corresponding to each mode, as described in
//  * the TimedRobot documentation. If you change the name of this class or the package after creating
//  * this project, you must also update the manifest file in the resource directory.
//  */
// public class Robot extends TimedRobot {
//   private final PWMSparkMax m_leftDrive = new PWMSparkMax(0);
//   private final PWMSparkMax m_rightDrive = new PWMSparkMax(1);
//   private final DifferentialDrive m_robotDrive =
//       new DifferentialDrive(m_leftDrive::set, m_rightDrive::set);
//   private final XboxController m_controller = new XboxController(0);
//   private final Timer m_timer = new Timer();

//   /** Called once at the beginning of the robot program. */
//   public Robot() {
//     SendableRegistry.addChild(m_robotDrive, m_leftDrive);
//     SendableRegistry.addChild(m_robotDrive, m_rightDrive);

//     // We need to invert one side of the drivetrain so that positive voltages
//     // result in both sides moving forward. Depending on how your robot's
//     // gearbox is constructed, you might have to invert the left side instead.
//     m_rightDrive.setInverted(true);
//   }

//   /** This function is run once each time the robot enters autonomous mode. */
//   @Override
//   public void autonomousInit() {
//     m_timer.restart();
//   }

//   /** This function is called periodically during autonomous. */
//   @Override
//   public void autonomousPeriodic() {
//     // Drive for 2 seconds
//     if (m_timer.get() < 2.0) {
//       // Drive forwards half speed, make sure to turn input squaring off
//       m_robotDrive.arcadeDrive(0.5, 0.0, false);
//     } else {
//       m_robotDrive.stopMotor(); // stop robot
//     }
//   }

//   /** This function is called once each time the robot enters teleoperated mode. */
//   @Override
//   public void teleopInit() {}

//   /** This function is called periodically during teleoperated mode. */
//   @Override
//   public void teleopPeriodic() {
//     m_robotDrive.arcadeDrive(-m_controller.getLeftY(), -m_controller.getRightX());
//   }

//   /** This function is called once each time the robot enters test mode. */
//   @Override
//   public void testInit() {}

//   /** This function is called periodically during test mode. */
//   @Override
//   public void testPeriodic() {}
// }

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run(); 
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}
}
