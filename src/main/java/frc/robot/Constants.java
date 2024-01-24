package frc.robot;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.GenericHID;
import frc.lib.util.COTSTalonFXSwerveConstants;
import frc.lib.util.SwerveModuleConstants;

public final class Constants {
    public static final boolean ROBOT_RELATIVE = false;
    public static final boolean OPEN_LOOP = true;
    public static final int CANDLE_ID = 15;
    public static final String PRACTICE_MAC = "00:80:2F:25:DE:54";
    public static final boolean PRACTICE_BOT = getMACAddress().equals(PRACTICE_MAC);
    public static final boolean DEBUG = true;

    public static final class Controllers{
        public static final GenericHID TRANSLATION_CONTROLLER = new GenericHID(0);
        public static final GenericHID ROTATION_CONTROLLER = new GenericHID(1);
        public static final GenericHID XBOX_CONTROLLER = new GenericHID(2);
        public static final GenericHID BUTTONS = new GenericHID(3);
        public static final int XBOX_CONTROLLER_A_BUTTON = 1;
        public static final int XBOX_CONTROLLER_B_BUTTON = 2;
        public static final int XBOX_CONTROLLER_X_BUTTON = 3;
        public static final int XBOX_CONTROLLER_Y_BUTTON = 4;
        public static final int XBOX_CONTROLLER_LB_BUTTON = 5;
        public static final int XBOX_CONTROLLER_RB_BUTTON = 6;
        public static final int XBOX_CONTROLLER_BACK_BUTTON = 7;
        public static final int XBOX_CONTROLLER_START_BUTTON = 8;
        public static final int XBOX_CONTROLLER_LEFT_SIICK_BUTTON = 9;
        public static final int XBOX_CONTROLLER_RIGHT_STICK_BUTTON = 10;
        public static final int UP = 0;
        public static final int RIGHT = 90;
        public static final int DOWN = 180;
        public static final int LEFT = 270;
        public static final double STICK_DEADBAND = 0.1;
        public static final int TRANSLATION_BUTTON = 1;
        public static final int ROTATION_BUTTON = 1;
        public static final int TRANSLATION_AXIS = 1;
        public static final int STRAFE_AXIS = 0;
        public static final int ROTATION_AXIS = 0;
    }

    public static final class Swerve {
        public static final Vector<N3> STATE_STDS = VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5));

        public static final int PIGEON_ID = 20;
        public static final boolean INVERT_GYRO = false;


        public static final COTSTalonFXSwerveConstants CHOSEN_MODULE = COTSTalonFXSwerveConstants.SDS.MK4.Falcon500(COTSTalonFXSwerveConstants.SDS.MK4.driveRatios.L2);

        /* Drivetrain Constants */
        public static final double DRIVE_BASE_RADIUS = 0.4318; //FIXME 0.3804
        public static final double TRACK_WIDTH = 0.45; //FIXME 0.57
        public static final double WHEEL_BASE = 0.645; //FIXME 0.57
        public static final double WHEEL_DIAMETER = Units.inchesToMeters(3.9);
        public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;

        public static final SwerveDriveKinematics SWERVE_KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            new Translation2d(WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0),
            new Translation2d(-WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            new Translation2d(-WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0));


        public static final double DRIVE_GEAR_RATIO = (6.75 / 1.0);
        public static final double ANGLE_GEAR_RATIO = (12.8 / 1.0);

        /* Motor Inverts */
        public static final InvertedValue ANGLE_MOTOR_INVERT = CHOSEN_MODULE.angleMotorInvert;
        public static final InvertedValue DRIVE_MOTOR_INVERT = CHOSEN_MODULE.driveMotorInvert;

        /* Angle Encoder Invert */
        public static final SensorDirectionValue CANCODER_INVERT = CHOSEN_MODULE.cancoderInvert;

        /* Swerve Current Limiting */
        public static final int ANGLE_CONTINUOUS_CURRENT_LIMIT = 25;
        public static final int ANGLE_PEAK_CURRENT_LIMIT = 40;
        public static final double ANGLE_PEAK_CURRENT_DURATION = 0.1;
        public static final boolean ANGLE_ENABLE_CURRENT_LIMIT = true;

        public static final int DRIVE_CONTINUOUS_CURRENT_LIMIT = 35;
        public static final int DRIVE_PEAK_CURRENT_LIMIT = 50;
        public static final double DRIVE_PEAK_CURRENT_DURATION = 0.1;
        public static final boolean DRIVE_ENABLE_CURRENT_LIMIT = true;

        public static final double OPEN_LOOP_RAMP = 0.25;
        public static final double CLOSED_LOOP_RAMP = 0.0;

        /* Angle Motor PID Values */
        public static final double ANGLE_KP = CHOSEN_MODULE.angleKP;
        public static final double ANGLE_KI = CHOSEN_MODULE.angleKI;
        public static final double ANGLE_KD = CHOSEN_MODULE.angleKD;

        /* Drive Motor PID Values */
        public static final double DRIVE_KP = 2.75;
        public static final double DRIVE_KI = 0.0;
        public static final double DRIVE_KD = 0.0;
        public static final double DRIVE_KF = 0.0;

        /* Drive Motor Characterization Values From SYSID */
        public static final double DRIVE_KS = (0.53906 / 12);
        public static final double DRIVE_KV = (2.2756 / 12);
        public static final double DRIVE_KA = (0.065383 / 12);

        /* Swerve Profiling Values */
        public static final double MAX_SPEED = 4.5;
        public static final double MAX_ANGULAR_VELOCITY = 11.5;

        /* Neutral Modes */
        public static final NeutralModeValue ANGLE_NEUTRAL_MODE = NeutralModeValue.Brake;
        public static final NeutralModeValue DRIVE_NEUTRAL_MODE = NeutralModeValue.Brake;

        /* Auto Path Finding Constraints*/
        public static final PathConstraints CONSTRAINTS = new PathConstraints(MAX_SPEED, 4.5, 
                                                                              Units.degreesToRadians(690), 
                                                                              Units.degreesToRadians(690));

        /* Front Left Module - Module 0 */
        public static final class Mod0 {
            public static final int DRIVE_MOTOR_ID = 0;
            public static final int ANGLE_MOTOR_ID = 1;
            public static final int CANCODER_ID = 21;
            public static final Rotation2d ANGLE_OFFSET = Rotation2d.fromDegrees(-179.296875);
            public static final Rotation2d ANGLE_OFFSET_PRACTICE = Rotation2d.fromDegrees(0);
            public static final SwerveModuleConstants CONSTANTS = 
                new SwerveModuleConstants(DRIVE_MOTOR_ID, ANGLE_MOTOR_ID, CANCODER_ID, ANGLE_OFFSET, ANGLE_OFFSET_PRACTICE);
        }

        /* Front Right Module - Module 1 */
        public static final class Mod1 {
            public static final int DRIVE_MOTOR_ID = 19;
            public static final int ANGLE_MOTOR_ID = 18;
            public static final int CANCODER_ID = 22;
            public static final Rotation2d ANGLE_OFFSET = Rotation2d.fromDegrees(-40.693359375);
            public static final Rotation2d ANGLE_OFFSET_PRACTICE = Rotation2d.fromDegrees(0);
            public static final SwerveModuleConstants CONSTANTS = 
                new SwerveModuleConstants(DRIVE_MOTOR_ID, ANGLE_MOTOR_ID, CANCODER_ID, ANGLE_OFFSET, ANGLE_OFFSET_PRACTICE);
        }
        
        /* Back Left Module - Module 2 */
        public static final class Mod2 {
            public static final int DRIVE_MOTOR_ID = 8;
            public static final int ANGLE_MOTOR_ID = 9;
            public static final int CANCODER_ID = 23;
            public static final Rotation2d ANGLE_OFFSET = Rotation2d.fromDegrees(-37.529296875);
            public static final Rotation2d ANGLE_OFFSET_PRACTICE = Rotation2d.fromDegrees(0);
            public static final SwerveModuleConstants CONSTANTS = 
                new SwerveModuleConstants(DRIVE_MOTOR_ID, ANGLE_MOTOR_ID, CANCODER_ID, ANGLE_OFFSET, ANGLE_OFFSET_PRACTICE);
        }

        /* Back Right Module - Module 3 */
        public static final class Mod3 {
            public static final int DRIVE_MOTOR_ID = 11;
            public static final int ANGLE_MOTOR_ID = 10;
            public static final int CANCODER_ID = 24;
            public static final Rotation2d ANGLE_OFFSET = Rotation2d.fromDegrees(148.798828125);
            public static final Rotation2d ANGLE_OFFSET_PRACTICE = Rotation2d.fromDegrees(0);
            public static final SwerveModuleConstants CONSTANTS = 
                new SwerveModuleConstants(DRIVE_MOTOR_ID, ANGLE_MOTOR_ID, CANCODER_ID, ANGLE_OFFSET, ANGLE_OFFSET_PRACTICE);
        }

    }

    public static final class Intake {
        public static final int FRONT_INTAKE_MOTOR_ID = 0; //FIXME
        public static final int BACK_INTAKE_MOTOR_ID = 0; //FIXME
    }

    public static final class Shooter {
        public static final int SHOOTER_MOTOR_ID = 0; //FIXME
        public static final int FOLLOWER_SHOOTER_MOTOR_ID = 0; //FIXME
        public static final int WRIST_MOTOR_ID = 0; //FIXME
        public static final int WRIST_FOLLOWER_MOTOR_ID = 0; //FIXME
        public static final int FORWARD_CHANNEL = 0; //FIXME
        public static final int REVERSE_CHANNEL = 0; //FIXME
    }

    public static final class Elevator {
        public static final int ELEVATOR_MOTOR_ID = 0; //FIXME
        public static final int FOLLOWER_ELEVATOR_MOTOR_ID = 0; //FIXME
        public static final int BABY_WRIST_ID = 0; //FIXME
        public static final int BABY_SHOOTER_ID = 0; //FIXME
    }

    public static final class Vision {
        public static final String FRONT_CAMERA_NAME = "3937";//"FrontCamera"; //FIXME
        public static final String BACK_CAMERA_NAME = "BackCamera";
        public static final String NOTE_CAMERA_NAME = "Microsoft_LifeCam_HD-3000";
        public static final Transform3d FRONT_CAMERA_TRANSFORM = new Transform3d(new Translation3d(0.3019665792, 0, 0.19), new Rotation3d(0, -0.349066, 0)); //FIXME
        public static final Transform3d BACK_CAMERA_TRANSFORM = new Transform3d(); //FIXME
        public static final Vector<N3> VISION_STDS = VecBuilder.fill(0.1, 0.1, Units.degreesToRadians(10));
        public static final double TARGET_X_BLUE = -0.04;
        public static final double TARGET_X_RED = 16.58;
        public static final double TARGET_Y_BLUE = 4.98;
        public static final double TARGET_Y_RED = 5.55;
    }

    public static String getMACAddress() {
        try{
            Enumeration<NetworkInterface> nwInterface = NetworkInterface.getNetworkInterfaces();
            StringBuilder ret = new StringBuilder();
            while(nwInterface.hasMoreElements()){
                NetworkInterface nis = nwInterface.nextElement();
                if(nis != null && "eth0".equals(nis.getDisplayName())){
                    byte[] mac = nis.getHardwareAddress();
                    if(mac != null){
                        for(int i = 0; i < mac.length; i++){
                            ret.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                        }
                        String addr = ret.toString();
                        return addr;
                    }
                    else {}
                } 
                else {}
            }
        } 
        catch (SocketException | NullPointerException e) {}
        return "";
    }
}