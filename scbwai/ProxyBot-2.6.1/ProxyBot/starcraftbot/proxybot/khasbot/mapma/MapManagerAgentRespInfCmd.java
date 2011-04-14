package starcraftbot.proxybot.khasbot.mapma;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class MapManagerAgentRespInfCmd extends CyclicBehaviour{
	MapManagerAgent agent=null;	
  MessageTemplate mt = null;

  public MapManagerAgentRespInfCmd(MapManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
  }

  public void action() {
    ACLMessage msg = agent.receive(mt);
    if (msg != null) {
      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
        try {
          agent.setGameObject((GameObject) msg.getContentObject());
        } catch (UnreadableException ex) {
          Logger.getLogger(MapManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } else {
      block();
    } 
  }//end action

}//end MapManagerAgentRespInform
