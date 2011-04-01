package starcraftbot.proxybot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.lang.*;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import starcraftbot.proxybot.bot.ExampleStarCraftBot;
import starcraftbot.proxybot.bot.StarCraftBot;
import starcraftbot.proxybot.command.*;
import starcraftbot.proxybot.command.Command;
import starcraftbot.proxybot.command.Command.StarCraftCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.wmes.UnitTypeWME;

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
      final Game game = new Game(playerData, locationData, mapData, chokesData, basesData, regionsData);

      //this is the new game object being developed
      gameObj = new GameObject(playerData, locationData, mapData, chokesData, basesData, regionsData);

      gameRef = game;
      //NOTE: enable this to test the example bot
      //boolean firstFrame = true;

      //DEBUG
      //gameObj.printMapInfo();

      if (speedUp) {
        game.getCommandQueue().setGameSpeed(0);
        
        gameObj.getCommandsToDo().add(new Command(Command.StarCraftCommand.gameSpeed, 0, 0,0,0));
        
      }

      // 4. game updates
      while (true) {
        //get update from StarCraft via the socket
    	
    	  //Thread.sleep( (long)(1000 / (24 * 3) ));
    	  
        String update = reader.readLine();
        if (update.startsWith("ended")) {
          break;
        } else if (update == null) {
          break;
        } else {	    				    			
          // update the game
          game.updateData(update);	    			
          gameObj.processGameUpdate(update);
          
          pba.sendGameUpdateToJADE(gameObj);
          
          gameObj = pba.getGameUpdateFromJADE();
          
          //socket.getOutputStream().write(game.getCommandQueue().getCommands().getBytes());
          
          socket.getOutputStream().write(gameObj.getCommandString().getBytes());
          
          gameObj.clearCommands();
          
          //AA: here is where ProxyBot will send data to ProxyBotAgent (maybe?)
          //pba.sendUpdate(game);

          //NOTE: enable this to test the example bot
//        if (firstFrame) {
//     		  firstFrame = false;
//     			    				
//     		//start the agent
//          new Thread() {
//     				public void run() {
//     	  		  bot.start(game);
//     				}
//     			}.start();
//    		} 

          //5. send commands
//          System.out.println(":::::::::::::::::PROXYBOT:::::::::::::: sending commands to AIModule:");
//
//          String com = commandQueue.getCommands();
//	    		if(!com.isEmpty() && !com.trim().matches("commands"))
//	    		{
//		    		//System.out.println("Unparsed game commands-"+com+"----");
//		    		String coms[] = com.split(":");
//		    		for(String c : coms)
//		    		{
//		    			if(!c.endsWith("commands"))
//		    			{
//			  				String command[] = c.split(";");
//			  				if(command.length == 5)
//			  				{
//			  					System.out.print("	Command Name: "+StarCraftCommand.values()[Integer.parseInt(command[0])].name());
//			  					if(Integer.parseInt(command[1]) < game.getUnits().size())
//			  					{
//			  						System.out.print(" on unit["+ game.getUnits().get(Integer.parseInt(command[1])).getType().getName()+":#"+Integer.parseInt(command[1])+"]");
//			  						System.out.println("|arg1:"+command[2]+"|arg2:"+ command[3]+"|arg3:"+command[4]);
//			  					}
//			  					else
//			  						System.out.println(" on unit that is out of bounds...->"+command[1]+" with game.getUnits().size()="+game.getUnits().size());
//			  				}
//			  				else
//			  					System.out.println("weird lengthon command[]");
//		    			}
//		    		}
//		    		System.out.println("--------------------------------------------------------------------");
//	    		}

          //socket.getOutputStream().write(game.getCommandQueue().getCommands().getBytes());
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
			
      //NOTE: enable this to test the example bot
			//stop the bot
//			if (bot != null) {
//				bot.stop();
//			}
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
    ArrayBlockingQueue<GameObject> jadeReplyQueue = null;

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
      jadeReplyQueue = new ArrayBlockingQueue<GameObject>(20); 
    
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
    public void sendGameUpdateToJADE(GameObject game){
      try{
        ac.putO2AObject(game,AgentController.ASYNC); 
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
    public GameObject getGameUpdateFromJADE(){
      GameObject reply = null;
      try {
        reply = (GameObject)jadeReplyQueue.take();
      } catch(InterruptedException ie) {
        ie.printStackTrace();
      }
      return reply;
    }//end getGameUpdateFromJADE
  }//end ProxyBotAgentClient
}//end ProxyBot

