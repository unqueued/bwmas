/**
 * 
 */
package starcraftbot.proxybot.khasbot.structurema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class StructureManagerAgentRespFIPAReqUnitM extends AchieveREResponder {
	StructureManagerAgent agent=null;	
  MessageTemplate mt = null;

  public StructureManagerAgentRespFIPAReqUnitM(StructureManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.StructM.BuildNewUnit.getConId()) ){
      agent.canBuildNewUnit();
      reply.setPerformative(ACLMessage.AGREE);
    } else if( request.getConversationId().equals(ConverId.StructM.UpgradeTechLevel.getConId()) ){
      agent.canUpgradeTechLevel();
      reply.setPerformative(ACLMessage.AGREE);
    } else if( request.getConversationId().equals(ConverId.StructM.ResearchTechLevel.getConId()) ){
      agent.canResearchTechLevel();
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

