package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.*;
import frc.robot.subsystems.led.LedSubsystem;
import frc.robot.subsystems.led.LedSubsystem.LedMode;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;

public class LocalSwerve extends Command{

    private final Swerve m_swerve;
    private final LedSubsystem m_led;
    private final Pose2d targetPose;
    private final boolean precise;
    
    private final double positionIZone = 4;
    private final double rotationIZone = 4;
    private final double positionKS = 0.04;
    private final double rotationKS = 0.02;

    public LocalSwerve(Swerve m_swerve, Pose2d targetPose, boolean precise, LedSubsystem led){
        super();
        m_led = led;
        targetPose = Swerve.flipIfRed(targetPose);

        this.m_swerve = m_swerve;
        this.targetPose = targetPose;
        this.precise = precise;
        SmartDashboard.putBoolean("Precise?", precise);
        SmartDashboard.putString("target pose", targetPose.toString());
        addRequirements(m_swerve);
      
        m_swerve.xPID.setIZone(positionIZone); // Only use Integral term within this range
        m_swerve.xPID.setIntegratorRange(-positionKS * 2, positionKS * 2);
        m_swerve.xPID.setSetpoint(Units.metersToInches(targetPose.getX()));

        m_swerve.yPID.setIZone(positionIZone); // Only use Integral term within this range
        m_swerve.yPID.setIntegratorRange(-positionKS * 2, positionKS * 2);
        m_swerve.yPID.setSetpoint(Units.metersToInches(targetPose.getY())); // TODO Set derivative, too

        m_swerve.rPID.enableContinuousInput(-180.0, 180.0);
        m_swerve.rPID.setIZone(rotationIZone); // Only use Integral term within this range
        m_swerve.rPID.setIntegratorRange(rotationKS * 2, rotationKS * 2);
        m_swerve.rPID.setSetpoint(targetPose.getRotation().getDegrees());

    }

    @Override
    public void initialize() {
        super.initialize();

        m_swerve.xPID.reset();
        m_swerve.yPID.reset();
        m_swerve.rPID.reset();
    }

    @Override
    public void execute() {
        Pose2d pose = m_swerve.getPose();
        Translation2d position = pose.getTranslation();
        Rotation2d rotation = pose.getRotation();

        double xCorrection = m_swerve.xPID.calculate(Units.metersToInches(position.getX()));
        double xFeedForward = positionKS * Math.signum(xCorrection);
        double xVal = MathUtil.clamp(xCorrection + xFeedForward, -1.0, 1.0);

        double yCorrection = m_swerve.yPID.calculate(Units.metersToInches(position.getY()));
        double yFeedForward = positionKS * Math.signum(yCorrection);
        double yVal = MathUtil.clamp(yCorrection + yFeedForward, -1.0, 1.0);

        double correction = m_swerve.rPID.calculate(rotation.getDegrees());
        double feedForward = rotationKS * Math.signum(correction);
        double rotationVal = MathUtil.clamp(correction + feedForward, -1.0, 1.0);
        
        // turn on color before autoaligning
        m_led.setModeAndColors(LedMode.STROBE, Color.kRed, Color.kPurple);

        /* Drive */
        
        m_swerve.drive(
            0.0,
            0.0,
            new Translation2d(xVal, yVal).times(Swerve.maxSpeed.get()), 
            rotationVal * Swerve.maxAngularVelocity.get(),
            true, false, false
         );
    }

    @Override
    public boolean isFinished() {
        return m_swerve.xPID.atSetpoint() && m_swerve.yPID.atSetpoint() && m_swerve.rPID.atSetpoint(); //&& m_swerve.visionDifference() < Units.inchesToMeters(1.5);
    }
    
    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            m_led.setModeAndColors(LedMode.WAVE, Color.kBlack, Color.kWhite);
        } else {
            m_led.setModeAndColors(LedMode.WAVE, Color.kGreen, Color.kBlue);
        }
    }

}

