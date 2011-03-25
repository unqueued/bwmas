/**
 * 
 */
package starcraftbot.proxybot.khasbot.commandera;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;

@SuppressWarnings("serial")
public class CommanderAgentResp extends AchieveREResponder{
	Agent agent=null;	

  public CommanderAgentResp(Agent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
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


}
