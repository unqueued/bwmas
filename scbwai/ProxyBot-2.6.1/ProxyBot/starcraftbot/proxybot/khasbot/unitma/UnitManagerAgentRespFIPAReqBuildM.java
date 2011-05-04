/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.io.*;
import java.util.Iterator;
import java.util.logging.*;


import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqBuildM extends AchieveREResponder {
	UnitManagerAgent agent=null;	
  MessageTemplate mt = null;
  DataStore ds = null;

  public UnitManagerAgentRespFIPAReqBuildM(UnitManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
    ds = agent.getDS();
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
      if(request.getConversationId().equals(ConverId.BuildM.NeedWorker.getConId())){
//        System.out.println(agent.getLocalName() + " > handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
//          + request.getSender().getLocalName() + " FOR " + request.getConversationId() + " REPLYTO: " +  request.getInReplyTo()
//          + " REPLYWITH: " + request.getReplyWith());


//        System.out.println("UnitM Message receivers: ");
//
//        for(Iterator itr = request.getAllReceiver(); itr.hasNext();){
//          System.out.println("\t receiver: " + itr.next());
//        }
        
        //get a worker from resource manager
        if(agent.retaskWorker()){
          reply.setPerformative(ACLMessage.AGREE);
          System.out.println("UnitManagerAgentRespFIPAReqBuildM: retaskWorker succeeded");
        }else{
          reply.setPerformative(ACLMessage.REFUSE);
          System.out.println("UnitManagerAgentRespFIPAReqBuildM: retaskWorker failed");
        }
      }

    }
    return reply;
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    ACLMessage reply = null;
    if( response == null )
      reply = request.createReply(); 
    else
      reply = response;
    if(request.getConversationId().equals(ConverId.BuildM.NeedWorker.getConId())){
//      System.out.println(agent.getLocalName() + " > prepareResultNotification > " + ACLMessage.getPerformative(response.getPerformative()) + " TO "
//        + response.getSender().getLocalName() + " FOR " + response.getConversationId()+ " REPLYTO: " +  request.getInReplyTo()
//          + " REPLYWITH: " + request.getReplyWith());

      if(response.getPerformative() == ACLMessage.AGREE){
        UnitObject unit = (UnitObject)ds.get("retaskWorker");
        reply.setPerformative(ACLMessage.INFORM);
        reply.setConversationId(request.getConversationId());
        reply.setInReplyTo(response.getReplyWith());
        try{
          reply.setContentObject(unit);
        }catch(IOException ex){
          Logger.getLogger(UnitManagerAgentRespFIPAReqBuildM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        System.out.println(agent.getLocalName() + " >> sending FAILURE due to a null unit worker showing up to:" + request.getSender().getLocalName());
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setConversationId(request.getConversationId());
      }
      
    }
    return reply;
	}//end prepareResultNotification


}

