
package starcraftbot.proxybot;

import jade.core.*;
import jade.core.behaviours.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.*;

import jade.domain.FIPANames;
import jade.domain.FIPANames.InteractionProtocol.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.concurrent.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;
import starcraftbot.proxybot.game.GameObject;

@SuppressWarnings("serial")
public class ProxyBotAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  // A queue for passing back a reply to the main thread:
  ArrayBlockingQueue<String> jadeReplyQueue = null;
  
   //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = new String [8];
 
  //variables to identify the CommanderAgent name & classname on JADE
  String commander_name = null;
  String commander_classname = null;

  public void setup() {		
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    //
    //Message Templates
    //
    
    //game updates will be INFORM messages
    MessageTemplate inform_mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);


    //setup the agent names and paths
    khasbot_agents[0] = "KhasCommander;starcraftbot.proxybot.khasbot.commandera.CommanderAgent";
    khasbot_agents[1] = getAID().getLocalName();
    khasbot_agents[2] = "KhasBuildingManager;starcraftbot.proxybot.khasbot.buildingma.BuildingManagerAgent";
    khasbot_agents[3] = "KhasStructureManager;starcraftbot.proxybot.khasbot.structurema.StructureManagerAgent";
    khasbot_agents[4] = "KhasUnitManager;starcraftbot.proxybot.khasbot.unitma.UnitManagerAgent";
    khasbot_agents[5] = "KhasBattleManager;starcraftbot.proxybot.khasbot.battlema.BattleManagerAgent";
    khasbot_agents[6] = "KhasMapManager;starcraftbot.proxybot.khasbot.mapma.MapManagerAgent";
    khasbot_agents[7] = "KhasResourceManager;starcraftbot.proxybot.khasbot.resourcema.ResourceManagerAgent";
    
    //register the SL codec with the content manager
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontology);

    // Enable O2A Communication
    setEnabledO2ACommunication(true, 20);

    Object[] args = getArguments();

    //Now read in the arguments and make sure to set the 
    //jadeReplyQueue to communicate back to the AgentClient app
    jadeReplyQueue = (ArrayBlockingQueue<String>) args[0];

    //ReadyToGo flip_switch = (ReadyToGo) args[1];
    
    //notify ProxBot Client that the object is up and ready to go
    //flip_switch.signal(); 

    commander_name = (String)Array.get(khasbot_agents[0].split(";"),0);
    commander_classname = (String)Array.get(khasbot_agents[0].split(";"),1);
   
    //create the commander agent
    ProxyBotAgentCreateAgents agents = new ProxyBotAgentCreateAgents(khasbot_agents, commander_name, commander_classname);
    agents.createAgents(this);
  
    //Behaviours
    
    //This cyclic behaviour will process all the incoming messages from the ProxyBotClient
    //application
    //NOTE: all behaviours must block to keep the cpu cycles from just being busy waits
    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    root_behaviour.addSubBehaviour(new ProxyBotAgentPB2A(this));
    root_behaviour.addSubBehaviour(new ProxyBotAgentA2PB(this,inform_mt));

    addBehaviour(root_behaviour);
	}//end setup

  public void takeDown() {
    
    // Disable O2A Communication
    setEnabledO2ACommunication(false, 0);
  }


  /**
   * This class will be responsible for taking input from the ProxyBot client application 
   * and passing the information to the CommanderAgent 
   */
  class ProxyBotAgentPB2A extends CyclicBehaviour {
    Agent agent = null;

    public ProxyBotAgentPB2A(Agent a){
      super(a);
      agent = a;
    }

    public void action() {
      //TODO: this will end up being the Game Object
      GameObject game = (GameObject) getO2AObject();
      if (game != null) {
        
        //now create a message and send it to the CommanderAgent
        // Fill the REQUEST message
    		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);  
        msg.addReceiver(new AID(commander_name, AID.ISLOCALNAME));

        try{
          msg.setContentObject(game);
        }catch(Exception e) {
          System.out.println(agent.getLocalName() + " FAIL: failed to serialize Game object!!! >>>");
          e.printStackTrace();
          System.out.println(agent.getLocalName() + " FAIL: failed to serialize Game object!!! <<<");
        }

        agent.send(msg);
      } else {
        block();
      }
    }//end action
  }//end ProxyBotAgentO2AProcess

  /**
   * This class will be responsible for taking input from the CommanderAgent and passing it
   * up to the ProxyBot client application.
   */
  class ProxyBotAgentA2PB extends CyclicBehaviour {
    Agent agent = null;
    MessageTemplate mt = null;

    public ProxyBotAgentA2PB(Agent a, MessageTemplate mt){
      super(a);
      agent=a;
    }

    /** 
     * This agent will use its incoming message queue to send updates back to the 
     * ProxyBotClient application
     *
     */
    public void action() {
      //process only the Inform messages
      ACLMessage msg = agent.receive(mt);
      if (msg != null) {
        //System.out.println(agent.getLocalName() + ": MSG RX : " + msg.getContent() ); 
        if (msg.getPerformative() == ACLMessage.INFORM) {
          //System.out.println(agent.getLocalName() + ": MSG INFORM : " + msg.getContent() ); 
          
          //handle the messages that come from CommanderAgent which will be the game object 
          if(ParseACLMessage.isSenderCommander(msg)) {
            System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + msg.getContent());

            //
            // Process the game update that was received. Pass on the information to ProxyBot client 
            // application by placing data onto the reply queue
            //
            try {
              jadeReplyQueue.put( msg.getContent() );
            } catch( InterruptedException ie ) {
              System.err.println( "ERROR while sending reply '" + msg + "' back to caller thread..." );
              ie.printStackTrace();
            }  
          }
        }
      } else {
        block();
      }     
    }//end action
  }//end ProxyBotAgentA2PB

}//end ProxyBotAgent





