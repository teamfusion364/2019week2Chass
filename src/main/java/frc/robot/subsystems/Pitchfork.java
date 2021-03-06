package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Pitchfork extends Subsystem {

   private static Pitchfork Instance = null;
   public VictorSPX driver;
   public TalonSRX levitator;
   public VictorSPX forearms;

  public Pitchfork() {
    driver = new VictorSPX(RobotMap.climbDriveMotor);
    levitator = new TalonSRX(RobotMap.levitator);
    forearms = new VictorSPX(RobotMap.forearms);

    levitator.configMotionCruiseVelocity(RobotMap.levitatorCruiseVelocity, RobotMap.TimeoutMs);
    levitator.configMotionAcceleration(RobotMap.levitatorAcceleration, RobotMap.TimeoutMs);
    levitator.setSensorPhase(RobotMap.levitatorSensorPhase);
    levitator.setInverted(true);
    levitator.configPeakOutputForward(1);
    levitator.configPeakOutputReverse(-1);
    levitator.setSelectedSensorPosition(0);

    levitator.selectProfileSlot(RobotMap.SlotIdx, RobotMap.PIDLoopIdx);
    levitator.config_kF(RobotMap.SlotIdx, RobotMap.levitatorFgain, RobotMap.TimeoutMs);
    levitator.config_kP(RobotMap.SlotIdx, RobotMap.levitatorPgain, RobotMap.TimeoutMs);
    levitator.config_kI(RobotMap.SlotIdx, RobotMap.levitatorIgain, RobotMap.TimeoutMs);
    levitator.config_kD(RobotMap.SlotIdx, RobotMap.levitatorDgain, RobotMap.TimeoutMs);

    forearms.setInverted(true);
    
  }

  public synchronized static Pitchfork getInstance() {
    if (Instance == null) {
      Instance = new Pitchfork();
    }
    return Instance;
  }

  public void keepCurrentPosition() {
      double pos = levitator.getSelectedSensorPosition(0);
      levitator.set(ControlMode.MotionMagic, pos);
  }

  public void driveWheelsToWin(){
    // Turn on drive motors.. full steam ahead
    driver.set(ControlMode.Velocity, 300);
  }
  public void driveWheelsSlow(){
    driver.set(ControlMode.Velocity, 1);
  }
  public void levitateToPos(double position){
    levitator.set(ControlMode.MotionMagic, position);
    System.out.println("Levitator is moving to " + position);
  }
  public void engageForarms(double percentOut){
    forearms.set(ControlMode.PercentOutput, percentOut);
  }
  public void stopForarms(){
    forearms.set(ControlMode.PercentOutput, 0);
  }
  public double getForearmVelocity(){
    return forearms.getSelectedSensorVelocity();
  }

  public void stop() {
    // Stop the drive wheels
    driver.set(ControlMode.PercentOutput, 0);
  }
  public void driveWheelOpenLoop(double power){
    driver.set(ControlMode.PercentOutput, power);
  }
  @Override
  public void initDefaultCommand() {

  }
}
