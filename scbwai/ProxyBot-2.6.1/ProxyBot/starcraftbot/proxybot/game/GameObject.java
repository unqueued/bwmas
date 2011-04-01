package starcraftbot.proxybot.game;

import java.util.*;
import java.io.*;

import starcraftbot.proxybot.command.Command;
import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.UnitsObject;


/**
 * This is our take on the "Game" object given in the original ProxyBot source.
 * 
 * 
 * GameObject maintains information for the players and the map in the Starcraft Game as well as keeping
 * the information up to date as the game progresses.
 * 
 * @author Antonio Arredondo
 * 
 * 
 */
public class GameObject implements Serializable {

  private PlayerObject myPlayer = null;

  /** all players */
  private ArrayList<PlayerObject> playersInGame;
  
  private ArrayList<UnitsObject> unitsInGame;
  
  private ArrayList<Command> CommandsToDo;

  /** map information */
  private MapObject map = null;

  /** Units in Game */
  private UnitsObject game_units = null;

  /** String given from Socket dll information*/
  private String update;

  /**
   * Default Constructor. Does nothing.
   */
  public GameObject () {
    
  }
  
  /**
   * @param playerData lists the players in the game (maybe)
   * @param locationData this will have the starting location of the players
   * @param mapData lists the map data
   *
   * Optional params
   * @param chokePointData lists the choke points on the map
   * @param baseLocationsData lists the bases on the map
   * @param regionsData lists the regions on the map
   *
   */
	public GameObject(String playersData, 
                    String startingLocationsData, 
                    String mapData, 
                    String chokePointsData, 
                    String baseLocationsData, 
                    String regionsData){
    
    playersInGame = PlayerObject.parsePlayersData(playersData);
    
    String[] playerDatas = playersData.split(":");
    int my_player_id = Integer.parseInt(playerDatas[0].split(";")[1]);

    //set myPlayer from the players on our list
    for(PlayerObject tmp : playersInGame) {
      if(tmp.getPlayerID() == my_player_id) {
        myPlayer = tmp;
        break;
      } 
    }

	unitsInGame = new ArrayList<UnitsObject>();
	
	CommandsToDo = new ArrayList<Command>();

    map = new MapObject(startingLocationsData, mapData);

    //future work for the MapObject to incorporate AI
    //map = new MapObject(startingLocationsData, mapData, baseLocationsData, chokepointsData, regionsData);
  
  }//end Constructor

   

	/**
	 * Updates the state of the game from the info passed to us by ProxyBot.java.
     * It looks like the string contains data that is only relevant to us.
	 *
	 * @param update String from socket with BWAPI
	 */
	public void processGameUpdate(String update) {

		String[] parsed_update = update.split(":")[0].split(";");

    /* may have to change, not sure, just don't like the args of array elements, will fix later */
    myPlayer.updateAttributes(parsed_update[1],parsed_update[2],parsed_update[3],parsed_update[4],parsed_update[5],parsed_update[6]);

    unitsInGame = UnitsObject.parseUpdateUnits(update, this);

    
    //DEBUG
    //System.out.println("myPlayer> " + myPlayer);
//
//
//		units = UnitWME.getUnits(this, updateData, unitTypes, playerID, playerArray);
//		lastGameUpdate = System.currentTimeMillis();
//
//		HashMap<Integer, UnitWME> newMap = new HashMap<Integer, UnitWME>();
//		for (UnitWME unit : units) {
//			newMap.put(unit.getID(), unit);
//		}
//
//		unitMap = newMap;
	}

  /**
   * This method is used to printout the map data visually.
   */
  public void printMapInfo(){
    map.print();
  }
  
  /**
   * returns the Array of units in game as given by BWAPI DLL
   * @return unitsInGame
   */
  public ArrayList<UnitsObject> getUnitArray(){
	  return unitsInGame;
  }
  /**
   * 
   * This will return the unit in the unitsInGame array at spot id, or null.
   * 
   * @param id
   * @return UnitsObject
   */
  public UnitsObject getUnitAt(int id){
	  if(unitsInGame == null)
		  return null;
	  else if(unitsInGame.get(id) == null)
		  return null;
	  return unitsInGame.get(id);
  }

  /**
   * get the PlayerObject for 'you'
   * 
   * @return myPlayer
   */
  public PlayerObject getMyPlayer() {
	// TODO Auto-generated method stub
	return myPlayer;
  }
  
  /**
   * 
   */
  public String toString()
  {
	  return "GameObject";
  }

public void setCommandsToDo(ArrayList<Command> commandsToDo) {
	CommandsToDo = commandsToDo;
}

public ArrayList<Command> getCommandsToDo() {
	return CommandsToDo;
}

public String getCommandString(){
	String commandData = new String("commands");

	for(int i = 0; i < CommandsToDo.size(); i++)
	{
		Command command = CommandsToDo.get(i);
		
		commandData += (":" + command.getCommand() + ";"
				+ command.getUnitID() + ";" + command.getArg0() + ";"
				+ command.getArg1() + ";" + command.getArg2());
	}
	
	return commandData;
	
	/*	int commandsAdded = 0;

		while (commandQueue.size() > 0
				&& commandsAdded < maxCommandsPerMessage) {
			commandsAdded++;
			Command command = commandQueue.remove(commandQueue.size() - 1);
			commandData.append(":" + command.getCommand() + ";"
					+ command.getUnitID() + ";" + command.getArg0() + ";"
					+ command.getArg1() + ";" + command.getArg2());
		}
	}*/
}

public void clearCommands() {
	// TODO Auto-generated method stub
	CommandsToDo.clear();
}
}//end GameObject

