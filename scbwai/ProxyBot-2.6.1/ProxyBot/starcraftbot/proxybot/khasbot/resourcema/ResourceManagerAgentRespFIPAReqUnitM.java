/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;


import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.io.IOException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class ResourceManagerAgentRespFIPAReqUnitM extends AchieveREResponder {
	ResourceManagerAgent agent=null;	
  MessageTemplate mt = null;
  DataStore ds = null;

  public ResourceManagerAgentRespFIPAReqUnitM(ResourceManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
    ds = agent.getDS();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
     if(request.getConversationId().equals(ConverId.BuildM.BuildStructure.getConId())){
      System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
                + request.getSender().getLocalName() + " FOR " + request.getConversationId());
      //check the conversation id
      if( request.getConversationId().equals(ConverId.UnitM.RetaskWorker.getConId()) ){
        ArrayList<UnitObject> workerUnits = (ArrayList<UnitObject>)ds.get("my_units");
        
        try{
          reply.setContentObject(workerUnits.remove(0));
        }catch(IOException ex){
          Logger.getLogger(ResourceManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      }
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
