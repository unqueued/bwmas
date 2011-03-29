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

  AID commander = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;
  
	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");
   
    //arguments passed into agent
    Object[] args = getArguments();
    String temp = null; 
    //System.out.println(getAID().getLocalName() + ": RX " + args.length + " arguments");

    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    for(int i=0; i < args.length; i++){
      temp = (String) args[i];
      //add the agents that will get game updates
      if(temp.matches(".*[Cc]ommander.*"))
        commander = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Bb]uilding[Mm]anager.*"))
        building_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Ss]tructure[Mm]anager.*"))
        structure_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Bb]attle[Mm]anager.*"))
        battle_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Rr]esource[Mm]anager.*"))
        resource_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Mm]ap[Mm]anager.*"))
        map_manager = new AID(temp,AID.ISLOCALNAME);
    } 
 


    //
    //Message Templates
    //
    
    //game updates will be INFORM messages
    MessageTemplate commander_inform_mt = MessageTemplate.and(
                                          MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                          MessageTemplate.MatchSender(commander)
                                          );

    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespInform(this,commander_inform_mt));
    //root_behaviour.addSubBehaviour(new UnitManagerAgentInitInform(this,inform_mt));
    //root_behaviour.addSubBehaviour(new UnitManagerAgentGatherResources(this, resource_manager));
    

    addBehaviour(root_behaviour);

	}
  
}//end UnitManagerAgent


