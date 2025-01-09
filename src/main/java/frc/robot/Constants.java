package frc.robot;

import com.ctre.phoenix6.CANBus;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;


public final class Constants {
    public static final class XboxConstants {
        public static final int kDriverControllerPort = 0;
        public static final double kDriveDeadBand = 0.125;
    }

    public static final class VortexMotorConstants {
        public static final double kFreeSpeedRpm = 6784;
    }

    public static final class CANConstants {
        public static final int intakeCoralId = 13;
        public static final int intakeAlgaeId = 14;
        public static final int elevatorLeftId = 2;
        public static final int elevatorRightId = 3;
    }
}