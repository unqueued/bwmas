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
	private PlayerObject[] playersInGame;

	/** map information */
	private MapObject map = null;

  private UnitsObject game_units = null;

  private TechObject tech = null;

	/** timestamp of when the game state was last changed */
	private long lastGameUpdate = 0;

  int frame = 0;

	private String update;

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


    //map = new MapObject(startingLocationsData, mapData);
    //map = new MapObject(startingLocationsData, mapData, baseLocationsData, chokepointsData, regionsData);

  }//end Constructor

  public void parsePlayersData(String playersData ){//, String startingLocationsData, String mapData, String chokePointsData, String baseLocationsData, String regionsData){
//    System.out.println(playersData);
//    int num1 = playersData.split(":").length;
//    String[] players = new String[num1];
//    players = playersData.split(":");
     String[] players = playersData.split(":");
    playersInGame = new PlayerObject[players.length-1];

//    for (PlayerObject itr : playersInGame)
//      System.out.println("playersInGame[]: " + itr);

    System.out.println("num of players: " + playersInGame.length);
    for(int i = 1, j=0 ; i < players.length; i++,j++){
//      System.out.println(players[i]);
//      int num2 = players[i].split(";").length;
//      String[] player_att = new String[num2];
//      player_att = players[i].split(";");
      String[] player_att = players[i].split(";");
      playersInGame[j] = new PlayerObject();
      playersInGame[j].parseData(player_att);
    }
    

    //System.out.println("done: parsePlayersData");
  }
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


}//end GameObject

