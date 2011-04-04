
package starcraftbot.proxybot;

import jade.core.*;
import jade.core.behaviours.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;

import jade.domain.FIPANames.InteractionProtocol.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;

import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.command.GameCommand;


/**
 * This is the Agent that the Java Application ( ProxyBot.java ) will talk to.
 * This agent will handle all communication in and out of the JADE platform, including
 * some processing of the messages.
 * \see ProxyBot.java
 */
@SuppressWarnings("serial")
public class ProxyBotAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  // A queue for passing back a reply to the ProxyBot.java 
  ArrayBlockingQueue<GameCommand> jadeReplyQueue = null;
  
  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  //this value is HARDCODE in ProxyBot.java
  String [] khasbot_agents = new String [8];
 
  //variables to identify the CommanderAgent name & classname on JADE
  String commander_name = null;
  String commander_classname = null;
  AID commander = null;

  /**
   * This is the setup method for the JADE Agent ProxyBotAgent.java. 
   */
  public void setup() {		
    //DEBUG
    //System.out.println(getAID().getLocalName() + ": is alive !!!");

    //
    //Message Templates
    //
    
    //game updates will be INFORM messages that come from CommanderAgent.java
    MessageTemplate commander_inform_mt = null;

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

    //this is used to get the arguments passed to this agent
    Object[] args = getArguments();

    //Now read in the arguments and make sure to set the 
    //jadeReplyQueue to communicate back to the AgentClient app
    jadeReplyQueue = (ArrayBlockingQueue<GameCommand>) args[0];

    ReadyToGo flip_switch = (ReadyToGo) args[1];
    
    //notify ProxBot Client that the object is up and ready to go
    flip_switch.signal(); 

    //parse the commander name 
    commander_name = (String)Array.get(khasbot_agents[0].split(";"),0);
    
    //parse the commander classname 
    commander_classname = (String)Array.get(khasbot_agents[0].split(";"),1);
    
    //create the commander AID reference
    commander = new AID(commander_name,AID.ISLOCALNAME);

    //update the commander specific message template
    commander_inform_mt = MessageTemplate.and(
                                              MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                              MessageTemplate.MatchSender(commander)
                                              );

    //init the class for creating the commander
    ProxyBotAgentCreateAgents agents = new ProxyBotAgentCreateAgents(khasbot_agents, commander_name, commander_classname); 

    //create the commander agent, which will then create all the remaining agents that will be
    //used in the system
    agents.createAgents(this);

    //MainWindow sniffer_gui = new MainWindow()

    /* 
     * This ParallelBehaviour will process all the incoming messages from the proxy bot client application
     * NOTE: all behaviours must block to keep the cpu cycles from just being busy waits
     */
    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    // This behaviour is used to accept incoming messages from the proxy bot client to this agent 
    root_behaviour.addSubBehaviour(new ProxyBotAgentPB2A(this));

    // This behaviour is used to send messages to the proxy bot client 
    root_behaviour.addSubBehaviour(new ProxyBotAgentA2PB(this,commander_inform_mt));

    addBehaviour(root_behaviour);
	}//end setup

  public void takeDown() {
    // Disable O2A Communication
    setEnabledO2ACommunication(false, 0);
  }


  /**
   * This class will be responsible for taking input from the proxy bot client application 
   * and passing the information to the commander agent.
   * \see ProxyBotClient.java
   * \see CommanderAgent.java
   */
  class ProxyBotAgentPB2A extends CyclicBehaviour {
    Agent agent = null;

    /**
     * Single argument constructor.
     * \arg \c Agent pass in the agent
     */
    public ProxyBotAgentPB2A(Agent a){
      super(a);
      agent = a;
    }

    /**
     * This is the setup method for the JADE Agent ProxyBotAgentPB2A.java. 
     */
    public void action(){
      //GameObject gets passed to the CommanderAgent
      Object obj = getO2AObject();
      GameObject game = null;
      GameObjectUpdate game_update = null;

      if( obj != null ){
        //System.out.println("Object received!");
        if(obj instanceof GameObject){
          //System.out.println("Object is GameObject!");
          game = (GameObject)obj;
        }else if(obj instanceof GameObjectUpdate){
          //System.out.println("Object is GameObjectUpdate!");
          game_update = (GameObjectUpdate)obj;
        }else{
          System.out.println("Object is unknown!");
          System.out.println("Object" + obj.toString());
        }

        if(game != null || game_update != null){
          //now create a message and send it to the CommanderAgent
          //MUST use ACLMessage.INFORM
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.addReceiver(commander);
          try{
            if(game != null){
              msg.setConversationId(ConverId.Game.InitGameObj.getConId());
              msg.setContentObject(game);
              //send the message
              agent.send(msg);
            } else if(game_update != null){
              msg.setConversationId(ConverId.Game.GameObjUpdate.getConId());
              msg.setContentObject(game_update);
              //send the message
              agent.send(msg);
            }
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      }else{
        block();
      }
    }//end action
  }//end ProxyBotAgentO2AProcess

  /**
   * This class will be responsible for taking input from the CommanderAgent and passing it
   * up to the proxy bot client application.
   * \see ProxyBotClient.java
   * \see CommanderAgent.java
   */
  class ProxyBotAgentA2PB extends CyclicBehaviour {
    Agent agent = null;
    MessageTemplate mt = null;

    /**
     * This constructor takes the agent and a message template that is tailored to only scan
     * for messages from the commander agent.
     * \arg \c Agent pass in the agent
     * \arg \c MessageTemplate pass in the commander agent specific message template
     */
    public ProxyBotAgentA2PB(Agent a, MessageTemplate mt){
      super(a);
      agent=a;
    }

    /** 
     * This agent will use its incoming message queue to send updates back to the 
     * proxy bot client application
     */
    public void action() {
      ACLMessage msg = agent.receive(mt);
      if (msg != null) {
        if (msg.getConversationId().equals(ConverId.Commands.ExecuteCommand.getConId())) {
          try {
              //
              // Process the game update that was received. Pass on the information to ProxyBot client
              // application by placing command data onto the reply queue
              //
              jadeReplyQueue.put((GameCommand)msg.getContentObject());
            } catch (UnreadableException ex) {
              Logger.getLogger(ProxyBotAgent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
              Logger.getLogger(ProxyBotAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      } else {
        block();
      }     
    }//end action

  }//end ProxyBotAgentA2PB
}//end ProxyBotAgent

