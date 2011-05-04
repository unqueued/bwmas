/**
 * 
 */
package starcraftbot.proxybot.khasbot.buildingma;


import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class BuildingManagerAgentRespFIPAReqUnitM extends AchieveREResponder {
	BuildingManagerAgent agent=null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public BuildingManagerAgentRespFIPAReqUnitM(BuildingManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
    ds = agent.getDS();
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
      if(request.getConversationId().equals(ConverId.BuildM.BuildStructure.getConId())){
//        System.out.println(agent.getLocalName() + " > handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
//          + request.getSender().getLocalName() + " FOR " + request.getConversationId());

        //first check to see if that the message for the building is not null
        Unit struct = null;
        try{
          struct = (Unit) request.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(BuildingManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }

        if( struct == null )
          reply.setPerformative(ACLMessage.REFUSE);
        else{
          // no null request was found, so now check to see if we have a worker available
          UnitObject worker = (UnitObject)ds.get("worker");
          if(worker == null){
            // request a worker from the UnitManager
            agent.requestWorker();
            while(ds.get("workerRequested") == null)
              ;
          }

          //check to see if our request was successful
          if(ds.get("worker") != null){
            reply.setPerformative(ACLMessage.AGREE);
            System.out.println(BuildingManagerAgentRespFIPAReqUnitM.class.getName() + ": Request for worker succeeded");
          }else{
            reply.setPerformative(ACLMessage.REFUSE);
            System.out.println(BuildingManagerAgentRespFIPAReqUnitM.class.getName() + ": Request for worker failed");
          }

          System.out.println("Request for building: " + struct);

        }
      }
    }
    return reply; 
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    ACLMessage inform = request.createReply();
//    if(request.getConversationId().equals(ConverId.BuildM.BuildStructure.getConId())){
//      System.out.println(agent.getLocalName() + " >  prepareResultNotification > " + ACLMessage.getPerformative(response.getPerformative()) + " TO "
//        + response.getSender().getLocalName() + " FOR " + response.getConversationId());
//      if(response.getPerformative() == ACLMessage.AGREE){
//        Unit struct = null;
//        try{
//          struct = (Unit) request.getContentObject();
//        }catch(UnreadableException ex){
//          Logger.getLogger(BuildingManagerAgentRespFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if(agent.buildNewStructure(struct))
//          inform.setPerformative(ACLMessage.INFORM);
//        else
//          inform.setPerformative(ACLMessage.FAILURE);
//
//      }else{
//        inform.setPerformative(ACLMessage.FAILURE);
//      }
//    }
    return inform;
	}//end prepareResultNotification


}
