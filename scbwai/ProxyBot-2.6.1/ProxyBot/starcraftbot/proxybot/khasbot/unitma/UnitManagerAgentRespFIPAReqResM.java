/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqResM extends AchieveREResponder {
	UnitManagerAgent agent=null;	
  MessageTemplate mt = null;

  public UnitManagerAgentRespFIPAReqResM(UnitManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
      int unit_id = agent.WorkerAvailable();
      if( unit_id > 0 ) {
        reply.setPerformative(ACLMessage.AGREE);
        try {
          reply.setContentObject(unit_id);
        } catch (IOException ex) {
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        reply.setPerformative(ACLMessage.REFUSE);
      }
    }else{
      System.out.println(agent.getLocalName() + " <<< Unknown request");
    }
    return reply; 
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
      int unit_id = agent.WorkerAvailable();
      if( unit_id > 0 ) {
        reply.setPerformative(ACLMessage.INFORM);
        try {
          reply.setContentObject(unit_id);
        } catch (IOException ex) {
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        reply.setPerformative(ACLMessage.FAILURE);
      }
    }else{
      System.out.println(agent.getLocalName() + " >>> Unknown conversation for workers ");
    }
    return reply;
	}//end prepareResultNotification


}

