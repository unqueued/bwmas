/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.ConverId.UnitM;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentInitFIPAReqUnitM {
	ResourceManagerAgent agent=null;
  AID receiver = null;

  public ResourceManagerAgentInitFIPAReqUnitM(ResourceManagerAgent a, AID receiver) {
    agent=a;
    this.receiver = receiver;
  }

  public void sendRequestFor(ResMRequests request){

    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    UnitM conv = null;

    if(request == ResMRequests.GatherMinerals){
      //set a conversatinon id for this FIPA-Request
      msg.setConversationId(ConverId.UnitM.NeedWorker.getConId());
      conv = ConverId.UnitM.NeedWorker;
    }

    msg.addReceiver(receiver);

    agent.addBehaviour(new FIPARequest(agent,msg,conv));

  }//end sendRequestFor

  

  /**
   * inner class to handle the actual requests.
   */
  class FIPARequest extends AchieveREInitiator {
    ACLMessage msg = null;
    UnitM conv = null;

    public FIPARequest(Agent a, ACLMessage msg, UnitM conv){
      super(a,msg);
      this.msg=msg;
      this.conv = conv;
    }

    /* This is the inform INFORM letting us know that the request was completed */
    protected void handleInform(ACLMessage inform) {
      System.out.println("ResourceManagerInit "+inform.getSender().getName()+" successfully performed the requested action");
    }

    protected void handleRefuse(ACLMessage refuse) {
      System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");

    }

//
//    protected void handleFailure(ACLMessage failure) {
//        // FAILURE notification from the JADE runtime: the receiver
//        // does not exist
//        System.out.println("Responder does not exist");
//    }
  }
  
}
