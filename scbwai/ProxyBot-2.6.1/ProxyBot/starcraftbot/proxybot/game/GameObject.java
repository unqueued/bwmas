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

  private PlayerObject khasbotPlayer = null;

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
	public GameObject(String playersData, String startingLocationsData, String mapData, String chokePointsData, String baseLocationsData, String regionsData){
    
    playersInGame = PlayerObject.parsePlayersData(playersData);
    
    map = new MapObject(startingLocationsData, mapData);
    //future work
    //map = new MapObject(startingLocationsData, mapData, baseLocationsData, chokepointsData, regionsData);
  
  }//end Constructor

   

	/**
	 * Updates the state of the game.
	 */
	public void processGameUpdate(String updateData) {


//    frame++;
//		player.update(updateData, enemy);
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

  public void printMapInfo(){
    map.print();
  }

}//end GameObject

