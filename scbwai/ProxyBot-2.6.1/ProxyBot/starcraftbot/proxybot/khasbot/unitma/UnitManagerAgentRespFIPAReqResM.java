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

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
      UnitObject unit = agent.WorkerAvailable();
      if( unit != null ) {
        reply.setPerformative(ACLMessage.INFORM);
        try {
          reply.setContentObject(unit);
        } catch (IOException ex) {
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        reply.setPerformative(ACLMessage.REFUSE);
      }
    }
    return reply; 
  }//end handleRequest

//  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
//    System.out.println(agent.getLocalName() + ": Action successfully performed");
//    ACLMessage inform = request.createReply();
//    inform.setPerformative(ACLMessage.INFORM);
//    return inform;
//	}//end prepareResultNotification


}

