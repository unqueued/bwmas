/**
 * 
 */
package starcraftbot.proxybot.khasbot.commandera;

import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;


@SuppressWarnings("serial")
public class CommanderAgentRespFIPARequest extends AchieveREResponder {
	Agent agent=null;	
  MessageTemplate mt = null;

  public CommanderAgentRespFIPARequest(Agent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    //System.out.println("MSG_H: " + agent.getLocalName() + ": REQUEST RX from " + request.getSender().getLocalName() + " Action: " + request.getContent());
    ACLMessage agree = request.createReply();
    agree.setPerformative(ACLMessage.AGREE);
    return agree;

   

  }//end handleRequest

  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
    ACLMessage inform = request.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    return inform;
	}//end prepareResultNotification


}
