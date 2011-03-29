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
import starcraftbot.proxybot.command.Command.StarCraftCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.wmes.UnitTypeWME;

/**
 * ProxyBot.
 * 
 * Manages socket connections with StarCraft and passes updates of the
 * game to ProxyBotAgent
 *
 * @see ProxyBotAgent
 */
public class ProxyBot {
  /** port to start the server socket on */
	public static int port = 12345;
	
	/** allow the user to control units */
	public static boolean allowUserControl = true;
	
	/** turn on complete information */
	public static boolean completeInformation = false;

	/** display agent commands in SC? */
	public static boolean logCommands = true;

	/** display agent commands in SC? */
	public static boolean terrainAnalysis = false;

	/** run the game very fast ? */
	public static boolean speedUp = true;
	
  private ProxyBotAgentClient pba = null;

  private GameObject gameObj = null;

	public static void main(String[] args) {		
		new ProxyBot().start();
	}

	/**
	 * Starts the ProxyBot.
	 * 
	 * A server socket is opened and waits for client connections.
	 */
	public void start() { 

    //AA: This is just test code for ensure back and forth communication 
    //AA: with the JADE platform and this Java application
    pba = new ProxyBotAgentClient();
    gameObj = new GameObject();
    pba.startClient("localhost", "1099", "KhasProxyBot");
     
    
		try {			
		    ServerSocket serverSocket = new ServerSocket(port);
		    
		    while (true) {
			    System.out.println("Waiting for client connection");

			    Socket clientSocket = serverSocket.accept();	
			    System.out.println("Received a client connection");
			    runGame(clientSocket);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}//end start

	/**
	 * Manages communication with StarCraft.
	 */
	private void runGame(Socket socket) {		
		//final StarCraftBot bot = new ExampleStarCraftBot();
    Game gameRef = null;

    //AA: i will uncomment this when i have properly tested it
    //AA: so for I just implemented it and tested it without running
    //AA: the starcraft api
    //ProxyBotAgentClient pba = new ProxyBotAgentClient();
    //pba.startClient("localhost", "1099", "KhasProxyBot");


		try {
			// 1. get the initial game information
		    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	String playerData = reader.readLine();
        gameObj.parsePlayersData(playerData);

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

	    	final Game game = new Game(playerData, locationData, mapData, chokesData, basesData, regionsData);
        

	    	gameRef = game;
	    	boolean firstFrame = true;

	    	if (speedUp) {
	    		game.getCommandQueue().setGameSpeed(20);
	    	}
	    	// 4. game updates
	    	while (true) {
	    		
	    		String update = reader.readLine();
	    		if (update.startsWith("ended")) {
	    			break;
	    		} else if (update == null) {
	    			break;
	    		} else {	    				    			
	    			// update the game
	    			game.updateData(update);	    			
            
            //AA: here is where ProxyBot will send data to ProxyBotAgent (maybe?)
            //pba.sendUpdate(game);
            
	    			//if (firstFrame) {
	    			//	firstFrame = false;
	    					    				
	    			//	 start the agent
            //   new Thread() {
	    			//		public void run() {
	    	    //				bot.start(game);
	    			//		}
	    			//	}.start();
	    			//}

	    			// 5. send commands
	    			//System.out.println(":::::::::::::::::PROXYBOT:::::::::::::: sending commands to AIModule:");

//            String com = commandQueue.getCommands();
//	    			if(!com.isEmpty() && !com.trim().matches("commands"))
//	    			{
//		    			//System.out.println("Unparsed game commands-"+com+"----");
//		    			String coms[] = com.split(":");
//		    			for(String c : coms)
//		    			{
//		    				if(!c.endsWith("commands"))
//		    				{
//			    				String command[] = c.split(";");
//			    				if(command.length == 5)
//			    				{
//			    					System.out.print("	Command Name: "+StarCraftCommand.values()[Integer.parseInt(command[0])].name());
//			    					if(Integer.parseInt(command[1]) < game.getUnits().size())
//			    					{
//			    						System.out.print(" on unit["+ game.getUnits().get(Integer.parseInt(command[1])).getType().getName()+":#"+Integer.parseInt(command[1])+"]");
//			    						System.out.println("|arg1:"+command[2]+"|arg2:"+ command[3]+"|arg3:"+command[4]);
//			    					}
//			    					else
//			    						System.out.println(" on unit that is out of bounds...->"+command[1]+" with game.getUnits().size()="+game.getUnits().size());
//			    				}
//			    				else
//			    					System.out.println("weird lengthon command[]");
//		    				}
//		    			}
//		    			System.out.println("--------------------------------------------------------------------");
//	    			}

	    			socket.getOutputStream().write(game.getCommandQueue().getCommands().getBytes());
	    		}
	    	}
		}
		catch (SocketException e) {
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("StarCraft game over");
			
			// stop update thread 
			gameRef.stop();
			
			//stop the bot
//			if (bot != null) {
//				bot.stop();
//			}
		}
	}
  /**
   *
   * Parameters:
   * @param host JADE environment Main Container host
   * @param port JADE environment Main Container port
   * @param name Agent name
   */
  class ProxyBotAgentClient {
    private Logger out = Logger.getLogger(getClass().getName());

    //Agent that will be used to communicate with between ProxyBot <-> ProxyBotAgent
    //communication between JADE and non-JADE application
    AgentController ac;

    //This will be used to communicate back and forth
    //ArrayBlockingQueue<String> jadeReplyQueue = null;
    ArrayBlockingQueue<String> jadeReplyQueue = null;

    public ProxyBotAgentClient(){
      //set the level for the logger
      //use Level.OFF to turn it off
      out.setLevel(Level.FINE);
    }

    public AgentController startClient(String host, String port, String name ) {
      // Retrieve the singleton instance of the JADE Runtime
      Runtime rt = Runtime.instance();

      // Create a main container to host the Book Buyer agent 
      Profile p = new ProfileImpl();
      //p.setParameter(Profile.MAIN_HOST, host);
      //p.setParameter(Profile.MAIN_PORT, port);
      ContainerController cc = rt.createMainContainer(p); 

      //Parameters to pass to the agent
      Object[] agentArgs = new Object[2];

      //array queue of 20 for ProxyBot to Tx & RX with ProxyBotAgent
      jadeReplyQueue = new ArrayBlockingQueue<String>(20); 
    
      //pass a wait switch so that we can communicate with the agent once it has
      //been created
      //ReadyToGo flip_switch = new ReadyToGo();

      //agent arguments
      agentArgs[0] = jadeReplyQueue;
      //agentArgs[1] = flip_switch;

      // notice that in the book it was rt.createAgentContainer(p) which requires a main-container to be already active
      if (cc != null) {
        // Create the ProxyBotAgent and start it
        try {
          ac = cc.createNewAgent(name, "starcraftbot.proxybot.ProxyBotAgent", agentArgs);
          ac.start();

          //TODO: if agents are still not responding, then enable this wait 
          //flip_switch.waitOn();

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
     */
    public void sendUpdate(GameObject game){
      
    //public void sendUpdate(String game){
      try{
        ac.putO2AObject(game,AgentController.ASYNC); 
       }catch(Exception e){
        e.printStackTrace();
      }
    }//end sendUpdate

    /**
     * This method will get game updates from ProxyBotAgent and will then pass them back up to  
     * the starcraft api
     *
     */
    public void getUpdate(){
      String reply = null;

      try {
        reply = (String)jadeReplyQueue.take();
      } catch( InterruptedException ie ) {
        ie.printStackTrace();
      }
      //now process the incoming update
      System.out.println(this.getClass().getName() + " RX: " + reply );
      //out.info(this.getClass().getName() + " RX: " + reply );

    }//end getUpdate

  }//end StartProxyBotAgent

}//end ProxyBot

