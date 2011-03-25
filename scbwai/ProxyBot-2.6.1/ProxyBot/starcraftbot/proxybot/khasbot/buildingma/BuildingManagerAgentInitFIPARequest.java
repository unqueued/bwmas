/**
 * 
 */
package starcraftbot.proxybot.khasbot.buildingma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.util.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;
import starcraftbot.proxybot.CommID;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class BuildingManagerAgentInitFIPARequest {
	Agent agent=null;

  public BuildingManagerAgentInitFIPARequest(Agent a, String[] receivers) {
    agent=a;

    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

    //set a conversatinon id for this FIPA-Request
    msg.setConversationId(CommID.genID(a));
    
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
  /*
  protected void handleAllResultNotifications(Vector notifications) {
      // Some responder didn't reply within the specified timeout
      System.out.println("Timeout expired: missing " + notifications.size() + " responses");
  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    System.out.println("MSG_H: " + agent.getLocalName() + ": REQUEST RX from " + request.getSender().getLocalName() 
        + " Action: " + request.getContent());
    if(ParseACLMessage.isSenderProxyBot(request)) {
      //Messages from ProxyBot have to be treated different, since what we get from it are updates from the
      //game. This game object will then be sent to all the other agents 

      // We agree to perform the action. Note that in the FIPA-Request
      // protocol the AGREE message is optional. Return null if you
      // don't want to send it.
      //System.out.println("Sender is ProxyBotAgent");
      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.AGREE);
      return agree;
    } else if(ParseACLMessage.isSenderUnitManager(request)) {
      //System.out.println("Sender is UnitManagerAgent");
  
      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.AGREE);
      return agree;
    } else {
      System.out.println("Sender is not recognized " + request.getSender().getLocalName());
      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.REFUSE);
      return agree;
    }

  }//end handleRequest

  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    //if (performAction()) {
      System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
      ACLMessage inform = request.createReply();
      inform.setPerformative(ACLMessage.INFORM);
      return inform;
    //} else {
    //  System.out.println("Agent " + agent.getLocalName() + ": Action failed");
    //  throw new FailureException("unexpected-error");
    //}	
	}//end prepareResultNotification
  */

}
