/**
 * 
 */
package starcraftbot.proxybot.khasbot.battlema;

import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

/**
 * This class/behaviour will handle requests from the commander.
 */
@SuppressWarnings("serial")
public class BattleManagerAgentRespFIPAReqCmd extends AchieveREResponder {
	BattleManagerAgent agent=null;	
  MessageTemplate mt = null;

  public BattleManagerAgentRespFIPAReqCmd(BattleManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.Commander.EndGamePhaseOne.getConId()) ){
      agent.endPhaseOne();
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
