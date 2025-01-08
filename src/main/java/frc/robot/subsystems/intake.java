package frc.robot.subsystems;

import com.revrobotics.spark.SparkFlex;
import java.io.ObjectInputFilter.Config;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class intake extends SubsystemBase {
    private SparkFlex intakeCoral;
    private SparkFlex intakeAlgae;

    public double intakeForwardSpeedReq;
    public double intakeBackwardSpeedReq;


    public void intakeSubsytem() {
        intakeCoral = new SparkFlex(Constants.CANConstants.intakeCoralId, MotorType.kBrushless);
        intakeAlgae = new SparkFlex(Constants.CANConstants.intakeAlgaeId, MotorType.kBrushless);
        SparkFlexConfig config = new SparkFlexConfig();
        config
            .inverted(true)
            .idleMode(IdleMode.kBrake);
        config.encoder
            .positionConversionFactor(1000)
            .velocityConversionFactor(1000);
        config.closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            .pid(1.0, 0.0, 0.0);
        intakeCoral.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        intakeAlgae.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
        intakeForwardSpeedReq = 0;
        intakeBackwardSpeedReq = 0;
    }
    
    public void intakeForward(){
        intakeForwardSpeedReq = -1 * Constants.VortexMotorConstants.kFreeSpeedRpm;
        intakeCoral.set(intakeForwardSpeedReq);
        intakeAlgae.set(intakeForwardSpeedReq);
    }

    public void intakeBackward(){
        intakeBackwardSpeedReq = 1 * Constants.VortexMotorConstants.kFreeSpeedRpm;
        intakeCoral.set(intakeBackwardSpeedReq);
        intakeAlgae.set(intakeBackwardSpeedReq);
    }
    
}
