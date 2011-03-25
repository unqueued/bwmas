/**
 * 
 */
package starcraftbot.proxybot.khasbot.buildingma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;

@SuppressWarnings("serial")
public class BuildingManagerAgentRespFIPARequest extends AchieveREResponder {
	Agent agent=null;	
  MessageTemplate mt = null;

  public BuildingManagerAgentRespFIPARequest(Agent a, MessageTemplate mt) {
    super(a, mt);
    agent=a;
    this.mt=mt;

  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    System.out.println("MSG_H: " + agent.getLocalName() + ": REQUEST RX from " + request.getSender().getLocalName() + " Action: " + request.getContent());
    if(ParseACLMessage.isSenderUnitManager(request)) {
      
      //
      // Here we have to determine what it is that the request is asking
      //

      //TODO: insert code to parse out what the UnitManager wants us to do. May have to define ontologies
      //still not sure

      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.AGREE);
      return agree;

    } else if(ParseACLMessage.isSenderMapManager(request)) {
      
      //
      // Here we have to determine what it is that the request is asking
      //

      //TODO: insert code to parse out what the MapManager wants us to do. May have to define ontologies
      //still not sure

      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.AGREE);
      return agree;
    } else {
      System.out.println("Sender is not recognized " + request.getSender().getLocalName());
      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.REFUSE);
      return agree;
    }

  }//end handleRequest

  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    //if (performAction()) {
      System.out.println("Agent "+ agent.getLocalName() + ": Action successfully performed");
      ACLMessage inform = request.createReply();
      inform.setPerformative(ACLMessage.INFORM);
      return inform;
    //} else {
    //  System.out.println("Agent " + agent.getLocalName() + ": Action failed");
    //  throw new FailureException("unexpected-error");
    //}	
	}//end prepareResultNotification


}
