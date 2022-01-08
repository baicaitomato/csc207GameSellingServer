package src.main.command;

import src.main.exceptions.ConstraintException;

/**
 * An Invoker capable of executing a transaction from daily.txt
 */
public class Invoker {
    Command activeCommand;

    /**
     * Initialize an Invoker
     */
    public Invoker() { }

    /**
     * set this Invoker's command to the given command (where command represents a transaction)
     * @param command the transaction to be executed by the Invoker
     */
    public void setCommand(Command command) {
        activeCommand = command;
    }

    /**
     * Execute the Invoker's command (ie perform the transaction)
     * @throws ConstraintException if the command is invalid
     */
    public void run() throws ConstraintException {
        activeCommand.execute();
    }
}
