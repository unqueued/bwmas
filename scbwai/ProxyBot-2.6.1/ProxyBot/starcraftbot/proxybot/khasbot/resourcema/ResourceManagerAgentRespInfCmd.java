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
  DataStore ds = null;
  MessageTemplate mt = null;

  public ResourceManagerAgentRespInfCmd(ResourceManagerAgent a, MessageTemplate mt) {
    super(a);
    this.agent=a;
    this.mt=mt;
    this.ds = this.agent.getDS();
  }

  public void action() {
    //System.out.println("ResourceManagerAgentRespInfCmd: " + agent.tbf.getThread(this).getId());
    //System.out.println("DataStore hello: " + getDataStore().get("hello"));
	this.agent = (ResourceManagerAgent)this.agent.getDS().get(this.agent.getLocalName()+"agent");
    ACLMessage msg = this.agent.receive(mt);
    //System.out.println(agent.getLocalName() + " <<< INFORM: from commander");
    if (msg != null) {
      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
        try {
          this.agent.setGameObject((GameObject) msg.getContentObject());
        } catch (UnreadableException ex) {
          Logger.getLogger(ResourceManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
        try {
          this.agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
        } catch (UnreadableException ex) {
          Logger.getLogger(ResourceManagerAgentRespInfCmd.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      this.agent.getDS().put(this.agent.getLocalName()+"agent", this.agent);
    } else {
      block();
    }
  }//end action

}//end ResourceManagerAgentRespInfCmd
