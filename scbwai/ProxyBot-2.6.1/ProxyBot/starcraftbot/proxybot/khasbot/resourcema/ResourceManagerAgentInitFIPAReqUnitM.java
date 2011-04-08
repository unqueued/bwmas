/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.ConverId.UnitM;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentInitFIPAReqUnitM extends AchieveREInitiator{
	ResourceManagerAgent agent=null;
    DataStore ds = null;
	ACLMessage msg = null;

  public ResourceManagerAgentInitFIPAReqUnitM(ResourceManagerAgent a, ACLMessage msg) {
    super(a,msg);
    this.msg=msg;
    this.agent=a;
    ds = this.agent.getDS();
    
  }

  protected void handleAgree(ACLMessage agree) {
    //System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");

    System.out.println(agent.getLocalName() + " <<< AGREE: from " + agree.getSender());
  }

  /* This is the inform INFORM letting us know that the request was completed */
  protected void handleInform(ACLMessage inform) {
    //System.out.println("ResourceManagerInit "+inform.getSender().getName()+" successfully performed the requested action");
	this.agent = (ResourceManagerAgent)this.agent.getDS().get(this.agent.getLocalName()+"agent");
	if(inform.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())) {
      System.out.println(this.agent.getLocalName() + " <<< INFORM: request for a worker was APPROVED");
      try {
        UnitObject worker = (UnitObject)inform.getContentObject();
        this.agent.addWorker(worker);
      } catch (UnreadableException ex) {
        Logger.getLogger(ResourceManagerAgentInitFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.agent.getDS().put(this.agent.getLocalName()+"agent", this.agent);
    }else
      System.out.println(this.agent.getLocalName() + " <<< INFORM: unknown conversation");
  }

  protected void handleRefuse(ACLMessage refuse) {
    //System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
    if(refuse.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())) {
      System.out.println(agent.getLocalName() + " <<< REFUSE: request for a worker was DENIED");
    }
    else
    	System.out.println(agent.getLocalName() + " <<< REFUSE: unknown conversation from " + refuse.getSender());
  }


  protected void handleFailure(ACLMessage failure) {
      // FAILURE notification from the JADE runtime: the receiver
      // does not exist
      System.out.println(agent.getLocalName() + " <<< FAILURE: from " + failure.getSender());
  }
 
}
