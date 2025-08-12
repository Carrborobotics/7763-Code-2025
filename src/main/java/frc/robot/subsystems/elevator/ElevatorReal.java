package frc.robot.subsystems.elevator;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import frc.robot.Constants;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static edu.wpi.first.units.Units.*;

public class ElevatorReal implements ElevatorIO {

    private SparkFlex elevatorLeft;
    private SparkFlex elevatorRight;
    SparkFlexConfig elevatorLeftConfig;
    SparkFlexConfig elevatorRightConfig;
    private SparkClosedLoopController closedLoopControllerLeft;

    // units are m/s and m/s^2
    private final TrapezoidProfile m_profile = new TrapezoidProfile(new TrapezoidProfile.Constraints(100,65));

    private TrapezoidProfile.State m_goal = new TrapezoidProfile.State();
    private TrapezoidProfile.State m_setpoint = new TrapezoidProfile.State();
    
    // Could also use private final ElevatorFeedforward = new ElevatorFeedforward( kS, kG, kV, kA);
    // here but trying to keep it simple
    private final SimpleMotorFeedforward m_feedforward = new SimpleMotorFeedforward(1, 1.5);


    public ElevatorReal() {
        elevatorLeft = new SparkFlex(Constants.CANConstants.elevatorLeftId, MotorType.kBrushless);
        elevatorLeftConfig = new SparkFlexConfig();

        elevatorRight = new SparkFlex(Constants.CANConstants.elevatorRightId, MotorType.kBrushless);
        elevatorRightConfig = new SparkFlexConfig();

        elevatorLeftConfig.inverted(true);
        elevatorLeftConfig.idleMode(IdleMode.kBrake);
        elevatorLeftConfig
            .closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .pid(0.175,0,0) // 0.035, 0, 0
                .outputRange(-1, 1);

        elevatorRightConfig.follow(Constants.CANConstants.elevatorLeftId,true);

        elevatorLeft.configure(elevatorLeftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        elevatorRight.configure(elevatorRightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        closedLoopControllerLeft = elevatorLeft.getClosedLoopController();
        elevatorLeft.getEncoder().setPosition(0);
    }

    @Override
    public void runSetpoint(Distance position) {
        double level = position.in(Inches);

        m_goal = new TrapezoidProfile.State(level, 0); // should have 0 velicty at the goal point
    }

    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        
        inputs.position.mut_replace(elevatorLeft.getAbsoluteEncoder().getPosition(), Meters);
        inputs.velocity.mut_replace(elevatorLeft.getAbsoluteEncoder().getVelocity(), MetersPerSecond);
        
        inputs.appliedVoltsLeader.mut_replace(elevatorLeft.getBusVoltage(), Volts);
        inputs.appliedVoltsFollower.mut_replace(elevatorRight.getBusVoltage(), Volts);

        inputs.supplyCurrentLeader.mut_replace(elevatorLeft.getOutputCurrent(), Amps);
        inputs.supplyCurrentFollower.mut_replace(elevatorRight.getOutputCurrent(), Amps);

        inputs.torqueCurrentLeader.mut_replace(elevatorLeft.getOutputCurrent(), Amps);
        inputs.torqueCurrentFollower.mut_replace(elevatorRight.getOutputCurrent(), Amps);

        inputs.temperatureLeader.mut_replace(elevatorLeft.getMotorTemperature(), Celsius);
        inputs.temperatureFollower.mut_replace(elevatorRight.getMotorTemperature(), Celsius);
    }

    public void runVolts(Voltage volts) {
        elevatorLeft.setVoltage(volts);
        elevatorRight.setVoltage(volts);
    }
    
    public void stop() {
        runVolts(Volts.of(0));
    }

    public Distance getPosition() {
        return Inches.of(elevatorLeft.getEncoder().getPosition());
    }
    
    @Override
    public void periodic() {
        // recalculate evert 20ms
        m_setpoint = m_profile.calculate(0.02, m_setpoint, m_goal);
        SmartDashboard.putNumber("ElevatorREAL/Left position", elevatorLeft.getEncoder().getPosition());
        SmartDashboard.putNumber("ElevatorREAL/Left velocity", elevatorLeft.getEncoder().getVelocity());
        closedLoopControllerLeft.setReference(
            m_setpoint.position,
            SparkFlex.ControlType.kPosition,
            ClosedLoopSlot.kSlot0,
            m_feedforward.calculate(m_setpoint.velocity) / 12.0); // velocity is from example code
    }




}
