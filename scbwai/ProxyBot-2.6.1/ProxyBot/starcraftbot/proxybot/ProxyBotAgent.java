
package starcraftbot.proxybot;

import jade.core.*;
import jade.core.behaviours.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.domain.FIPANames;
import jade.domain.FIPANames.InteractionProtocol.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("serial")
public class ProxyBotAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  // A queue for passing back a reply to the main thread:
  ArrayBlockingQueue<String> jadeReplyQueue = null;
  
   //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = new String [7];
  
  public void setup() {		
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    //create message template to communicate with the JADE platform
    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    String commander_name = null;
    String commander_classname = null;

    //setup the agent names and paths
    khasbot_agents[0] = "KhasCommander;starcraftbot.proxybot.khasbot.commandera.CommanderAgent";
    khasbot_agents[1] = "KhasBuildingManager;starcraftbot.proxybot.khasbot.buildingma.BuildingManagerAgent";
    khasbot_agents[2] = "KhasStructureManager;starcraftbot.proxybot.khasbot.structurema.StructureManagerAgent";
    khasbot_agents[3] = "KhasUnitManager;starcraftbot.proxybot.khasbot.unitma.UnitManagerAgent";
    khasbot_agents[4] = "KhasBattleManager;starcraftbot.proxybot.khasbot.battlema.BattleManagerAgent";
    khasbot_agents[5] = "KhasMapManager;starcraftbot.proxybot.khasbot.mapma.MapManagerAgent";
    khasbot_agents[6] = "KhasResourceManager;starcraftbot.proxybot.khasbot.resourcema.ResourceManagerAgent";
    
    //register the SL codec with the content manager
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontology);

    // Enable O2A Communication
    setEnabledO2ACommunication(true, 20);

    Object[] args = getArguments();

    ReadyToGo flip_switch = (ReadyToGo) args[0];
    
    //notify Client that the object is up and ready to go
    flip_switch.signal(); 

    //Now read in the arguments and make sure to set the 
    //jadeReplyQueue to communicate back to the AgentClient app
    jadeReplyQueue = (ArrayBlockingQueue<String>) args[1];

    commander_name = (String)Array.get(khasbot_agents[0].split(";"),0);
    commander_classname = (String)Array.get(khasbot_agents[0].split(";"),1);
    
    ProxyBotAgentCreateAgents agents = new ProxyBotAgentCreateAgents(khasbot_agents, commander_name, commander_classname);
    agents.createAgents(this);

    //add behaviours here
    
    //This cyclic behaviour will process all the incoming messages from the ProxyBotClient
    //application
    //NOTE: all behaviours must block to keep the cpu cycles from just being busy waits
    addBehaviour(new ProxyBotAgentO2AProcess(this));
    //addBehaviour(new ProxyBotAgentA2OProcess(this),jadeReplyQueue);
	}

  public void takeDown() {
    
    // Disable O2A Communication
    setEnabledO2ACommunication(false, 0);
  }


  /**
   * This class will be responsible for taking input from the ProxyBot client application 
   * and passing the information to the commander agent 
   */
  class ProxyBotAgentO2AProcess extends CyclicBehaviour {

    public ProxyBotAgentO2AProcess(Agent a){
      super(a);
    }

    public void action() {
      //Game game_obj = (Game) myAgent.getO2AObject();
      String game = (String) getO2AObject();
      if (game != null) {
        System.out.println("Received game object");
        System.out.println("RX: " + game);
      } else {
        block();
      }
    }//end action
  }//end ProxyBotAgentO2AProcess

  /**
   * This class will be responsible for taking input from the commander and passing it
   * up to the ProxyBot client application.
   */
  class ProxyBotAgentA2OProcess extends CyclicBehaviour {

    public ProxyBotAgentA2OProcess(Agent a){
      super(a);
    }

    /** 
     * This agent will use its incoming message queue to send updates back to the 
     * ProxyBotClient application
     *
     */
    public void action() {
      /* 
      if (game != null) {
       
        // Passing back the reply to the caller:
        try {
          replyQueue.put( reply );
        } catch( InterruptedException ie ) {
          System.err.println( "ERROR while sending reply '" + reply + "' back to caller thread..." );
          ie.printStackTrace();
        }
      } else {
        block();
      }
      */
    }//end action

  }//end ProxyBotAgentA2OProcess 

}//end ProxyBotAgent

