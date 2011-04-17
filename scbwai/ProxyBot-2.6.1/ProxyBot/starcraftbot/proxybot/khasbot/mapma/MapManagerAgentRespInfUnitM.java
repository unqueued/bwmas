package starcraftbot.proxybot.khasbot.mapma;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.game.GameObject;

@SuppressWarnings("serial")
public class MapManagerAgentRespInfUnitM extends CyclicBehaviour{
	MapManagerAgent agent=null;	
  MessageTemplate mt = null;

  public MapManagerAgentRespInfUnitM(MapManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
  }

  @Override
  public void action() {
    ACLMessage msg = agent.receive(mt);
    if (msg != null) {
//      System.out.println(agent.getLocalName() + ">  ??? > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM " +
//        msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
      if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObject.getConId())){
        try {
          GameObject gObj = (GameObject)msg.getContentObject();
          if( gObj != null ){
            agent.parseGameObject(gObj);
          }
        } catch (UnreadableException ex) {
          Logger.getLogger(MapManagerAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } else {
      block();
    } 
  }//end action

}//end MapManagerAgentRespInform
