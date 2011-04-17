package starcraftbot.proxybot.khasbot.structurema;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject; 
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class StructureManagerAgentRespInfUnitM extends CyclicBehaviour{
	StructureManagerAgent agent=null;	
  MessageTemplate mt = null;

  public StructureManagerAgentRespInfUnitM(StructureManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
  }

  @Override
  public void action() {
    ACLMessage msg = agent.receive(mt);
    if (msg != null) {
//      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
//        try {
//          agent.setGameObject((GameObject) msg.getContentObject());
//        } catch (UnreadableException ex) {
//          Logger.getLogger(StructureManagerAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
//        try {
//          agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
//        } catch (UnreadableException ex) {
//          Logger.getLogger(StructureManagerAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
    } else {
      block();
    }
  }//end action
  

}//end StructureManagerAgentRespInfCmd

