// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Map;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class Shooter extends SubsystemBase {

  private final TalonFX shooterMotor, followerShooterMotor;
  private final TalonFXConfiguration shooterMotorConfig = new TalonFXConfiguration();
  private final CANSparkMax wristMotor, wristFollowerMotor;
  private RelativeEncoder wristEncoder;
  private SparkPIDController pid;
  private final MotionMagicVelocityVoltage request = new MotionMagicVelocityVoltage(0).withSlot(0).withEnableFOC(true);
  private final Follower followerRequest = new Follower(Constants.Shooter.SHOOTER_MOTOR_ID, true);
  private final GenericEntry shooterEncoderEntry, wristEncoderEntry, shooterOffset, wristOffset;
  private double speed, position = 0;
  private boolean subwoofer, podium;
  private final InterpolatingDoubleTreeMap shooterMap = new InterpolatingDoubleTreeMap();
  private final InterpolatingDoubleTreeMap wristMap = new InterpolatingDoubleTreeMap();
  private boolean autoFire;

  /** Creates a new Shooter. */
  public Shooter() {
    shooterMotor = new TalonFX(Constants.Shooter.SHOOTER_MOTOR_ID);
    followerShooterMotor = new TalonFX(Constants.Shooter.FOLLOWER_SHOOTER_MOTOR_ID);
    wristMotor = new CANSparkMax(Constants.Shooter.WRIST_MOTOR_ID, MotorType.kBrushless);
    wristFollowerMotor = new CANSparkMax(Constants.Shooter.WRIST_FOLLOWER_MOTOR_ID, MotorType.kBrushless);
    configShooterMotors();
    configWristMotor();
    shooterEncoderEntry = Shuffleboard.getTab("Shooter").add("Shooter", getShooterVelocity()).withPosition(0,0).getEntry();
    wristEncoderEntry = Shuffleboard.getTab("Shooter").add("Wrist", getWrist()).withPosition(1, 0).getEntry();
    shooterOffset = Shuffleboard.getTab("Shooter").add("Shooter Offset", 0).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", -500, "max", 500)).withPosition(0, 1).getEntry();
    wristOffset = Shuffleboard.getTab("Shooter").add("Wrist Offset", -0.37).withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", -5, "max", 5)).withPosition(0, 2).getEntry();

    wristMap.put(1.24, 17.5);
    wristMap.put(2.14, 19.75);
    wristMap.put(2.71, 20.75);
    wristMap.put(2.74, 21.0);
    wristMap.put(3.24, 21.75);
    wristMap.put(3.74, 22.0);
    wristMap.put(3.94, 22.0);
    wristMap.put(4.36, 22.5);
    wristMap.put(5.1, 22.75);

    shooterMap.put(1.24, 3000.0 / 60.0);
    shooterMap.put(2.14, 3000.0 / 60.0);
    shooterMap.put(2.71, 3000.0 / 60.0);
    shooterMap.put(2.74, 3500.0 / 60.0);
    shooterMap.put(3.24, 3500.0 / 60.0);
    shooterMap.put(3.74, 3500.0 / 60.0);
    shooterMap.put(3.94, 3500.0 / 60.0);
    shooterMap.put(4.36, 4000.0 / 60.0);
    shooterMap.put(5.1, 4000.0 / 60.0);
  }

  public Pair<TalonFX, TalonFX> getShooterMotors(){
    return new Pair<TalonFX, TalonFX>(shooterMotor, followerShooterMotor);
  }

  public void setSubShooting(){
    subwoofer = true;
    podium = false;
  }

  public void setPodiumShooting(){
    subwoofer = false;
    podium = true;
  }

  public void setAutoShooting(){
    subwoofer = false;
    podium = false;
  }

  public void runShooter(){
    shooterMotor.setControl(request.withVelocity(speed));
  }

  public void setShooter(double speed){
    shooterMotor.setControl(request.withVelocity(speed));
  }

  public void stopShooter(){
    shooterMotor.stopMotor();
  }

  public boolean atSpeed(){
    if(speed <= getShooterVelocity() + 1.67 && speed >= getShooterVelocity() - 1.67){
      return true;
    }
    else{
      return false;
    }
  }
  
  public double getShooterVelocity(){
    return shooterMotor.getVelocity().getValueAsDouble();
  }

  public void setWristShooting(){
    if(podium){
      speed = 3500.0 / 60.0;
      position = 21;
    }
    else if(subwoofer){
      speed = 3000.0 / 60.0;
      position = 17.5;
    }
    else{
      speed = shooterMap.get(Robot.robotContainer.s_Vision.getDistance() + shooterOffset.getDouble(0));
      position = wristMap.get(Robot.robotContainer.s_Vision.getDistance() + wristOffset.getDouble(0));
    }
    if(!Robot.getFront()){
      position -= 11.5;
    }
    if((position > 0 && position < 4) || (position > 10 && position < 23)){
      pid.setReference(position, ControlType.kSmartMotion);
    }
  }

  public void setWrist(double position){
    this.position = position;
    pid.setReference(position, ControlType.kSmartMotion);
  }

  public double getWrist(){
    return wristEncoder.getPosition();
  }

  public boolean isSafe(){
    if(getWrist() > 12.8 && getWrist() < 13.2){
      return true;
    }
    else{
      return false;
    }
  }

  public boolean isAtPosition(){
    if(getWrist() > position - 0.2 && getWrist() < position + 0.2){
      return true;
    }
    else{
      return false;
    }
  }

  public double getSpeed(){
    return speed;
  }

  public void setSpeedToZero(){
    speed = 0;
  }

  public boolean autoFire(){
    return autoFire;
  }

  public void setAutoFire(boolean autoFire){
    this.autoFire = autoFire;
  }

  private void configShooterMotors(){
    shooterMotor.getConfigurator().apply(new TalonFXConfiguration());
    followerShooterMotor.getConfigurator().apply(new TalonFXConfiguration());

    shooterMotorConfig.Slot0.kS = 0.4; // Add 0.25 V output to overcome static friction
    shooterMotorConfig.Slot0.kV = 0.13; // A velocity target of 1 rps results in 0.12 V output
    shooterMotorConfig.Slot0.kA = 0.1; // An acceleration of 1 rps/s requires 0.01 V output
    shooterMotorConfig.Slot0.kP = 0.2; // An error of 1 rps results in 0.11 V output
    shooterMotorConfig.Slot0.kI = 0; // no output for integrated error
    shooterMotorConfig.Slot0.kD = 0; // no output for error derivative

    shooterMotorConfig.MotionMagic.MotionMagicAcceleration = 2500;
    shooterMotorConfig.MotionMagic.MotionMagicJerk = 4000;

    shooterMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

    shooterMotorConfig.Audio.AllowMusicDurDisable = true;

    shooterMotor.getConfigurator().apply(shooterMotorConfig);
    followerShooterMotor.getConfigurator().apply(shooterMotorConfig);

    followerShooterMotor.setControl(followerRequest);
  }

  private void configWristMotor(){
    wristMotor.restoreFactoryDefaults();
    wristFollowerMotor.restoreFactoryDefaults();
    wristMotor.setIdleMode(IdleMode.kBrake);
    wristFollowerMotor.setIdleMode(IdleMode.kBrake);
    wristFollowerMotor.follow(wristMotor);

    wristMotor.setInverted(true);

    wristMotor.setSmartCurrentLimit(35);
    wristFollowerMotor.setSmartCurrentLimit(35);

    wristEncoder = wristMotor.getEncoder();
    pid = wristMotor.getPIDController();
    pid.setFeedbackDevice(wristEncoder);
    wristEncoder.setPosition(0);
    pid.setOutputRange(-1, 1);
    pid.setSmartMotionMaxVelocity(4250, 0);
    pid.setSmartMotionMaxAccel(4000, 0);
    pid.setP(0);
    pid.setI(0);
    pid.setD(0);
    pid.setFF(0.0002);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    shooterEncoderEntry.setDouble(getShooterVelocity() * 60.0);
    Logger.recordOutput("Shooter", getShooterVelocity() * 60.0);
    wristEncoderEntry.setDouble(getWrist());
    Logger.recordOutput("Wrist", getWrist());
  }
}
