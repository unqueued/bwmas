/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqMapM extends AchieveREResponder {
	UnitManagerAgent agent=null;	
  MessageTemplate mt = null;

  public UnitManagerAgentRespFIPAReqMapM(UnitManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.SmallUnitGroup.getConId()) ){
      agent.SmallUnitGroup();
      reply.setPerformative(ACLMessage.AGREE);
    }else if( request.getConversationId().equals(ConverId.UnitM.MediumUnitGroup.getConId()) ){
      agent.MediumUnitGroup();
      reply.setPerformative(ACLMessage.AGREE);
    }else if( request.getConversationId().equals(ConverId.UnitM.LargeUnitGroup.getConId()) ){
      agent.LargeUnitGroup();
      reply.setPerformative(ACLMessage.AGREE);
    }
    return reply; 
  }//end handleRequest

  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
    ACLMessage inform = request.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    return inform;
	}//end prepareResultNotification

}
