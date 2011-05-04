package starcraftbot.proxybot.game;

import java.util.*;
import java.io.*;

import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.khasbot.mapma.MapLocation;
import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.Units;


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
  private PlayerObject enemyPlayer = null;

  private int myPlayerId = -10;
  private int enemyPlayerId = -10;

  /** all players */
  private ArrayList<PlayerObject> playersInGame = null;
  
  private Units unitsInGame = null;

  /** map information */
  private MapObject map = null;

  private MapLocation myStartLocation = null;

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
    
    myPlayerId = Integer.parseInt(playerDatas[0].split(";")[1]);
    if( myPlayerId == 0 )
      enemyPlayerId = 1;
    else
      enemyPlayerId = 0;

    //set myPlayer from the players on our list
    for(PlayerObject tmp : playersInGame){
      if(tmp.getPlayerID() == myPlayerId){
        myPlayer = tmp;
      }
      else if(tmp.getPlayerID() == enemyPlayerId){
        enemyPlayer = tmp;
      }
    }

    unitsInGame = new Units();

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

    unitsInGame.parseUpdateUnits(update,myPlayer.getPlayerID(),enemyPlayer.getPlayerID());

    myStartLocation = map.getStartLocation(myPlayer.getPlayerID());
	}

  public PlayerObject getMyPlayer() {
    return myPlayer;
  }

  public PlayerObject getEnemyPlayer() {
    return enemyPlayer;
  }

  public Units getUnitsInGame() {
    return unitsInGame;
  }

  public ArrayList<PlayerObject> getPlayersInGame(){
    return playersInGame;
  }
  
  @Override
  public String toString()
  {
	  String s = "GameObject: my Player->#"+myPlayerId+":"+myPlayer.getPlayerName()+":"+myPlayer.getPlayerRace()+"| map: "+ map.toString();
	  
	  return s;
  }

  public MapObject getMapObj(){
    return map;
  }

  public MapLocation getMyStartLocation(){
    return myStartLocation;
  }

}//end GameObject

