// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

public class RunNote extends Command {
  private final Intake s_Intake;
  private final Shooter s_Shooter;
  private final XboxController xboxController;
  private final double handoff = 17.6;
  private final double protect = 13;
  private boolean runIntakeBackwardsUntilShooterSensorReturnsAFalseValue, deadIntake;

  /** Creates a new RunNote. */
  public RunNote(Intake s_Intake, Shooter s_Shooter, XboxController xboxController) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.s_Intake = s_Intake;
    this.s_Shooter = s_Shooter;
    this.xboxController = xboxController;
    addRequirements(s_Intake, s_Shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    s_Shooter.setWrist(protect);
    runIntakeBackwardsUntilShooterSensorReturnsAFalseValue = false;
    deadIntake = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(RunElevator.handoff && Robot.robotContainer.s_Elevator.isAtPosition()){
      s_Shooter.setWrist(handoff);
    }
    if(RunElevator.trapStage1){
      s_Intake.intake();
      s_Shooter.setShooter(10);
    }
    else if(RunElevator.trapStage2){
      s_Intake.stop();
      s_Shooter.stopShooter();
    }
    else if(RunElevator.startStage1){
      if(s_Shooter.isAtPosition()){
        s_Intake.intake();
        s_Shooter.setShooter(10);
      }
    }
    else if(RunElevator.startStage2){
      s_Intake.stop();
      s_Shooter.stopShooter();
      s_Shooter.setWrist(protect);
    }
    else if(RunElevator.reverse){
      s_Intake.spit();
      s_Shooter.setShooter(-10);
    }
    else{
      //No Buttons
      if(xboxController.getLeftTriggerAxis() <= 0.3 && !xboxController.getRawButton(5) && xboxController.getRightTriggerAxis() <= 0.3){
        s_Intake.stop();
      }
      //Intake
      else if(xboxController.getLeftTriggerAxis() > 0.3 && !s_Intake.getShooterSensor() && !deadIntake){
        s_Intake.intake();
      }
      //Spit
      else if(xboxController.getRawButton(5)){
        deadIntake = false;
        s_Intake.spit();
      }
      //Sensor Detects, Stop Intake
      else if(s_Intake.getShooterSensor()){
        s_Intake.stop();
        runIntakeBackwardsUntilShooterSensorReturnsAFalseValue = true;
      }
      if(runIntakeBackwardsUntilShooterSensorReturnsAFalseValue){
        s_Intake.spitSlowly();
      }
      if(runIntakeBackwardsUntilShooterSensorReturnsAFalseValue && !s_Intake.getShooterSensor()){
        runIntakeBackwardsUntilShooterSensorReturnsAFalseValue = false;
        deadIntake = true;
        s_Intake.stop();
      }
      //Run Shooter
      if(xboxController.getRawButton(6)){
        s_Shooter.runShooter();
        if(!RunElevator.deadShooter){
          s_Shooter.setWristShooting();
        }
      }
      //Stop Shooter
      else{
        s_Shooter.stopShooter();
        s_Shooter.setSpeedToZero();
        if(!RunElevator.handoff){
          s_Shooter.setWrist(protect);
        }
      }
      //Fire Shooter
      if(s_Shooter.atSpeed() && s_Shooter.isAtPosition() && xboxController.getRightTriggerAxis() > 0.3){
        s_Intake.intake();
        deadIntake = false;
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
