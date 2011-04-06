/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
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
    ACLMessage msg = null;

  public ResourceManagerAgentInitFIPAReqUnitM(ResourceManagerAgent a, ACLMessage msg) {
    super(a,msg);
    this.msg=msg;

    agent=a;
  }

  protected void handleAgree(ACLMessage agree) {
    //System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");

    System.out.println(agent.getLocalName() + " <<< AGREE: from " + agree.getSender());
  }

  /* This is the inform INFORM letting us know that the request was completed */
  protected void handleInform(ACLMessage inform) {
    //System.out.println("ResourceManagerInit "+inform.getSender().getName()+" successfully performed the requested action");
    if(inform.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())) {
      System.out.println(agent.getLocalName() + " <<< INFORM: request for a worker was APPROVED");
      try {
        UnitObject worker = (UnitObject)inform.getContentObject();
        agent.addWorker(worker);
      } catch (UnreadableException ex) {
        Logger.getLogger(ResourceManagerAgentInitFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
      }
    }else
      System.out.println(agent.getLocalName() + " <<< INFORM: unknown conversation");
  }

  protected void handleRefuse(ACLMessage refuse) {
    //System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
    if(refuse.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())) {
      System.out.println(agent.getLocalName() + " <<< REFUSE: request for a worker was DENIED");
    }
    System.out.println(agent.getLocalName() + " <<< REFUSE: unknown conversation from " + refuse.getSender());
  }


  protected void handleFailure(ACLMessage failure) {
      // FAILURE notification from the JADE runtime: the receiver
      // does not exist
      System.out.println(agent.getLocalName() + " <<< FAILURE: from " + failure.getSender());
  }
 
}
