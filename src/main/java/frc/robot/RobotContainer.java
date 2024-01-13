package frc.robot;

import org.littletonrobotics.junction.Logger;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.commands.*;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    /* Controllers */
    private final Joystick translationController = new Joystick(Constants.Controllers.TRANSLATION_CONTROLLER.getPort());
    private final Joystick rotationController = new Joystick(Constants.Controllers.ROTATION_CONTROLLER.getPort());
    public final XboxController xboxController = new XboxController(Constants.Controllers.XBOX_CONTROLLER.getPort());
    public final Joystick buttonGrid = new Joystick(Constants.Controllers.BUTTON_GRID.getPort());

    /* Drive Controls */
    private final int translationAxis = Constants.Controllers.TRANSLATION_AXIS;
    private final int strafeAxis = Constants.Controllers.STRAFE_AXIS;
    private final int rotationAxis = Constants.Controllers.ROTATION_AXIS;
    private final boolean robotRelative = Constants.ROBOT_RELATIVE;


    /* Driver Buttons */
    private final JoystickButton translationButton = new JoystickButton(translationController, Constants.Controllers.TRANSLATION_BUTTON);

    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();
    private final LED s_LED = new LED();

    /* Commands */
    public final Music c_Music = new Music(s_Swerve);

    private final SendableChooser<Command> autoChooser;

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        CommandScheduler.getInstance().registerSubsystem(s_Swerve);
        CommandScheduler.getInstance().registerSubsystem(s_LED);
        s_Swerve.setDefaultCommand(new TeleopSwerve(s_Swerve, () -> translationController.getRawAxis(translationAxis), () -> translationController.getRawAxis(strafeAxis), () -> rotationController.getRawAxis(rotationAxis), () -> robotRelative));
        s_Swerve.configPathPlanner();
        NamedCommands.registerCommand("greenLEDS", new InstantCommand(() -> s_LED.green()));
        autoChooser = AutoBuilder.buildAutoChooser();
        Shuffleboard.getTab("Auto").add("Auto", autoChooser).withPosition(0, 0);
        Shuffleboard.selectTab("Auto");
        Logger.recordOutput("Auto", autoChooser.getSelected().toString());
        // Configure the button bindings
        configureButtonBindings();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        translationButton.onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}