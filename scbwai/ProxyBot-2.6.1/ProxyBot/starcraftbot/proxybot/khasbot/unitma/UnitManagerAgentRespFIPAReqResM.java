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
import starcraftbot.proxybot.command.GameCommandQueue;

@SuppressWarnings("serial")
public class UnitManagerAgentRespFIPAReqResM extends AchieveREResponder {
	UnitManagerAgent agent=null;
	DataStore ds = null;
  MessageTemplate mt = null;

  public UnitManagerAgentRespFIPAReqResM(UnitManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    this.agent=a;
    this.mt=mt;
    this.ds = this.agent.getDS();
  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    this.agent = (UnitManagerAgent)this.agent.getDS().get(this.agent.getLocalName()+"agent");
    if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
      UnitObject unit = this.agent.WorkerAvailable();
      if( unit != null ) {
    	System.out.println(this.agent.getLocalName()+":: avaiable worker was NOT null... unit is:"+ unit);  
        reply.setPerformative(ACLMessage.AGREE);
        try {
          reply.setContentObject(unit);
        } catch (IOException ex) {
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
      	//System.out.println(this.agent.getLocalName()+":: avaiable worker was null... sending REFUSE");   
        reply.setPerformative(ACLMessage.REFUSE);
      }
      this.agent.getDS().put(this.agent.getLocalName()+"agent", this.agent);
    }
    else if(request.getConversationId().equals(ConverId.UnitM.NewCommands.getConId()))
    {
    	try {
			this.agent.addCommands((GameCommandQueue)request.getContentObject());
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      this.ds.put(this.agent.getLocalName()+"agent", this.agent);
    }
    else{
      System.out.println(this.agent.getLocalName() + " <<< Unknown request");
    }
    return reply; 
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId()) ){
      UnitObject unit_id = null;
	try {
		unit_id = (UnitObject)response.getContentObject();
	} catch (UnreadableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//agent.WorkerAvailable();
      if( unit_id != null) {
        System.out.println(agent.getLocalName()+" >> sending INFORM with a non-null unit worker to:"+ request.getSender());
    	reply.setPerformative(ACLMessage.INFORM);
        try {
          reply.setContentObject(unit_id);
        } catch (IOException ex) {
          Logger.getLogger(UnitManagerAgentRespFIPAReqResM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else{
    	System.out.println(agent.getLocalName()+" >> sending FAILURE due to a null unit worker showing up to:" +request.getSender());  
        reply.setPerformative(ACLMessage.FAILURE);
      }
    }
    else if(request.getConversationId().equals(ConverId.UnitM.NewCommands.getConId()))
    {
    	//only sending here because Resource is only one w/ commands, however need to add others THEN this will be called.
        this.agent = (UnitManagerAgent)this.ds.get(this.agent.getLocalName()+"agent");
    	this.agent.processCmdsReceived();
    	reply.setPerformative(ACLMessage.INFORM);
        this.ds.put(this.agent.getLocalName()+"agent", this.agent);
    }
    else{
      System.out.println(this.agent.getLocalName() + " >>> Unknown conversation for workers ");
    }
    return reply;
	}//end prepareResultNotification


}

