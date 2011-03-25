/**
 * Agent Description:
 * 	Controls all the structure Units, this includes training new units and researching tech
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 *  <-> UnitManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 *  @see UnitManagerAgent
 */
package starcraftbot.proxybot.khasbot.structurema;

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
public class StructureManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");

		MessageTemplate mt = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
    
    /*
    ParallelBehaviour controller = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    // add all the individual behaviours 
    controller.addSubBehaviour(new StructureManagerAgentResp(this, mt));

    // finally add the parallel behaviour
    addBehaviour(controller);
    */
	}
  
}//end StructureManagerAgent

