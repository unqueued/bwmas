/**
 * 
 */
package starcraftbot.proxybot.khasbot.buildingma;

import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class BuildingManagerAgentRespFIPAReqUnitM extends AchieveREResponder {
	BuildingManagerAgent agent=null;
  MessageTemplate mt = null;

  public BuildingManagerAgentRespFIPAReqUnitM(BuildingManagerAgent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;
  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    ACLMessage reply = request.createReply();
    //check the conversation id
    if( request.getConversationId().equals(ConverId.UnitM.NewStructureToBuild.getConId()) ){
      //new structure requested

      //addBehaviour();//Request worker to build it
      //or
      //agent.buildNewStructure(worker,structure_id)

      reply.setPerformative(ACLMessage.AGREE);
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
