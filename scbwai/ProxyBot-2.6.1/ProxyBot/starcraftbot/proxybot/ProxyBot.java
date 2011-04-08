package starcraftbot.proxybot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import starcraftbot.proxybot.command.GameCommandQueue;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;

/**
 * ProxyBot.
 * 
 * Manages socket connections with StarCraft and passes updates of the
 * game to ProxyBotAgent
 *
 * \see ProxyBotAgent
 */
public class ProxyBot {
  /**< port to start the server socket on */
	public static int port = 12345;
	
	/**< allow the user to control units */
	public static boolean allowUserControl = true;
	
	/**< turn on complete information */
	public static boolean completeInformation = false;

	/**< display agent commands in SC? */
	public static boolean logCommands = true;

	/**< display agent commands in SC? */
	public static boolean terrainAnalysis = false;

	/**< run the game very fast. 0 = really fast, 25 = slow enough to 
   * watch what is happening 
   */
	public static boolean speedUp = true;
	
	/**< The proxy bot client object */
  private ProxyBotAgentClient pba = null;

	/**< This is the game object that will contain game relevant data */
  private GameObject gameObj = null;
  private GameObjectUpdate gameObjUp = null;

  private GameCommandQueue starcraft_cmds = null;

  private int game_counter = 0;

  /**
   * The main class for ProxyBot.java
   * \arg \c String[] array of string arguments
   */
	public static void main(String[] args) {		
		new ProxyBot().start();
	}

	/**
	 * Starts the ProxyBot as a new thread. A server socket is opened and waits for 
   * client connections from the StarCraft game.
	 * \todo not sure why this is done, but further investigation of this is needed
   * \throws Exception
	 */
	public void start() {
    //This is the command queue that will store the commands that
    //will be sent to StarCraft
    starcraft_cmds = new GameCommandQueue();

    //create the ProxyBotAgentClient object
    pba = new ProxyBotAgentClient();
    
    //start the client with the initial data
    //DANGER: the bot name(s) will have to all have the same prefix in
    //order to get the jade.sniffer to listen to all of them
    pba.startClient("localhost", "1099", "KhasProxyBot");
    
		try {			
      ServerSocket serverSocket = new ServerSocket(port);
      
      while (true) {
        System.out.println("Waiting for client connection");
        Socket clientSocket = serverSocket.accept();	
        System.out.println("Received a client connection");
        runGame(clientSocket);
      }
		} catch(Exception e) {
			e.printStackTrace();
		}

	}//end start

	/**
	 * Manages communication with StarCraft.
   * \arg \c Socket socket that connects us to StarCraft
   * \throws SocketException
   * \throws Exception
	 */
	private void runGame(Socket socket) {		
    //NOTE: enable this to test the example bot
		//final StarCraftBot bot = new ExampleStarCraftBot();
    Game gameRef = null;

		try {
			// 1. get the initial game information
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String playerData = reader.readLine();

      // 2. respond with bot options
      String botOptions = (allowUserControl ? "1" : "0") 
                        + (completeInformation ? "1" : "0")
                        + (logCommands ? "1" : "0")
                        + (terrainAnalysis ? "1" : "0");
              
      socket.getOutputStream().write(botOptions.getBytes());
    
      // 3. get the starting locations and map information
      String locationData = reader.readLine();
      String mapData = reader.readLine();
      
      // TA
      String regionsData = "Regions:";
      String chokesData = "Chokes:";
      String basesData = "Bases:";
      
      if (terrainAnalysis) {
        regionsData = reader.readLine();
        chokesData = reader.readLine();
        basesData = reader.readLine();
      }
      
      //currently the old game object
      //final Game game = new Game(playerData, locationData, mapData, chokesData, basesData, regionsData);

      //this is the new game object
      gameObj = new GameObject(playerData, locationData, mapData, chokesData, basesData, regionsData);
       
      //this is the game update object
      gameObjUp = new GameObjectUpdate(gameObj);
      
      //gameRef = game;
      //NOTE: enable this to test the example bot
      //boolean firstFrame = true;

      //DEBUG
      //gameObj.printMapInfo();

      if (speedUp) {
        GameCommand game_speed = GameCommand.setGameSpeed(20);
        starcraft_cmds.addCommand(game_speed);
      }

      // 4. game updates
      while (true) {

        //at the game's fastest speed wait to run this loop once a second
    	  Thread.sleep( (1000 / 24) );

        if( (game_counter < 3) )
          game_counter++;


    	  //get update from StarCraft via the socket
        String update = reader.readLine();
        if (update.startsWith("ended")) {
          break;
        } else if (update == null) {
          break;
        } else {	    				    			
          // update the game
          //game.updateData(update);

          if( game_counter < 3 )
            gameObj.processGameUpdate(update);
          else
            gameObjUp.processGameUpdate(update);

          if( game_counter < 3 )
            pba.sendGameObjToJADE(gameObj);
          else
            pba.sendGameObjUpdateToJADE(gameObjUp);

          pba.getGameUpdateFromJADE();

          socket.getOutputStream().write(starcraft_cmds.cmdsToExe());
        }
      }//end while - game update loop
		} catch (SocketException e) {
			System.out.println("Socket Exception occurred");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("A General Exception occurred");
			e.printStackTrace();
		} finally {
			System.out.println("StarCraft game over");
			
			// stop update thread 
			gameRef.stop();

		}
	}
  /**
   * This class is the client that ProxyBot.java will use to communicate with ProxyBotAgent.java.
   * This is done, since communication between JADE and Java Applications must use the 
   * put02AObject() and getO2AObject() methods. This class uses the put02AObject() to send
   * data to ProxyBotAgent.java and receives data via an ArrayBlockingQueue jadeReplyQueue
   * 
   * \arg \c String host of the JADE environment Main Container 
   * \arg \c String port of the JADE environment Main Container
   * \arg \c String name the name of the agent
   */
  class ProxyBotAgentClient {

    //Agent that will be used to communicate with between ProxyBot <-> ProxyBotAgent
    //communication between JADE and non-JADE application
    AgentController ac;

    //This will be used to communicate back and forth
    //\todo the return type, currently String must be finalized
    ArrayBlockingQueue<GameCommandQueue> jadeReplyQueue = null;

    /**
     * Empty Constructor.
     */
    public ProxyBotAgentClient(){

    }

    /**
     * This method will initialize the client application as well as send the command
     * to create the proxy bot agent and setup the communication channels.
     * \arg \c String the host that the JADE platform is running on
     * \arg \c String the port of the JADE platform 
     * \arg \c String the name for the proxy bot agent
     * \return \c AgentController
     * \throws Exception
     */
    public AgentController startClient(String host, String port, String name ) {
      // Retrieve the singleton instance of the JADE Runtime
      Runtime rt = Runtime.instance();

      // Create a main container to host the Book Buyer agent 
      Profile p = new ProfileImpl();
      p.setParameter(Profile.MAIN_HOST, host);
      p.setParameter(Profile.MAIN_PORT, port);
      ContainerController cc = rt.createMainContainer(p); 

      //Parameters to pass to the agent
      Object[] agentArgs = new Object[2];

      //array queue of 20 for ProxyBot to Tx & RX with ProxyBotAgent
      jadeReplyQueue = new ArrayBlockingQueue<GameCommandQueue>(1);
    
      //pass a wait switch so that we can communicate with the agent once it has
      //been created
      ReadyToGo flip_switch = new ReadyToGo();

      //agent arguments
      agentArgs[0] = jadeReplyQueue;
      agentArgs[1] = flip_switch;

      // notice that in the book it was rt.createAgentContainer(p) which requires a main-container to be already active
      if (cc != null) {
        // Create the ProxyBotAgent and start it
        try {
          ac = cc.createNewAgent(name, "starcraftbot.proxybot.ProxyBotAgent", agentArgs);
          ac.start();

          //this switch is used to wait for the ProxyBotAgent to be created
          flip_switch.waitOn();

          return ac;
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
      return null;
    }

    /**
     * This method will send game updates to the ProxyBotAgent that will then relay
     * them to commander for dispersal.
     *
     * \arg \c GameObject the game object
     * \throws Exception
     */
    public void sendGameObjToJADE(GameObject game){
      //System.out.println("Sending game object to jade");
      try{
        ac.putO2AObject(game,AgentController.ASYNC);
       }catch(Exception e){
        e.printStackTrace();
      }
    }//end sendGameUpdateToJADE

    public void sendGameObjUpdateToJADE(GameObjectUpdate game_update){
      //System.out.println("Sending game object update to jade");
      try{
        ac.putO2AObject(game_update,AgentController.ASYNC);
       }catch(Exception e){
        e.printStackTrace();
      }
    }//end sendGameUpdateToJADE

    /**
     * This method will get game updates from ProxyBotAgent and will then pass them back up to  
     * the starcraft api
     * \todo we have to finalize what is being returned (a string, an object, ...)
     * \return String the updated information from JADE
     * \throws InterruptedException
     */
    public ArrayList<GameCommand> getUpdate(){
      ArrayList<GameCommand> reply = null;
      /*GameCommand temp = null;
      
      if(! jadeReplyQueue.isEmpty() ) {
        for(GameCommand cmd : jadeReplyQueue){
          try {
            temp = (GameCommand) jadeReplyQueue.take();
            reply.add(temp);
          } catch (InterruptedException ex) {
            Logger.getLogger(ProxyBot.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        return reply;
      }else{
        return null;
      }*/
      
      /*try {
		reply = (ArrayList<GameCommand>) jadeReplyQueue.take();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	return reply;
      
    }//end getGameUpdateFromJADE

    public void getGameUpdateFromJADE(){
      //ArrayList<GameCommand> commands = getUpdate();
      //GameCommand temp = null;

      try {
		starcraft_cmds = (GameCommandQueue)jadeReplyQueue.take();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
    	
      /*if(commands != null){
        for(GameCommand cmd : commands){
            starcraft_cmds.addCommand(cmd);
        }
      }*/
    }//end getGameUpdateFromJADE

  }//end ProxyBotAgentClient
  
}//end ProxyBot

