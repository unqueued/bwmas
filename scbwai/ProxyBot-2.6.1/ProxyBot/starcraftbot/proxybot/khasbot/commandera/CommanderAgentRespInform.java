package starcraftbot.proxybot.khasbot.commandera;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;

@SuppressWarnings("serial")
public class CommanderAgentRespInform extends CyclicBehaviour{
	Agent agent=null;	
  MessageTemplate mt = null;
  AID[] game_update_agents = null;
  String proxybot_agent_name = null;

  public CommanderAgentRespInform(Agent a, MessageTemplate mt, AID[] game_update_agents) {
    super(a);
    agent=a;
    this.mt=mt;
    this.game_update_agents = game_update_agents;
  }

  public void action() {
    //process only the Inform messages
    ACLMessage msg = agent.receive(mt);
    if (msg != null) {
      try{
        System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + msg.getContentObject());
      }catch(Exception e) {
        System.out.println(agent.getLocalName() + "FAIL: Unable to get message ContentObject");
      }
      //System.out.println(agent.getLocalName() + ": MSG RX : " + msg.getContent() ); 
      /*if (msg.getPerformative() == ACLMessage.INFORM) {
        //System.out.println(agent.getLocalName() + ": MSG INFORM : " + msg.getContent() ); 
        
        //handle the messages that come from Proxybot and send them to agents that need the game object update
        if(ParseACLMessage.isSenderProxyBot(msg)) {
          System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + msg.getContent());
          proxybot_agent_name = ParseACLMessage.getProxyBotName(msg);

          //sendGameUpdate2Agents(msg);

        }//end if ProxyBot
        //handle the messages that come from UnitManager and send them to ProxyBot 
        if(ParseACLMessage.isSenderUnitManager(msg)) {
          System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + msg.getContent());
          
          ACLMessage msg_gameObj_update = new ACLMessage(ACLMessage.INFORM); 
          msg_gameObj_update.addReceiver(new AID(proxybot_agent_name, AID.ISLOCALNAME));
          //msg_gameObj.setContent(gameObject);
          msg_gameObj_update.setContent("Game Object Update");
          agent.send(msg_gameObj_update);

        }//end if UnitManager
      }//end if ACLMessage.INFORM
      */
    } else {
      block();
    }

  }//end action

  private void sendGameUpdate2Agents(ACLMessage msg){
    //
    //game updates go to game_update_agents[]
    //
    for( int i=0; i < game_update_agents.length; i++){
      ACLMessage msg_gameObj = new ACLMessage(ACLMessage.INFORM); 
      msg_gameObj.addReceiver(game_update_agents[i]);
      try{
        msg_gameObj.setContentObject(msg.getContentObject());
      }catch(Exception e){
        System.out.println("Failed to serialize the Game object!!!");
      }
      agent.send(msg_gameObj);
    }

  }


}//end CommanderAgentRespInform

