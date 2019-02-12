package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;


public class StopDriveMotors extends Command {

    public StopDriveMotors() {
        requires(Robot.superStructure.driveTrain);
        setTimeout(0.1);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
        Robot.superStructure.stopDrive();
    }

    @Override
    protected boolean isFinished() {
        return isTimedOut();
    }

    @Override
    protected void end() {
        Robot.superStructure.shifter.noInput();
    }

    @Override
    protected void interrupted() {
        super.interrupted();
    }
}
