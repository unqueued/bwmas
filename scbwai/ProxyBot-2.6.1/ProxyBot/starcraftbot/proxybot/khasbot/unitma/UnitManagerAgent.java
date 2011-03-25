/**
 * Agent Description:
 * 	Controls all the structure Units, this includes training new units and researching tech
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 * 	<-> BuildingManagerAgent
 * 	<-> StructureManagerAgent
 * 	 -> BattleManagerAgent
 * 	 -> ResourceManagerAgent
 * 	<-  MapManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 * 	@see BuildingManagerAgent
 * 	@see StructureManagerAgent
 * 	@see BattleManagerAgent
 * 	@see ResourceManagerAgent
 * 	@see MapManagerAgent
 *
 */
package starcraftbot.proxybot.khasbot.unitma;

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
public class UnitManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");
	   
    //
    //Message Templates
    //
    
    //game updates will be INFORM messages
    MessageTemplate inform_mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespInform(this,inform_mt));
    //root_behaviour.addSubBehaviour(new UnitManagerAgentInitInform(this,inform_mt));
    

    addBehaviour(root_behaviour);

	}
  
}//end UnitManagerAgent


