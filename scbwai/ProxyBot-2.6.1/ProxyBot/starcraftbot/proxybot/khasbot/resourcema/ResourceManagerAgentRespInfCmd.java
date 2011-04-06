package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class ResourceManagerAgentRespInfCmd extends CyclicBehaviour{
	ResourceManagerAgent agent=null;	
  MessageTemplate mt = null;

  public ResourceManagerAgentRespInfCmd(ResourceManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
    
  }

  public void action() {
    //System.out.println("ResourceManagerAgentRespInfCmd: " + agent.tbf.getThread(this).getId());
    //System.out.println("DataStore hello: " + getDataStore().get("hello"));
    ACLMessage msg = agent.receive(mt);
    //System.out.println(agent.getLocalName() + " <<< INFORM: from commander");
    if (msg != null) {
      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
        try {
          agent.setGameObject((GameObject) msg.getContentObject());
        } catch (UnreadableException ex) {
          Logger.getLogger(ResourceManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
        try {
          agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
        } catch (UnreadableException ex) {
          Logger.getLogger(ResourceManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      
    } else {
      block();
    }
  }//end action

}//end MapManagerAgentRespInform
