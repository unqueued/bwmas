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
import jade.lang.acl.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;

@SuppressWarnings("serial")
public class BuildingManagerAgent extends Agent {
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
 	private GameObject gameObj;
  private GameObjectUpdate gameObjUp;

  AID commander = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;
  AID unit_manager = null;

	protected void setup(){
   /*
     * Message Template: ACLMessage.INFORM
     */
    MessageTemplate commander_inform_mt = null;

    /*
     * Message Template: FIPANames.InteractionProtocol.FIPA_REQUEST
     */
    MessageTemplate unitm_fipa_req_mt = null;
    MessageTemplate mapm_fipa_req_mt = null;

    //arguments passed into agent
    Object[] args = getArguments();
    
    String temp = null;

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

    commander_inform_mt = MessageTemplate.and(
                                               MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                               MessageTemplate.MatchSender(commander)
                                             );


    unitm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(unit_manager)
                                             )
                                           );

    mapm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(map_manager)
                                              )
                                            );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new BuildingManagerAgentRespInfCmd(this,commander_inform_mt));

    root_behaviour.addSubBehaviour(new BuildingManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt));
    //can't see a reason why MapManager would ever need to request anything from BuildingManager
    //root_behaviour.addSubBehaviour(new BuildingManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt));

    addBehaviour(root_behaviour);
	}//end setup
	
	//may not need this, but we'll see
  public GameObject getGameObject()
	{
		return gameObj;
	}

	public void setGameObject(GameObject g)
	{
    System.out.println(this.getLocalName() + "> setting GameObject <");
		gameObj = g;
	}

  //may not need this, but we'll see
 	public GameObjectUpdate getGameObjectUpdate()
	{
		return gameObjUp;
	}

  public void setGameObjectUpdate(GameObjectUpdate g)
	{
    System.out.println(this.getLocalName() + "> setting GameObjectUpdate <");
		gameObjUp = g;
	}

}//end BuildingManagerAgent

