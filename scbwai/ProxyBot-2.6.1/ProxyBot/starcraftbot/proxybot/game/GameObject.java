package starcraftbot.proxybot.game;

import java.util.*;
import java.io.*;


import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.UnitsObject;
import starcraftbot.proxybot.khasbot.structurema.TechObject;

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

  /** map information */
  private MapObject map = null;

  /** Units in Game */
  private UnitsObject game_units = null;

  /** Tech Objects we have */
  private TechObject tech = null;

  /** timestamp of when the game state was last changed */
  private long lastGameUpdate = 0;

  int frame = 0;

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
    
	myPlayer = new PlayerObject();
		
    playersInGame = PlayerObject.parsePlayersData(playersData);
    
    unitsInGame = new ArrayList<UnitsObject>();
    
    map = new MapObject(startingLocationsData, mapData);
    //future work
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
		System.out.println("parsed data for updateAttributes()");
    /* may have to change, not sure */
    myPlayer.updateAttributes(parsed_update[1],parsed_update[2],parsed_update[3],parsed_update[4],parsed_update[5],parsed_update[6]);
    	System.out.println("Attributes done, about to parseUpdateUnits()...");
    unitsInGame = UnitsObject.parseUpdateUnits(update, this);
    	System.out.println("done w/ parseUpdateUnits()!");
    
    lastGameUpdate = System.currentTimeMillis();	
    	
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
}//end GameObject

