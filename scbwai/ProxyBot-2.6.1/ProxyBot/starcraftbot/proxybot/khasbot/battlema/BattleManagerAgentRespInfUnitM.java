package starcraftbot.proxybot.khasbot.battlema;

import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;

/**
 * This class/behaviour is responsible for handling all the GameObj
 * and GameObjUpdate from Commander.
 */
@SuppressWarnings("serial")
public class BattleManagerAgentRespInfUnitM extends CyclicBehaviour{
	BattleManagerAgent agent=null;	
  MessageTemplate mt = null;

  public BattleManagerAgentRespInfUnitM(BattleManagerAgent a, MessageTemplate mt) {
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
//          Logger.getLogger(BattleManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
//        try {
//          agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
//        } catch (UnreadableException ex) {
//          Logger.getLogger(BattleManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
    } else {
      block();
    }

  }//end action

}//end BattleManagerAgentRespInform

