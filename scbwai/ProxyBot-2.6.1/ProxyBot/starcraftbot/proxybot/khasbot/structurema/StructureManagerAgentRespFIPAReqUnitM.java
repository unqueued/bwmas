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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.Unit;

@SuppressWarnings("serial")
public class StructureManagerAgentRespFIPAReqUnitM extends AchieveREResponder{

  StructureManagerAgent agent = null;
  MessageTemplate mt = null;

  public StructureManagerAgentRespFIPAReqUnitM(StructureManagerAgent a, MessageTemplate mt){
    super(a, mt);
    agent = a;
    this.mt = mt;
    
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException{
    ACLMessage reply = request.createReply();
    //while(agent.receive() != null){}
    if(request != null && request.getConversationId() != null){
      if(request.getConversationId().equals(ConverId.StructM.TrainNewUnit.getConId())){
        System.out.println(agent.getLocalName() + " > handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
          + request.getSender().getLocalName() + " FOR " + request.getConversationId() + " REPLYTO: " +  request.getInReplyTo()
          + " REPLYWITH: " + request.getReplyWith());
/*
        System.out.println("StructM Message> ConversationID: " + request.getConversationId());
        System.out.println("StructM Message> replyWith: " + request.getReplyWith());
        System.out.println("StructM Message> sender: " + request.getSender());

        System.out.println("StructM Message replyTo: ");

        for(Iterator itr = request.getAllReplyTo(); itr.hasNext();){
          System.out.println("\t replyTo: " + itr.next());
        }

        System.out.println("StructM Message receivers: ");

        for(Iterator itr = request.getAllReceiver(); itr.hasNext();){
          System.out.println("\t receiver: " + itr.next());
        }
*/
        Unit unit = null;
        try{
          unit = (Unit) request.getContentObject();
          //System.out.println("StructM Message ContentObject:" + unit.toString());
        }catch(UnreadableException ex){
          Logger.getLogger(StructureManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        reply.setInReplyTo(request.getReplyWith());

        if(agent.canTrainNewUnit(unit)){
          reply.setPerformative(ACLMessage.AGREE);
        }else{
          reply.setPerformative(ACLMessage.REFUSE);
        }

      }
//      else if(request.getConversationId().equals(ConverId.StructM.UpgradeTechLevel.getConId())){
//        agent.canUpgradeTechLevel();
//        reply.setPerformative(ACLMessage.AGREE);
//      }else if(request.getConversationId().equals(ConverId.StructM.ResearchTechLevel.getConId())){
//        agent.canResearchTechLevel();
//        reply.setPerformative(ACLMessage.AGREE);
//      }
    }
    return reply;
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
    ACLMessage inform = null;
    if(response == null)
      inform = request.createReply();
    else
      inform = response;

    if(response.getConversationId().equals(ConverId.StructM.TrainNewUnit.getConId())){
      System.out.println(agent.getLocalName() + " > prepareResultNotification > " + ACLMessage.getPerformative(inform.getPerformative()) + " TO "
        + inform.getAllReceiver().next().toString() + " FOR " + inform.getConversationId()+ " REPLYTO: " +  inform.getInReplyTo()
        + " REPLYWITH: " + inform.getReplyWith());

      inform.setInReplyTo(inform.getReplyWith());
      if(inform.getPerformative() == ACLMessage.AGREE){
        Unit unit = null;

        try{
          unit = (Unit) request.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(StructureManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(agent.trainNewUnit(unit)){
          inform.setPerformative(ACLMessage.INFORM);
        }else{
          inform.setPerformative(ACLMessage.FAILURE);
        }
      }else{
        inform.setPerformative(ACLMessage.FAILURE);
      }
      //request.reset();
    }

    return inform;
  }//end prepareResultNotification
}
