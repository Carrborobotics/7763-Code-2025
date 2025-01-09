package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.XboxConstants;
import frc.robot.subsystems.intake;

public class RobotContainer {
    private static RobotContainer m_robot = null;

    public static void setRobot(RobotContainer robot){
        m_robot = robot;
    }

    public static RobotContainer getRobot(){
        return m_robot;
    }

    private final intake m_intake = new intake();

    XboxController m_driverController = new XboxController(XboxConstants.kDriverControllerPort);
    
    Trigger xButton = new JoystickButton(m_driverController, XboxController.Button.kX.value);
    Trigger yButton = new JoystickButton(m_driverController, XboxController.Button.kY.value);
    //Trigger aButton = new JoystickButton(m_driverController, XboxController.Button.kA.value);

    private void configureButtonBindings(){
        xButton.whileTrue(new InstantCommand(() -> m_intake.intakeForward()));
        yButton.whileTrue(new InstantCommand(() -> m_intake.intakeBackward()));

    }

public RobotContainer(){
    configureButtonBindings();

    
}



}
