/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.DataStore;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentInitFIPAReqMapM extends AchieveREInitiator{

  ResourceManagerAgent agent = null;
  ACLMessage msg = null;
  DataStore ds = null;

  public ResourceManagerAgentInitFIPAReqMapM(ResourceManagerAgent a, ACLMessage msg){
    super(a, msg);
    this.msg = msg;
    agent = a;
    ds = agent.getDS();
  }

  @Override
  protected void handleAgree(ACLMessage agree){
//    System.out.println(agent.getLocalName() + "<  handleAgree < " + ACLMessage.getPerformative(agree.getPerformative()) + " FROM " +
//              agree.getSender().getLocalName() + " FOR " + agree.getConversationId());
  }

  /* This is the inform INFORM letting us know that the request was completed */
  @SuppressWarnings("unchecked")
  @Override
  protected void handleInform(ACLMessage inform){
//      System.out.println(agent.getLocalName() + "<  handleInform < " + ACLMessage.getPerformative(inform.getPerformative()) + " FROM " +
//          inform.getSender().getLocalName() + " FOR " + inform.getConversationId());

    if(inform.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId())){
      ArrayList<UnitObject> minerals = null;
      try{
        minerals = (ArrayList<UnitObject>) inform.getContentObject();
      }catch(UnreadableException ex){
        Logger.getLogger(ResourceManagerAgentInitFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
      }
      ds.put("minerals", minerals);
      ds.put("RequestMinerals", true);
    }else{
      System.out.println(this.agent.getLocalName() + " <<< INFORM: unknown conversation");
    }
  }

  @Override
  protected void handleRefuse(ACLMessage refuse){
//      System.out.println(agent.getLocalName() + "<  handleRefuse < " + ACLMessage.getPerformative(refuse.getPerformative()) + " FROM " +
//          refuse.getSender().getLocalName() + " FOR " + refuse.getConversationId());
    if(refuse.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId())){
      System.out.println(agent.getLocalName() + " <<< REFUSE: request for a " + ConverId.MapM.NearestMinerals.getConId());
      //ask again
      ds.put("RequestMinerals", false);
    }else{
      System.out.println(agent.getLocalName() + " <<< REFUSE: unknown conversation from " + refuse.getSender());
    }
  }

  @Override
  protected void handleFailure(ACLMessage failure){
//      System.out.println(agent.getLocalName() + "<  handleFailure < " + ACLMessage.getPerformative(failure.getPerformative()) + " FROM " +
//          failure.getSender().getLocalName() + " FOR " + failure.getConversationId());
    System.out.println(agent.getLocalName() + " <<< FAILURE: from " + failure.getSender());
  }
}
