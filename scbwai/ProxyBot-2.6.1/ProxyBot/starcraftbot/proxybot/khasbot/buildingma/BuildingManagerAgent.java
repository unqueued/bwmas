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

	AID commander = new AID("KhasCommander",AID.ISLOCALNAME);
	AID unit_manager = new AID("KhasUnitManager",AID.ISLOCALNAME);
	AID map_manager = new AID("KhasMapManager",AID.ISLOCALNAME);
	
	protected void setup(){
    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

		//ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		//msg.addReceiver(unit_manager);
		//msg.addReceiver(commander);	
		//msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		//msg.setContent("dummy-action");
		
    
    //use the arguments for testing purposes only
    /*
    Object[] args = getArguments();
        String arg1 = args[0].toString(); 
        String arg2 = args[1].toString(); 
        String arg3 = args[2].toString(); 
    */
    //ParallelBehaviour controller = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    // add all the individual behaviours 
    //controller.addSubBehaviour(new StructureManagerAgentResp(this, mt));

    // finally add the parallel behaviour
    //addBehaviour(controller);
    addBehaviour(new BuildingManagerAgentResp(this,mt));
	}

}

