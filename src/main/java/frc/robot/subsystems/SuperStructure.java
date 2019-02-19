package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.defaultcommands.Periodic;
import frc.robot.RobotMap;
import frc.robot.util.PIDCalc;
import frc.robot.util.prefabs.subsystems.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.States;

public class SuperStructure extends Subsystem {

  public TalonBase rightDrive;
  public TalonBase leftDrive;
  public TalonBase lift;
  public TalonBase arm;
  public VictorBase intake;
  public VictorBase dropWheels;

  private TalonSRX rDrive;
  private TalonSRX lDrive;
  private TalonSRX lt;
  public TalonSRX a;
  private VictorSPX in;
  private VictorSPX dw;

  public DriveTrain driveTrain;
  public ElevatorSystem elevatorSystem;

  private VictorSPX lRearDriveSlave;
  private VictorSPX lTopDriveSlave;
  private VictorSPX rRearDriveSlave;
  private VictorSPX rTopDriveSlave;
  private TalonSRX liftSlave;
  private VictorSPX intakeSlave;

  public Piston claw;
  public Piston lever;
  public Piston back;
  public Piston front;
  public Piston shifter;

  private DoubleSolenoid cl;
  private DoubleSolenoid le;
  private DoubleSolenoid ba;
  private DoubleSolenoid wh;
  private DoubleSolenoid sh;

  public AHRS navX;
  public PIDCalc pidNavX;

  public DigitalInput iL;
  public DigitalInput aL;
  public DigitalInput lLL;
  public DigitalInput uLL;

  /**Access limit switches as follows
   * <p>0: Cargo
   * <p>1: Arm
   * <p>2: Lower Lift
   * <p>3: Upper Lift
   */
  public boolean[] limitArray = {false, false, false, false};

  public SuperStructure(){
    //masters
    rDrive = new TalonSRX(RobotMap.rightFrontDrive);
    lDrive = new TalonSRX(RobotMap.leftFrontDrive);
    lt = new TalonSRX(RobotMap.rightLift);
    a = new TalonSRX(RobotMap.arm);
    in = new VictorSPX(RobotMap.rightClaw);
    dw = new VictorSPX(RobotMap.dropWheels);

    //followers
    lRearDriveSlave = new VictorSPX(RobotMap.leftRearDrive);
    lTopDriveSlave = new VictorSPX(RobotMap.leftTopDrive);
    rRearDriveSlave = new VictorSPX(RobotMap.rightRearDrive);
    rTopDriveSlave = new VictorSPX(RobotMap.rightTopDrive);
    liftSlave = new TalonSRX(RobotMap.leftLift);
    intakeSlave = new VictorSPX(RobotMap.leftClaw);

    //Pistons
    //PCM 1
    cl = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.intakePort1, RobotMap.intakePort2);
    le = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.leverPort1, RobotMap.leverPort2);
    sh = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.shifterPort1, RobotMap.shifterPort2);
    ba = new DoubleSolenoid(RobotMap.primaryPCM, RobotMap.climbPort1, RobotMap.climbPort2);
    //PCM 2
    wh = new DoubleSolenoid(RobotMap.secondaryPCM, RobotMap.climbPort3, RobotMap.climbPort4);
   

    //Right Drive Train
    rightDrive = new TalonBase(        
        rDrive, 
        RobotMap.driveReverse,
        RobotMap.driveReverseEncoder,
        RobotMap.driveNominalOutputForward, 
        RobotMap.driveNominalOutputReverse, 
        RobotMap.drivePeakOutputForward, 
        RobotMap.drivePeakOutputReverse, 
        RobotMap.driveCruiseVelocity, 
        RobotMap.driveAcceleration, 
        "RightDrive");
    rRearDriveSlave.follow(rDrive);
    rTopDriveSlave.follow(rDrive);

    //Left Drive Train
    leftDrive = new TalonBase(        
        lDrive, 
        RobotMap.driveReverse,
        RobotMap.driveReverseEncoder,
        RobotMap.driveNominalOutputForward, 
        RobotMap.driveNominalOutputReverse, 
        RobotMap.drivePeakOutputForward, 
        RobotMap.drivePeakOutputReverse, 
        RobotMap.driveCruiseVelocity, 
        RobotMap.driveAcceleration, 
        "LeftDrive");
    lRearDriveSlave.follow(lDrive);
    lTopDriveSlave.follow(lDrive);

    driveTrain = new DriveTrain(leftDrive, rightDrive);
    
    //Lift
    lift = new TalonBase(        
        lt, 
        RobotMap.liftReverse,
        RobotMap.liftReverseEncoder,
        RobotMap.liftNominalOutputForward, 
        RobotMap.liftNominalOutputReverse, 
        RobotMap.liftPeakOutputForward, 
        RobotMap.liftPeakOutputReverse, 
        RobotMap.liftCruiseVelocity, 
        RobotMap.liftAcceleration, 
        RobotMap.liftLowerBound, 
        RobotMap.liftUpperBound, 
        0.2, 0, 0, 0.2,
        "Lift");
    liftSlave.follow(lt);
    liftSlave.setInverted(true);
    lLL = new DigitalInput(RobotMap.lowerLiftLimitSwitch);
    uLL = new DigitalInput(RobotMap.upperLiftLimitSwitch);
    
    //Arm
    arm = new TalonBase(
        a, 
        RobotMap.armReverse,
        RobotMap.armReverseEncoder,
        RobotMap.armNominalOutputForward, 
        RobotMap.armNominalOutputReverse, 
        RobotMap.armPeakOutputForward, 
        RobotMap.armPeakOutputReverse, 
        RobotMap.armCruiseVelocity, 
        RobotMap.armAcceleration, 
        RobotMap.armLowerBound, 
        RobotMap.armUpperBound, 
        0.8, 0, 0, 0,
        "Arm");
    // arm.setPID(1.2, 0.00005, 0, 0);
    aL = new DigitalInput(RobotMap.armLimitSwitch);

    elevatorSystem = new ElevatorSystem(lift, arm);

    //Intake 
    intake = new VictorBase(
      in, 
      RobotMap.intakeNominalOutputForward, 
      RobotMap.intakeNominalOutputReverse, 
      RobotMap.intakePeakOutputForward, 
      RobotMap.intakePeakOutputReverse, 
      "Intake");
    intakeSlave.follow(in);
    iL = new DigitalInput(RobotMap.ballLimitSwitch);

    //DropWheels
    dropWheels = new VictorBase(
      dw, 
      RobotMap.dropWheelsNominalOutputForward, 
      RobotMap.dropWheelsNominalOutputReverse, 
      RobotMap.dropWheelsPeakOutputForward, 
      RobotMap.dropWheelsPeakOutputReverse,  
      "DropWheels");
    
    //Pistons
    claw = new Piston(cl, "Claw");
    lever = new Piston(le, "Lever");
    back = new Piston(ba, "Back");
    front = new Piston(wh, "Front");
    shifter = new Piston(sh, "Shifter");

    //Gyro
    navX = new AHRS(SPI.Port.kMXP);
    pidNavX = new PIDCalc(RobotMap.navXPterm, RobotMap.navXIterm, RobotMap.navXDterm, RobotMap.navXFterm, "NavX");
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
     setDefaultCommand(new Periodic());
  }
  //Drive Train
  public void driveOpenLoop(double left, double right){
    driveTrain.openLoop(left, right);
  }

  public void resetDriveEncoders(){
    rightDrive.zero();
    leftDrive.zero();
  }
  //Gyro
  public double getYaw(){
    return navX.getYaw();
  }
  public void zeroYaw(){
    navX.reset();
  }
  //Misc
  /**Sets enocders of arm and lift to zero */
  public void resetEncoders(){
    lift.zero();
    arm.zero();
  }
  /**Because none of the grip runs default commands,
   * the grip is inactive when no commands are being run
   * by any three of the subsystems of the larger apparatus
   */
  public boolean gripInactive(){
      return (Robot.superStructure.intake.noCommand() && Robot.superStructure.lever.noCommand() && Robot.superStructure.claw.noCommand());
  }
  public boolean elevatorPassive(){
    return States.actionState == States.ActionStates.PASSIVE;
  }
  /**Posts MotionMagic Trajectory Data to SmartDashboard for each ComplexTalon */
  public void postImplementation(){
    //lift.instrumentation();
    //arm.instrumentation();
    //rightDrive.instrumentation();
    //leftDrive.instrumentation();
  }

  public void postSmartDashVars(){
    //Talons
    lift.postSmartDashVars();
    //rightDrive.postSmartDashVars();
    //leftDrive.postSmartDashVars();
    arm.postSmartDashVars();
    //Victors
    //intake.postSmartDashVars();
    //dropWheels.postSmartDashVars();
    //Pistons
    //claw.postSmartDashVars();
    //lever.postSmartDashVars();
    //back.postSmartDashVars();
    front.postSmartDashVars();
    //shifter.postSmartDashVars();
    //States
    SmartDashboard.putString("Object State:", States.objState.toString());
    SmartDashboard.putString("Action State:", States.actionState.toString());
    SmartDashboard.putString("Loop State:", States.loopState.toString());
    //SmartDashboard.putString("Drive State:", States.driveState.toString());
    //SmartDashboard.putString("Drive Motion State:", States.driveMotionState.toString());
    //SmartDashboard.putString("Score State:", States.scoreState.toString());
    //SmartDashboard.putString("Climb State:", States.climbState.toString());
    //LimitSwitches
    SmartDashboard.putBoolean("Intake Limit: ", limitArray[0]);
    SmartDashboard.putBoolean("Arm Limit: ", limitArray[1]);
    SmartDashboard.putBoolean("Lift Lower Limit: ", limitArray[2]);
    SmartDashboard.putBoolean("Lift Upper Limit: ", limitArray[3]);
    SmartDashboard.putString("Lift Zone: ", States.liftZone.toString());
    SmartDashboard.putString("Elevator Command: ", elevatorSystem.getCurrentCommandName());
  }
  }

