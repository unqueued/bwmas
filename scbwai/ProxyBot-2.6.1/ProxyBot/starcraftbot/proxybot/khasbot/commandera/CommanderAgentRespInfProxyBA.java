package starcraftbot.proxybot.khasbot.commandera;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class CommanderAgentRespInfProxyBA extends CyclicBehaviour{
	Agent agent=null;	
  MessageTemplate mt = null;
  AID unit_manager = null;

  public CommanderAgentRespInfProxyBA(Agent a,
                                      MessageTemplate mt,
                                      AID unit_m) {
    super(a);
    agent=a;
    this.mt=mt;
    unit_manager = unit_m;
  }

  @Override
  public void action() {
    ACLMessage msg = agent.receive(mt);
    if (msg != null){
      //now i'm going to match the conversation to see if its a
      //GameObject or a GameObjectUpdate
      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
        ACLMessage msg_gameObj = new ACLMessage(ACLMessage.INFORM);
        msg_gameObj.setConversationId(ConverId.Game.InitGameObj.getConId());
        try{
          msg_gameObj.setContentObject(msg.getContentObject());
        }catch(Exception e){
          System.out.println("Failed to serialize the GameObject: " + e.toString());
        }
        msg_gameObj.addReceiver(unit_manager);
        agent.send(msg_gameObj);
      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
        ACLMessage msg_gameObjUp = new ACLMessage(ACLMessage.INFORM);
        msg_gameObjUp.setConversationId(ConverId.Game.GameObjUpdate.getConId());
        try{
          msg_gameObjUp.setContentObject(msg.getContentObject());
        }catch(Exception e){
          System.out.println("Failed to serialize the GameObjectUpdate: " + e.toString());
        }
        msg_gameObjUp.addReceiver(unit_manager);
        agent.send(msg_gameObjUp);
      }
    } else {
      block();
    }
  }//end action


//  private void sendInitGameObjToAgents(ACLMessage msg){
//	  ACLMessage msg_gameObj = new ACLMessage(ACLMessage.INFORM);
//    msg_gameObj.setConversationId(ConverId.Game.InitGameObj.getConId());
//	  try{
//      msg_gameObj.setContentObject(msg.getContentObject());
//    }catch(Exception e){
//      System.out.println("Failed to serialize the Game object!!!");
//    }
//    for( int i=0; i < init_game_obj_agents.length; i++){
//      msg_gameObj.clearAllReceiver();
//      msg_gameObj.addReceiver(init_game_obj_agents[i]);
//      //System.out.println("Commander Sending Game Object to: "+ init_game_obj_agents[i].getLocalName());
//      agent.send(msg_gameObj);
//    }
//  }
//
//  private void sendGameObjUpToAgents(ACLMessage msg){
//	  ACLMessage msg_gameObj = new ACLMessage(ACLMessage.INFORM);
//    msg_gameObj.setConversationId(ConverId.Game.GameObjUpdate.getConId());
//	  try{
//      msg_gameObj.setContentObject(msg.getContentObject());
//    }catch(Exception e){
//      System.out.println("Failed to serialize the Game object update!!!");
//    }
//    for( int i=0; i < game_obj_update_agents.length; i++){
//      msg_gameObj.clearAllReceiver();
//      msg_gameObj.addReceiver(game_obj_update_agents[i]);
//      //System.out.println("Commander Sending Game Object Update to: "+ game_obj_update_agents[i].getLocalName());
//      agent.send(msg_gameObj);
//    }
//  }
  

}//end CommanderAgentRespInform

