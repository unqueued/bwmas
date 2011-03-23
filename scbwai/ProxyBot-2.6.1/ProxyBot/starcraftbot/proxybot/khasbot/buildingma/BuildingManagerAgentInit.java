/**
 * Incorporates Construction Manager and Building Placer from "bwsal".
 * 
 * Agent Description:
 * 	Controls units to build buildings, and communicates with MapManagerAgent to place possible 
 * 	future buildings known from Commander
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 *  <-> UnitManagerAgent
 *  <-  MapManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 *  @see UnitManagerAgent
 *  @see MapManagerAgent
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


@SuppressWarnings("serial")
public class BuildingManagerAgentInit extends AchieveREInitiator{
		
		public BuildingManagerAgentInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}
		protected void handleInform(ACLMessage inform){
      System.out.println("*** " + myAgent.getLocalName() + ": handleInform() " + inform.getContent());
		}
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
		}
		protected void handleFailure(ACLMessage failure){
			System.out.println("Failed to get response!!!");
		}
/*
		protected void handleAllResultNotifications(Vector notifications) {
			if (notifications.size() < nResponders) {
				// Some responder didn't reply within the specified timeout
				System.out.println("Timeout expired: missing " + (notifications.size()) + " responses");
			}
		}
*/
}//end BuildingManagerAgentInit


