/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.io.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqStructM extends AchieveREResponder {
	UnitManagerAgent agent=null;	
  MessageTemplate mt = null;

  public UnitManagerAgentRespFIPAReqStructM(UnitManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.StructM.TrainNewUnit.getConId()) ){
      //
      
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

