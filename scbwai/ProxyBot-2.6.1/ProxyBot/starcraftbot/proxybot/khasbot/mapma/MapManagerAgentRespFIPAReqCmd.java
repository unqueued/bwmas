/**
 * 
 */
package starcraftbot.proxybot.khasbot.mapma;

import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class MapManagerAgentRespFIPAReqCmd extends AchieveREResponder {
	MapManagerAgent agent=null;
  MessageTemplate mt = null;

  public MapManagerAgentRespFIPAReqCmd(MapManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.Commander.EndGamePhaseOne.getConId()) ){
      agent.EndGamePhaseOne();
      reply.setPerformative(ACLMessage.AGREE);
    }else if( request.getConversationId().equals(ConverId.Commander.StartGamePhaseTwo.getConId()) ){
      agent.StartGamePhaseTwo();
      reply.setPerformative(ACLMessage.AGREE);
    }else if( request.getConversationId().equals(ConverId.Commander.EndGamePhaseTwo.getConId()) ){
      agent.EndGamePhaseTwo();
      reply.setPerformative(ACLMessage.AGREE);
    }else if( request.getConversationId().equals(ConverId.Commander.StartGamePhaseThree.getConId()) ){
      agent.StartGamePhaseThree();
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

