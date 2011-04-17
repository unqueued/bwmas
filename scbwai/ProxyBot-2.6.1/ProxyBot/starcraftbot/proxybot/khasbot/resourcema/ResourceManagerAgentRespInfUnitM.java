package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class ResourceManagerAgentRespInfUnitM extends CyclicBehaviour{

  ResourceManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;

  public ResourceManagerAgentRespInfUnitM(ResourceManagerAgent a, MessageTemplate mt){
    super(a);
    this.agent = a;
    this.mt = mt;
    ds = agent.getDS();
  }

  @Override
  public void action(){
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
          Logger.getLogger(ResourceManagerAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } else {
      block();
    }
  }//end action
}//end ResourceManagerAgentRespInfCmd

