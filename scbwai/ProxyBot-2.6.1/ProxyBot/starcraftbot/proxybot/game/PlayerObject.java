
package starcraftbot.proxybot.game;

import java.io.*;
import java.util.*;

import starcraftbot.proxybot.khasbot.resourcema.PlayerResources;
import starcraftbot.proxybot.khasbot.structurema.PlayerTech;
/**
 * The PlayerObject holds what each player would know.  Name, Race, Resources and Tech.
 * 
 * 
 * @author Antonio Arredondo
 *
 */
public class PlayerObject implements Serializable {

  /** id of this player */
  private int playerID;
  /** race of this player */
  private Race playerRace;
  /** name of this player */
  private String playerName;
  /** current resources for this player*/
  private PlayerResources myResources = null;
  /** current tech for this player*/
  private PlayerTech myTech = null;

  /**
   * Default no arg constructor
   * 
   * Sets up PlayerObject which includes ID, Race, Name, Resources, and Tech...
   */
  public PlayerObject(){

    playerID = -10;
    playerRace = null;
    playerName = null;

    myResources = new PlayerResources();
    myTech = new PlayerTech();
  }

  /**
   * This will split the string of the playerData to determine how many players there are
   * 
   * @param String playersData input String received from the .dll Module
   * @return ArrayList<PlayerObject> playersInGame list of players including neutral.
   */
  public static ArrayList<PlayerObject> parsePlayersData(String playersData) {
    ArrayList<PlayerObject> playersInGame = new ArrayList<PlayerObject>();
    
    String[] players = playersData.split(":");
    
    for(int i = 1; i < players.length; i++){
      PlayerObject temp = new PlayerObject();
      String[] player_att = players[i].split(";");

      temp.setPlayerID(Integer.parseInt(player_att[0]));
      temp.setPlayerRace(player_att[1]);
      temp.setPlayerName(player_att[2]);
      
      playersInGame.add(temp);
    }
  
    return playersInGame;
  }

  /* Mutators */
  /**
   * 
   * @param num
   */
  public void setPlayerID(int num){
    playerID = num;
  }

  /**
   * 
   * @param race_name
   */
  public void setPlayerRace(String race_name){
    if( race_name.equals(Race.Protoss.name()) )
      playerRace = Race.Protoss;
    else if( race_name.equals(Race.Terran.name()) )
      playerRace = Race.Terran; 
    else if( race_name.equals(Race.Zerg.name()) )
      playerRace = Race.Zerg; 
  }

  /**
   * 
   * @param name
   */
  public void setPlayerName(String name){
    playerName = name;
  }

  /* Accessors */
  public int getPlayerID(){
    return playerID;
  }

  public Race getPlayerRace(){
    return playerRace;
  }

  public String getPlayerName(){
    return playerName;
  }

  /**
   * This updates the Resources and Tech for the given PlayerObject
   * 
   * Calls update() on myResources and myTech.
   * 
   * @param String in_minerals
   * @param String in_gas
   * @param String in_supplyUsed
   * @param String in_supplyTotal
   * @param String in_researchUpdate
   * @param String in_upgradeUpdate
   * 
   */
  public void updateAttributes(String in_minerals, String in_gas, String in_supplyUsed, String in_supplyTotal, String in_researchUpdate, String in_upgradeUpdate){
    myResources.update(in_minerals,in_gas, in_supplyUsed, in_supplyTotal); 
    myTech.update(in_researchUpdate,in_upgradeUpdate);
  }


  @Override
  public String toString(){
    return "PlayerID: " + playerID + 
           " PlayerRace: " + playerRace.name() + 
           " PlayerName: " + playerName + " " +
           myResources + " " +
           myTech ;
  }

}//end PlayerObject

