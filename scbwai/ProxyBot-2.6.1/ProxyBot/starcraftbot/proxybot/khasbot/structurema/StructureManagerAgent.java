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
		MessageTemplate mt = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
    
    //use the arguments for testing purposes only
    Object[] args = getArguments();
        String arg1 = args[0].toString(); 
        String arg2 = args[1].toString(); 
        String arg3 = args[2].toString(); 

    ParallelBehaviour controller = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    // add all the individual behaviours 
    controller.addSubBehaviour(new StructureManagerAgentResp(this, mt));

    // finally add the parallel behaviour
    addBehaviour(controller);
	}
  
}//end StructureManagerAgent

