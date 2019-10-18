package frc.robot;

import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.*;
import frc.robot.oi.*;
import frc.robot.subroutines.ActivateTrident;
import frc.robot.States;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.command.Command;
import static frc.robot.Conversions.*;

public class Neptune extends TimedRobot {

  public static AnalogOutput LEDs = new AnalogOutput(0);
  public static Elevator elevator = Elevator.getInstance();
  public static Drivetrain driveTrain = Drivetrain.getInstance();
  public static Trident trident = Trident.getInstance();
  public static Climber climber = Climber.getInstance();

  public static DriverOI oi;
  public static OperatorOI oi2;

  public UsbCamera camera;
  public static Command sandstorm = new ActivateTrident(5);
  public static double teleopStart;
  public static double teleopElapsedTime;
  public static boolean endGame;
  public static boolean climbDrive;
  public int stopLoops;

  private DriverStation dStation = DriverStation.getInstance();
  public static RobotController diagnostics;
 

  @Override
  public void robotInit() {
    oi = new DriverOI();
    oi2 = new OperatorOI();
    camera = CameraServer.getInstance().startAutomaticCapture("Video", 0);
    camera.setResolution(320, 240);
    camera.setFPS(18);
  } 

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("gyro", Neptune.elevator.getYaw());
    for(SwerveMod mod : driveTrain.getSwerveModules()){
      SmartDashboard.putNumber("target Angle " + mod.moduleNumber + "  ", toCounts(mod.smartAngle));
      SmartDashboard.putNumber("relative Angle " + mod.moduleNumber + "  ", mod.getPos());
      SmartDashboard.putNumber("absolute Angle " + mod.moduleNumber + "  ", mod.getTicks());
    }
  }

  @Override
  public void autonomousInit() {
    Scheduler.getInstance().removeAll();
    sandstorm.start();
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
    oi2.controlLoop();
  }

  @Override
  public void teleopInit() {
    Neptune.driveTrain.setDriverCamMode();
    Scheduler.getInstance().removeAll();
  }

  @Override
  public void teleopPeriodic() { 
    Scheduler.getInstance().run();
    oi2.controlLoop();
    if ((elevator.getLiftPosition() < 10000) && (elevator.getLiftPosition() > RobotMap.liftLowerBound)) {
      States.liftZone = States.LiftZones.LOWER_DANGER;
    } else if ((elevator.getLiftPosition() > 100000) && (elevator.getLiftPosition() < RobotMap.liftUpperBound))
      States.liftZone = States.LiftZones.UPPER_DANGER;
    else {
      States.liftZone = States.LiftZones.SAFE;
    }

    if ((elevator.getLiftPosition() >= RobotMap.liftUpperBound)) {
      if(stopLoops == 0){
        elevator.stopLift();
        stopLoops++;
      }
      elevator.setLiftPosition(RobotMap.liftHighH);

    }
    if ((Neptune.elevator.getLiftPosition() <= RobotMap.liftLowerBound)) {
      if(stopLoops == 0){
        elevator.stopLift();
        stopLoops++;
      }
      elevator.setLiftPosition(0);
  
    }

    
  }

}
