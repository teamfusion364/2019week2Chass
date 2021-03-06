package frc.robot.subroutines;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Neptune;
import frc.robot.States;
import frc.robot.commands.*;

public class ActivateTrident extends CommandGroup {
  private int set;
  public ActivateTrident(int set) {
    this.set = set;
    if (set == 1) { // Intake Cargo
      addSequential(new RunIntake(-0.8, true)); // Intake
      addSequential(new SetPiston(Neptune.trident.claw, 0)); // Close Claw
      System.out.println("Intaking Cargo");
    } else if (set == 2) { // Intake Hatch
      addSequential(new SetPiston(Neptune.trident.lever, 1)); // Open lever
      addSequential(new ElevateToPosition(8));
      System.out.println("Grabbing Hatch");
    } else if (set == 3) { // Score Cargo
      addSequential(new RunIntake(0.65, false)); // Outtake
      addSequential(new SetPiston(Neptune.trident.claw, 1)); // Open Claw
      System.out.println("Scoring Cargo");
    } else if (set == 4) { // Score Hatch
      addParallel(new SetPiston(Neptune.trident.lever, 0)); // Close lever
      addSequential(new SetPiston(Neptune.trident.claw, 1));
      System.out.println("Scoring Hatch");
    }else if( set == 5){
      addSequential(new SetPiston(Neptune.trident.lever, 1)); // Open lever
      addSequential(new ElevateToPosition(1));
      System.out.println("Start Config Hatch");
    }
  }
    @Override
    protected void initialize() {
      if((set == 1)||(set == 2)){
        States.actionState = States.ActionStates.INTAKE_ACT;

      }else{
        States.led = States.LEDstates.PASSIVE;
      }
      
    }
    @Override
    protected void end() {
      if((set == 1)||(set == 2)){
        States.actionState = States.ActionStates.FERRY_ACT;
      }else{
        States.actionState = States.ActionStates.PASSIVE;
      }
      if(set == 2){
        States.led = States.LEDstates.HAS_OBJ;
      }else if(set ==4){
        States.led = States.LEDstates.PASSIVE;
      }
    }


}
