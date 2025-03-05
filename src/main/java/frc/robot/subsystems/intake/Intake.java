package frc.robot.subsystems.intake;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotState;
import frc.robot.subsystems.intake.IntakeIOInputsAutoLogged;
import frc.robot.subsystems.intake.IntakeVisualizer;
import frc.robot.util.LoggedTunableNumber;
import static edu.wpi.first.units.Units.*;

import org.littletonrobotics.junction.Logger;


public class Intake extends SubsystemBase{
    
    // PID values
    private static final LoggedTunableNumber kP = new LoggedTunableNumber("Pivot/Gains/kP", 0.15);
    private static final LoggedTunableNumber kI = new LoggedTunableNumber("Pivot/Gains/kI", 0.0);
    private static final LoggedTunableNumber kD = new LoggedTunableNumber("Pivot/Gains/kD", 0.0);

    // Feedforward values
    private static final LoggedTunableNumber kS = new LoggedTunableNumber("Pivot/Gains/kS", 0.1);
    private static final LoggedTunableNumber kV = new LoggedTunableNumber("Pivot/Gains/kV", 1.45);
    private static final LoggedTunableNumber kA = new LoggedTunableNumber("Pivot/Gains/kA", 0.1);
    private static final LoggedTunableNumber kG = new LoggedTunableNumber("Pivot/Gains/kG", 0.1);
    
    private final IntakeIOInputsAutoLogged inputs = new IntakeIOInputsAutoLogged();

    private final IntakeVisualizer measuredVisualizer;
    private final IntakeVisualizer setpointVisualizer;
    private final IntakeVisualizer goalVisualizer;
    private final IntakeIO io;

    private final RobotState actual;
    private final RobotState target;
    private final RobotState goal;

    public Intake(IntakeIO io) {
        this.io = io;

        this.io.setPID(0.15, 0, 0);
        this.io.setPID(kP.get(), kI.get(), kD.get());
        this.io.setFF(kS.get(), kG.get(), kV.get(), kA.get());
        this.actual = RobotState.getMeasuredInstance();
        this.target = RobotState.getDesiredInstance();
        this.goal = RobotState.getGoalInstance();
    
        this.measuredVisualizer = new IntakeVisualizer("Measured", Color.kWhite);
        this.setpointVisualizer = new IntakeVisualizer("Setpoint", Color.kBlue);
        this.goalVisualizer = new IntakeVisualizer("Goal", Color.kGreen);
    }

    public Command setIntakeSpeed(double speed){
        return runOnce(() -> this.io.setSpeed(speed));
    }

    public boolean hasCoral(){
        if(this.inputs.supplyCurrent.gt(Amps.of(20))){
            return true;
        }
        return false;
    }

    @Override
    public void periodic(){
        super.periodic();
        this.io.updateInputs(inputs);
        Logger.processInputs("Intake", inputs);
    }
}
