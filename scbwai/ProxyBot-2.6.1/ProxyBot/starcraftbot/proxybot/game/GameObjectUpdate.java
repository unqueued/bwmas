package starcraftbot.proxybot.game;

import java.util.*;
import java.io.*;

import starcraftbot.proxybot.command.GameCommand;
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
public class GameObjectUpdate implements Serializable {

  private PlayerObject myPlayer = null;
  
  /** all players */
  private ArrayList<PlayerObject> playersInGame;
  
  private Units unitsInGame;

  /**
   * Default Constructor. Does nothing.
   */
  public GameObjectUpdate (GameObject game) {
    myPlayer = game.getMyPlayer();
    playersInGame = game.getPlayersInGame();
    unitsInGame = new Units();
  }
 
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

//    System.out.println("myPlayer ID> " + myPlayer.getPlayerID());
    
//    unitsInGame = UnitsObject.parseUpdateUnits(update, this);
    unitsInGame.parseUpdateUnits(update,myPlayer.getPlayerID());
    
    //DEBUG
    //System.out.println("myPlayer> " + myPlayer);

	}

  public PlayerObject getMyPlayer() {
	// TODO Auto-generated method stub
    return myPlayer;
  }

  public Units getUnitsInGame() {
    return unitsInGame;
  }

public ArrayList<PlayerObject> getPlayersInGame(){
    return playersInGame;
  }
 
}//end GameObjectUpdate

