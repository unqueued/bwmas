package starcraftbot.proxybot.game;

import java.util.*;
import java.io.*;


import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.UnitsObject;
import starcraftbot.proxybot.khasbot.structurema.TechObject;

/**
 *
 */
public class GameObject implements Serializable {

  private PlayerObject myPlayer = null;

  /** all players */
	private ArrayList<PlayerObject> playersInGame;

	/** map information */
	private MapObject map = null;

  private UnitsObject game_units = null;

  private TechObject tech = null;

	/** timestamp of when the game state was last changed */
	private long lastGameUpdate = 0;

  int frame = 0;

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
    
    map = new MapObject(startingLocationsData, mapData);
    //future work
    //map = new MapObject(startingLocationsData, mapData, baseLocationsData, chokepointsData, regionsData);
  
  }//end Constructor

   

	/**
	 * Updates the state of the game from the info passed to us by ProxyBot.java.
   * It looks like the string contains data that is only relevant to us.
	 */
	public void processGameUpdate(String update) {

		String[] parsed_update = update.split(":")[0].split(";");

    /* may have to change, not sure */
    myPlayer.updateAttributes(parsed_update[1],parsed_update[2],parsed_update[3],parsed_update[4],parsed_update[5],parsed_update[6]);

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

}//end GameObject

