/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.*;
import java.util.ArrayList;

import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentInfMapM extends CyclicBehaviour{

  ResourceManagerAgent agent = null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public ResourceManagerAgentInfMapM(ResourceManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    mt = in_mt;
    agent = in_a;
    ds = in_ds;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      if(msg.getConversationId().equals(ConverId.MapM.NearestMineralsSuccess.getConId())){
//        System.out.println("Adding a mineral list");
        ArrayList<UnitObject> minerals = null;
        try{
          minerals = (ArrayList<UnitObject>) msg.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInitFIPAReqMapM.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.put("minerals", minerals);
      }else if(msg.getConversationId().equals(ConverId.MapM.NearestMineralsFailure.getConId())){
        System.out.println("NULL mineral list received");
      }
      /*
      else if(msg.getConversationId().equals(ConverId.MapM.NearestMineralsFailure.getConId())){
        ArrayList<UnitObject> gas = null;
        try{
          gas = (ArrayList<UnitObject>) msg.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInitFIPAReqMapM.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.put("gas", gas);
      }else if(msg.getConversationId().equals(ConverId.MapM.NearestMineralsFailure.getConId())){
        ArrayList<UnitObject> gas = null;
        try{
          gas = (ArrayList<UnitObject>) msg.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInitFIPAReqMapM.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.put("gas", gas);
      }
       * 
       */
    }else{
      block();
    }
  }//end action


  /*
  @Override
  protected void handleAgree(ACLMessage agree){
//    System.out.println(agent.getLocalName() + "<  handleAgree < " + ACLMessage.getPerformative(agree.getPerformative()) + " FROM " +
//              agree.getSender().getLocalName() + " FOR " + agree.getConversationId());
  }

  
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
        Logger.getLogger(ResourceManagerAgentInitFIPAReqMapM.class.getName()).log(Level.SEVERE, null, ex);
      }
      ds.put("minerals", minerals);
      ds.put("RequestMinerals", true);
    }else if(inform.getConversationId().equals(ConverId.MapM.NearestGas.getConId())){
      ArrayList<UnitObject> gas = null;
      try{
        gas = (ArrayList<UnitObject>) inform.getContentObject();
      }catch(UnreadableException ex){
        Logger.getLogger(ResourceManagerAgentInitFIPAReqMapM.class.getName()).log(Level.SEVERE, null, ex);
      }
      ds.put("gas", gas);
      ds.put("RequestGas", true);
    }else{
      System.out.println(agent.getLocalName() + " <<< INFORM: unknown conversation: " + inform.getConversationId()
        + " FROM " + inform.getSender().getLocalName());
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
  */
}
