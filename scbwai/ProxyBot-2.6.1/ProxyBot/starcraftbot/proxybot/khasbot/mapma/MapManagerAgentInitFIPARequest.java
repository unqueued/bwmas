/**
 * 
 */
package starcraftbot.proxybot.khasbot.mapma;

import jade.core.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;


import starcraftbot.proxybot.ConverId;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class MapManagerAgentInitFIPARequest {
	Agent agent=null;

  public MapManagerAgentInitFIPARequest(Agent a, String[] receivers) {
    agent=a;

    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

    //set a conversatinon id for this FIPA-Request
    //msg.setConversationId(CommID.genID(a));
    
    for(int i=0; i < receivers.length; i++)
      msg.addReceiver(new AID((String) receivers[i], AID.ISLOCALNAME));
     
    agent.addBehaviour(new FIPARequest(agent,msg));
  }

  /**
   * inner class to handle the actual requests.
   */
  class FIPARequest extends AchieveREInitiator {
    ACLMessage msg = null;

    public FIPARequest(Agent a, ACLMessage msg){
      super(a,msg);
      this.msg=msg;
    }
    protected void handleInform(ACLMessage inform) {
      System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
    }

    protected void handleRefuse(ACLMessage refuse) {
      System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
    }

    protected void handleFailure(ACLMessage failure) {
        // FAILURE notification from the JADE runtime: the receiver
        // does not exist
        System.out.println("Responder does not exist");
    }
  }

}
