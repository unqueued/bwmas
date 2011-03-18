/**
 * Incorporates Construction Manager and Building Placer from "bwsal".
 * 
 * Agent Description:
 * 	Controls units to build buildings, and communicates with MapManagerAgent to place possible 
 * 	future buildings known from Commander
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 * 	 -> StructureManagerAgent
 *  <-> UnitManagerAgent
 *  <-  MapManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 * 	@see StructureManagerAgent
 *  @see UnitManagerAgent
 *  @see MapManagerAgent
 * 
 */

package starcraftbot.khasbot.buildingma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;


@SuppressWarnings("serial")
public class BuildingManagerAgent extends Agent {
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();

	AID commander = new AID("Commander",AID.ISLOCALNAME);
	AID structure_manager = new AID("KhasStructureManager",AID.ISLOCALNAME);
	AID unit_manager = new AID("UnitManager",AID.ISLOCALNAME);
	AID map_manager = new AID("MapManager",AID.ISLOCALNAME);

	
	protected void setup(){
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(structure_manager);
		//msg.addReceiver(unit_manager);
		//msg.addReceiver(commander);	
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setContent("dummy-action");
		
		addBehaviour(new BuildManagerAgentInit(this,msg));
	}
	
  class BuildManagerAgentInit extends AchieveREInitiator{
		
		public BuildManagerAgentInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}
		protected void handleInform(ACLMessage inform){
			System.out.println("Agent responded!!!");
		}
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
		}
		protected void handleFailure(ACLMessage failure){
			System.out.println("Failed to get response!!!");
		}

//		protected void handleAllResultNotifications(Vector notifications) {
			//if (notifications.size() < nResponders) {
				// Some responder didn't reply within the specified timeout
//				System.out.println("Timeout expired: missing " + (notifications.size()) + " responses");
			//}
//		}

	}//end BuildManagerAgentInit

  /* BuildingManager responder class 
	class BuildingManagerAgentResp extends AchieveREResponder{
		
		public BuildingManagerAgentResp(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
			System.out.println("Agent "+getLocalName()+": REQUEST received from "+request.getSender().getName()+". Action is "+request.getContent());
			//if (checkAction()) {
				// We agree to perform the action. Note that in the FIPA-Request
				// protocol the AGREE message is optional. Return null if you
				// don't want to send it.
				System.out.println("Agent "+getLocalName()+": Agree");
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				return agree;

	//		}else{
			
				// We refuse to perform the action
	//			System.out.println("Agent "+getLocalName()+": Refuse");
	//			throw new RefuseException("check-failed");
	//		}
		}
		
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
			//if( performAction() ) {
				System.out.println("Agent "+getLocalName()+": Action successfully performed");
				ACLMessage inform = request.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
      
			//}else{
			//	System.out.println("Agent "+getLocalName()+": Action failed");
		//		throw new FailureException("unexpected-error");
	//		}		
      
		}//end prepareResultNotification
	}//class BuildingManagerAgentResp
  */



}
