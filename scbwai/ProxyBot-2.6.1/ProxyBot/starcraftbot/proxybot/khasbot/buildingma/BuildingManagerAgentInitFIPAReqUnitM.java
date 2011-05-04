/**
 * 
 */
package starcraftbot.proxybot.khasbot.buildingma;

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
public class BuildingManagerAgentInitFIPAReqUnitM extends AchieveREInitiator{

  BuildingManagerAgent agent = null;
  ACLMessage msg = null;
  DataStore ds = null;

  public BuildingManagerAgentInitFIPAReqUnitM(BuildingManagerAgent a, ACLMessage msg){
    super(a, msg);
    this.msg = msg;
    agent = a;
    ds = agent.getDS();
  }

  @Override
  protected void handleAgree(ACLMessage agree){
//    System.out.println(agent.getLocalName() + "<  handleAgree < " + ACLMessage.getPerformative(agree.getPerformative()) + " FROM " +
//      agree.getSender().getLocalName() + " FOR " + agree.getConversationId());
  }

  /* This is the inform INFORM letting us know that the request was completed */
  @SuppressWarnings("unchecked")
  @Override
  protected void handleInform(ACLMessage inform){
//    System.out.println(agent.getLocalName() + " < handleInform < " + ACLMessage.getPerformative(inform.getPerformative()) + " FROM " +
//      inform.getSender().getLocalName() + " FOR " + inform.getConversationId());
    if(inform.getConversationId().equals(ConverId.BuildM.NeedWorker.getConId())){
      UnitObject unit = null;
      try{
        unit = (UnitObject)inform.getContentObject();
      }catch(UnreadableException ex){
        Logger.getLogger(BuildingManagerAgentInitFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
      }
      System.out.println("Unit received was: " + unit.getType().toString() + " ID: " + unit.getID());
      ds.put("worker",unit);
      ds.put("workerRequested",true);
    }else{
      System.out.println(agent.getLocalName() + " <<< INFORM: unknown conversation " + inform.getConversationId() + " from " + inform.getSender());
    }
  }

  @Override
  protected void handleRefuse(ACLMessage refuse){
//      System.out.println(agent.getLocalName() + " < handleRefuse < " + ACLMessage.getPerformative(refuse.getPerformative()) + " FROM " +
//          refuse.getSender().getLocalName() + " FOR " + refuse.getConversationId());
    if(refuse.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId())){
      System.out.println(agent.getLocalName() + " <<< REFUSE: request for a " + ConverId.MapM.NearestMinerals.getConId());
      //ask again

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
