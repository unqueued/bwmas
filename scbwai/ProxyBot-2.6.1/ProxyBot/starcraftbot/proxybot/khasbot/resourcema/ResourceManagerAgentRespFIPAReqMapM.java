/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
public class ResourceManagerAgentRespFIPAReqMapM extends AchieveREResponder{

  ResourceManagerAgent agent = null;
  MessageTemplate mt = null;

  public ResourceManagerAgentRespFIPAReqMapM(ResourceManagerAgent a, MessageTemplate mt){
    super(a, mt);
    agent = a;
    this.mt = mt;

  }

  @Override
  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException{
    ACLMessage reply = request.createReply();
    //check the conversation id
    if(request != null && request.getConversationId() != null){
//      System.out.println(agent.getLocalName() + ">  handleRequest > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM " +
//              request.getSender().getLocalName() + " FOR " + request.getConversationId());
      if(request.getConversationId().equals(ConverId.ResM.EnoughResourcesToBuild.getConId())){
        agent.canEnoughResourcesToBuild();
        reply.setPerformative(ACLMessage.AGREE);
      }
    }
    return reply;
  }//end handleRequest

  @Override
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException{
//    System.out.println(agent.getLocalName() + "> prepareResultNotification > " + ACLMessage.getPerformative(request.getPerformative()) + " FROM " +
//            request.getSender().getLocalName() + " FOR " + request.getConversationId());
    ACLMessage inform = request.createReply();
    inform.setPerformative(ACLMessage.INFORM);
    return inform;
  }//end prepareResultNotification
}
