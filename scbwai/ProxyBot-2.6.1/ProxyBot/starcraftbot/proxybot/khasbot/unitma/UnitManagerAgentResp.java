/**
 * 
 * Agent Description:
 * 
 * Associated Agents (Controlling Agent):
 * 	@see UnitManagerAgent 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

@SuppressWarnings("serial")
public class UnitManagerAgentResp extends AchieveREResponder{
		
	AID commander = new AID("Commander",AID.ISLOCALNAME);
	AID unit_manager = new AID("KhasUnitManager",AID.ISLOCALNAME);
	AID building_manager = new AID("KhasBuildingManager",AID.ISLOCALNAME);
	
  public UnitManagerAgentResp(Agent a, MessageTemplate mt) {
    super(a, mt);
  }

  protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
    System.out.println("*** " + myAgent.getLocalName() + ": REQUEST received from "+request.getSender().getName()+
        ". Action is "+request.getContent());
    //if (checkAction()) {
      // We agree to perform the action. Note that in the FIPA-Request
      // protocol the AGREE message is optional. Return null if you
      // don't want to send it.
      System.out.println("*** " + myAgent.getLocalName() + ": Agree");
      ACLMessage agree = request.createReply();
      agree.setPerformative(ACLMessage.AGREE);
      return agree;
    //}else{
    
      // We refuse to perform the action
    //	System.out.println("Agent "+getLocalName()+": Refuse");
  //		throw new RefuseException("check-failed");
  //	}
  }
  
  protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
    //if( performAction() ) {
      System.out.println("*** " + myAgent.getLocalName() + ": Action successfully performed");
      ACLMessage inform = request.createReply();
      inform.setPerformative(ACLMessage.INFORM);
      return inform;
    //}else{
  //		System.out.println("Agent "+getLocalName()+": Action failed");
  //		throw new FailureException("unexpected-error");
  //	}		
  }//end prepareResultNotification

}//class UnitManagerAgentResp

