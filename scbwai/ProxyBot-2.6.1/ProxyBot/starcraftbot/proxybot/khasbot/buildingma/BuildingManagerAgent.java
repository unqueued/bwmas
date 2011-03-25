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
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;


@SuppressWarnings("serial")
public class BuildingManagerAgent extends Agent {
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();

	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    //
    //Message Templates
    //
 
    MessageTemplate fipa_request_mt = null;

    //game updates will be INFORM messages
    MessageTemplate inform_mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);


    //this template will only respond to FIPA_REQUEST messages 
    fipa_request_mt = MessageTemplate.and(
                                 MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                 );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new BuildingManagerAgentRespFIPARequest(this,fipa_request_mt));
    root_behaviour.addSubBehaviour(new BuildingManagerAgentRespInform(this,inform_mt));

    addBehaviour(root_behaviour);
	}//end setup

}//end BuildingManagerAgent

