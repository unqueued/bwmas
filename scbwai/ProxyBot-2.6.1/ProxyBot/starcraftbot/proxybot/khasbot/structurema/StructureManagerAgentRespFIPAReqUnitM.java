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
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.Unit;

@SuppressWarnings("serial")
public class StructureManagerAgentRespFIPAReqUnitM extends AchieveREResponder {
	StructureManagerAgent agent=null;	
  MessageTemplate mt = null;

  public StructureManagerAgentRespFIPAReqUnitM(StructureManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
      if( request.getConversationId().equals(ConverId.StructM.TrainNewUnit.getConId())){
        System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
                + request.getSender().getLocalName() + " FOR " + request.getConversationId());
        Unit unit = null;
        try{
          unit = (Unit) request.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(StructureManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(agent.trainNewUnit(unit))
          reply.setPerformative(ACLMessage.AGREE);
        else
          reply.setPerformative(ACLMessage.REFUSE);

      } else if( request.getConversationId().equals(ConverId.StructM.UpgradeTechLevel.getConId()) ){
        agent.canUpgradeTechLevel();
        reply.setPerformative(ACLMessage.AGREE);
      } else if( request.getConversationId().equals(ConverId.StructM.ResearchTechLevel.getConId()) ){
        agent.canResearchTechLevel();
        reply.setPerformative(ACLMessage.AGREE);
      }
    }
    return reply; 
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
    ACLMessage inform = request.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    return inform;
	}//end prepareResultNotification

}

