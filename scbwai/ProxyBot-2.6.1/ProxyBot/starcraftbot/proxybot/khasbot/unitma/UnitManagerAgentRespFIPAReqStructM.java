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

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
      System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
              + request.getSender().getLocalName() + " FOR " + request.getConversationId());
//      if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
//
//      }
    }
    return reply;
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    ACLMessage inform = request.createReply();
    if(request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())){
//      System.out.println(agent.getLocalName() + ">  prepareResultNotification > " + ACLMessage.getPerformative(response.getPerformative()) + " FROM "
//        + response.getSender().getLocalName() + " FOR " + response.getConversationId());
//      if(response.getPerformative() == ACLMessage.AGREE){
//
//      }else{
//
//      }

    }
    return inform;
	}//end prepareResultNotification


}

