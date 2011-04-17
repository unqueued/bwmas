/**
 * 
 */
package starcraftbot.proxybot.khasbot.mapma;

import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class MapManagerAgentRespFIPAReqResM extends AchieveREResponder {
	MapManagerAgent agent=null;
  MessageTemplate mt = null;

  public MapManagerAgentRespFIPAReqResM(MapManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request != null && request.getConversationId() != null){
//      System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM " +
//          request.getSender().getLocalName() + " FOR " + request.getConversationId());
      if( request.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId()) ){
        if( agent.NearestMinerals() != null ){
          reply.setPerformative(ACLMessage.AGREE);
        }else{
          reply.setPerformative(ACLMessage.REFUSE);
        }
      }else if( request.getConversationId().equals(ConverId.MapM.NearestGas.getConId()) ){
        if(agent.NearestGas() != null ){
          reply.setPerformative(ACLMessage.AGREE);
        }else{
          reply.setPerformative(ACLMessage.REFUSE);
        }
      }
    }
    return reply;  

  }//end handleRequest

  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    //System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
    ACLMessage inform = request.createReply();
    inform.setPerformative(ACLMessage.INFORM);
//    System.out.println(agent.getLocalName() + "> prepareResultNotification > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM " +
//        request.getSender().getLocalName() + " FOR " + request.getConversationId());
    if( request.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId()) ){
      if( agent.NearestMinerals() != null ){
        try{
          inform.setContentObject(agent.NearestMinerals());
          inform.setPerformative(ACLMessage.INFORM);
        }catch(IOException ex){
          Logger.getLogger(MapManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        inform.setPerformative(ACLMessage.FAILURE);
      }
    }else if( request.getConversationId().equals(ConverId.MapM.NearestGas.getConId()) ){
      if(agent.NearestGas() != null ){
        try{
          inform.setContentObject(agent.NearestGas());
          inform.setPerformative(ACLMessage.INFORM);
        }catch(IOException ex){
          Logger.getLogger(MapManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        inform.setPerformative(ACLMessage.FAILURE);
      }
    }
    return inform;
	}//end prepareResultNotification

}

