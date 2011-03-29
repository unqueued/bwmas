
package starcraftbot.proxybot.game;

import java.io.*;

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
  public void parseData(String[] player_att) {
//    System.out.println("In parsePlayerData(), player_att is:");
//    for(String att : player_att)    {
//      System.out.println("  "+att);
//    }

      //System.out.println("Setting playerID");
      playerID = Integer.parseInt(player_att[0]);
      //System.out.println("Setting Player Race");
      setPlayerRace(player_att[1]);
      //System.out.println("Setting player Name");
      setPlayerName(player_att[2]);
      //setPlayerType(player_att[3]);
      //setPlayerAlly(player_att[4]);

    //System.out.println("done parsePlayers ");
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

