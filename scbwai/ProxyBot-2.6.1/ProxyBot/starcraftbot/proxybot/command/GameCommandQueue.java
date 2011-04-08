package starcraftbot.proxybot.command;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Class for queueing up commands to be sent to StarCraft. This class handles
 * the asynchronuous communication between the agent and StarCraft.
 */
@SuppressWarnings("serial")
public class GameCommandQueue implements Serializable{

	/** queued up commands (orders) to send to StarCraft */
	private ArrayList<GameCommand> cmdQueue = null; 
	private ArrayList<GameCommandListener> listeners = null;

	/** message number of commands to send to starcraft per response */
	private int maxCommandsPerMessage = 1000;


  /**
   * Default constructor.
   */
  public GameCommandQueue(){
    cmdQueue = new ArrayList<GameCommand>();
    listeners = new ArrayList<GameCommandListener>();

  }

	public void addGameCommandListener(GameCommandListener listener) {
		listeners.add(listener);
	}

	/**
	 * Gets the commands to execute in starcraft.
	 */
	private StringBuilder getCommands() {
		StringBuilder commandData = new StringBuilder("commands");

		//synchronized (cmdQueue) {
			int commandsAdded = 0;

      //send as many commands as we can fit in the buffer
			while (cmdQueue.size() > 0 && commandsAdded < maxCommandsPerMessage) {
				commandsAdded++;
				commandData.append(cmdQueue.remove(cmdQueue.size() - 1).formatCmd());
			}
		//}

		return commandData;
	}

	/**
	 * Adds a command to the command queue.
	 * 
	 * @param command - the command to execute, see the Orders enumeration
	 * @param unitID - the unit to control
	 * @param arg0 - the first command argument
	 * @param arg1 - the second command argument
	 * @param arg2 - the third command argument
	 */
	public void addCommand(GameCommand command) {
		synchronized (cmdQueue) {
			if (!cmdQueue.contains(command)) {
				cmdQueue.add(command);

				for (GameCommandListener listener : listeners) {
					listener.event(command);
				}
			} else {
				System.out.println("Game Command Queue already had order:"+ command +": not putting in queue...");
			}
		}
	}//end addCommand

  public byte[] cmdsToExe() {
    return getCommands().toString().getBytes();
  }

public GameCommand pop() {
	//GameCommand t = cmdQueue.remove(cmdQueue.size()-1);
	return cmdQueue.remove(cmdQueue.size()-1);
}

public boolean empty() {
	return (cmdQueue.size() == 0);
}

public void push(GameCommand pop) {
	cmdQueue.add(pop);
	
}
  	
}
