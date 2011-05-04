/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import starcraftbot.proxybot.ConverId;


@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqResM extends AchieveREResponder{

  UnitManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;

  public UnitManagerAgentRespFIPAReqResM(UnitManagerAgent a, MessageTemplate mt){
    super(a, mt);
    agent = a;
    this.mt = mt;
    ds = agent.getDS();
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException{
    ACLMessage reply = request.createReply();
    if(request != null && request.getConversationId() != null){
      if(request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())){
//        System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
//                + request.getSender().getLocalName() + " FOR " + request.getConversationId());
        UnitObject unit = agent.WorkerAvailable();
        if(unit != null){
          //System.out.println(this.agent.getLocalName() + ":: avaiable worker was NOT null... unit is:" + unit);
          reply.setPerformative(ACLMessage.AGREE);
          reply.setConversationId(ConverId.UnitM.NeedWorker.getConId());
          try{
            reply.setContentObject(unit);
          }catch(IOException ex){
            Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
          }
        }else{
          //System.out.println(this.agent.getLocalName()+":: avaiable worker was null... sending REFUSE");
          reply.setPerformative(ACLMessage.REFUSE);
          reply.setConversationId(ConverId.UnitM.NeedWorker.getConId());
        }
        //this.agent.getDS().put(this.agent.getLocalName() + "agent", this.agent);
      }else{
        System.out.println(agent.getLocalName() + ">  Unknown handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM "
                + request.getSender().getLocalName() + " FOR " + request.getConversationId());
      }
    }
    return reply;
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
    ACLMessage reply = request.createReply();
    if(request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())){
//      System.out.println(agent.getLocalName() + ">  prepareResultNotification > " + ACLMessage.getPerformative(response.getPerformative()) + " FROM "
//        + response.getSender().getLocalName() + " FOR " + response.getConversationId());

      if(response.getPerformative() == ACLMessage.AGREE){
        UnitObject unit_id = null;
        try{
          unit_id = (UnitObject) response.getContentObject();
        }catch(UnreadableException e){
          System.out.println("UnitObject was unreabable" + e.toString());
        }
        assert unit_id == null: "UnitObject being sent in response cannot be null";
//        System.out.println(agent.getLocalName() + " >> sending INFORM with a non-null unit worker to:" + request.getSender().getLocalName());
        reply.setPerformative(ACLMessage.INFORM);
        reply.setConversationId(response.getConversationId());
        try{
          reply.setContentObject(unit_id);
        }catch(IOException ex){
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
        System.out.println(agent.getLocalName() + " >> sending FAILURE due to a null unit worker showing up to:" + request.getSender().getLocalName());
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setConversationId(ConverId.UnitM.NeedWorker.getConId());
      }
    }else{
      System.out.println(agent.getLocalName() + " >>> Unknown conversation for workers ");
    }
    return reply;
  }//end prepareResultNotification
}
