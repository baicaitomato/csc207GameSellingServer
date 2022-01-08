package src.main.command;

import src.main.exceptions.ConstraintException;

/**
 * A interface representing a command
 */
public interface Command {
    public void execute() throws ConstraintException;
}
