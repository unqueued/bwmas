
package starcraftbot.proxybot.game;

import java.io.*;
import java.util.*;

public class PlayerObject implements Serializable {

	private int playerID;

  private Race playerRace;

  private String playerName;

  public PlayerObject(){

    playerID = -10;
    playerRace = null;
    playerName = null;

  }

  /**
   * This will split the string of the playerData to determine how many players there are
   */
  public static ArrayList<PlayerObject> parsePlayersData(String playersData) {
    ArrayList<PlayerObject> playersInGame = new ArrayList<PlayerObject>();
    
    String[] players = playersData.split(":");
    PlayerObject temp = new PlayerObject();

    for(int i = 1; i < players.length; i++){
      String[] player_att = players[i].split(";");

      temp.setPlayerID(Integer.parseInt(player_att[0]));
      temp.setPlayerRace(player_att[1]);
      temp.setPlayerName(player_att[2]);

    }
  
    return playersInGame;
  }

  public void setPlayerID(int num){
    playerID = num;
  }

  public void setPlayerRace(String race_name){
    if( race_name.equals(Race.Protoss.name()) )
      playerRace = Race.Protoss;
    else if( race_name.equals(Race.Terran.name()) )
      playerRace = Race.Terran; 
    else if( race_name.equals(Race.Zerg.name()) )
      playerRace = Race.Zerg; 
  }

  public void setPlayerName(String name){
    playerName = name;
  }

  @Override
  public String toString(){
    return "PlayerID: " + playerID + " PlayerRace: " + playerRace.name() + " PlayerName: " + playerName ;
  }

}//end PlayerObject

