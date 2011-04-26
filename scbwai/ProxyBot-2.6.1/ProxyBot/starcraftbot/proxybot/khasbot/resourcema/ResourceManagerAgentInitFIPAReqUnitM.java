/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.core.behaviours.DataStore;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.ConverId.UnitM;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentInitFIPAReqUnitM extends AchieveREInitiator{

  ResourceManagerAgent agent = null;
  DataStore ds = null;
  ACLMessage msg = null;

  public ResourceManagerAgentInitFIPAReqUnitM(ResourceManagerAgent a, ACLMessage msg){
    super(a, msg);
    this.msg = msg;
    this.agent = a;
    ds = agent.getDS();

  }

  @Override
  protected void handleAgree(ACLMessage agree){
//    System.out.println(agent.getLocalName() + "<  handleAgree < " + ACLMessage.getPerformative(agree.getPerformative()) + " FROM "
//            + agree.getSender().getLocalName() + " FOR " + agree.getConversationId());
  }

  /* This is the inform INFORM letting us know that the request was completed */
  @Override
  protected void handleInform(ACLMessage inform){
//      System.out.println(agent.getLocalName() + "<  handleInform < " + ACLMessage.getPerformative(inform.getPerformative()) + " FROM " +
//          inform.getSender().getLocalName() + " FOR " + inform.getConversationId());
    if(inform.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())){
      try{
        UnitObject worker = (UnitObject) inform.getContentObject();
        agent.addWorker(worker);
        //set this to false, so that I can ask again
        agent.gather_minerals.setRequestWorker(false);
      }catch(UnreadableException ex){
        Logger.getLogger(ResourceManagerAgentInitFIPAReqUnitM.class.getName()).log(Level.SEVERE, null, ex);
      }
    }else{
      System.out.println(agent.getLocalName() + " <<< INFORM: unknown conversation " + inform.getConversationId() + " from " + inform.getSender());
    }
  }

  @Override
  protected void handleRefuse(ACLMessage refuse){
//      System.out.println(agent.getLocalName() + "<  handleRefuse < " + ACLMessage.getPerformative(refuse.getPerformative()) + " FROM " +
//          refuse.getSender().getLocalName() + " FOR " + refuse.getConversationId());
    if(refuse.getConversationId().equals(ConverId.UnitM.NeedWorker.getConId())){
      //set this to false, so that I can ask again
      agent.gather_minerals.setRequestWorker(false);
    }else{
      System.out.println(agent.getLocalName() + " <<< REFUSE: unknown conversation from " + refuse.getSender());
    }
  }

  @Override
  protected void handleFailure(ACLMessage failure){
      System.out.println(agent.getLocalName() + "<  handleFailure < " + ACLMessage.getPerformative(failure.getPerformative()) + " FROM " +
          failure.getSender().getLocalName() + " FOR " + failure.getConversationId());
      agent.gather_minerals.setRequestWorker(false);
  }
}
