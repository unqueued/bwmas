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

import starcraftbot.proxybot.CommandId;
import starcraftbot.proxybot.command.GameCommand;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import java.util.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.game.PlayerObject;

@SuppressWarnings("serial")
public class UnitManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
	private GameObject gameObj;
  private GameObjectUpdate gameObjUp;

  private boolean usingGameObject = true;

  UnitManagerAgentInitCmdsToCommander sendCommandsToCommander = null;

  AID commander = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;

  //non structure units i get from the game objects that belong
  //to my player
  HashMap<Integer,ArrayList<UnitObject>> myNonStructUnits  = null;

	protected void setup(){
  
    /*
     * Message Template: ACLMessage.INFORM
     */
    MessageTemplate commander_inform_mt = null;

    /*
     * Message Template: FIPANames.InteractionProtocol.FIPA_REQUEST
     */
    MessageTemplate mapm_fipa_req_mt = null;
    MessageTemplate buildm_fipa_req_mt = null;
    MessageTemplate structm_fipa_req_mt = null;
    MessageTemplate resm_fipa_req_mt = null;
    MessageTemplate battm_fipa_req_mt = null;
    MessageTemplate cmd_fipa_req_mt = null;


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

    mapm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(map_manager)
                                              )
                                            );

    buildm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(building_manager)
                                             )
                                           );

    structm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(structure_manager)
                                              )
                                            );

    resm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(resource_manager)
                                             )
                                           );

    battm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(battle_manager)
                                              )
                                            );

    cmd_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(commander)
                                              )
                                            );
   

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);

    //set a behaviour to handle all the inform messages from the commander
    //right now handles:
    //  GameObject
    //  GameObjectUpdate
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespInfCmd(this,commander_inform_mt));

    //can't think of any service/info Map Manager would need from Unit Manager
    //root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt));
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqBuildM(this,buildm_fipa_req_mt));
    //can't think of any service/info Structure Manager would need from Unit Manager
    //root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqStructM(this,structm_fipa_req_mt));
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqResM(this,resm_fipa_req_mt));
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqBattM(this,battm_fipa_req_mt));
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespFIPAReqCmd(this,cmd_fipa_req_mt));
    
    sendCommandsToCommander = new UnitManagerAgentInitCmdsToCommander(this,commander);
    root_behaviour.addSubBehaviour(sendCommandsToCommander);


    addBehaviour(root_behaviour);

	}

  public void processCmdsReceived(GameCommand cmd){
    sendCommandsToCommander.cmdToSend(cmd);

    /*
     * Done last.
     * once the command has been processed reset
     * the behaviour so that it can be scheduled again
     */
    sendCommandsToCommander.reset();
  }

	//may not need this, but we'll see
  public GameObject getGameObject()
	{
		return gameObj;
	}

	public void setGameObject(GameObject g)
	{
    //System.out.println(this.getLocalName() + "> setting GameObject <");
		gameObj = g;
    myNonStructUnits = gameObj.getUnitsInGame().getMyPlayersNonStructureUnits();
	}

  //may not need this, but we'll see
 	public GameObjectUpdate getGameObjectUpdate()
	{
		return gameObjUp;
	}

  public void setGameObjectUpdate(GameObjectUpdate g)
	{
    //System.out.println(this.getLocalName() + "> setting GameObjectUpdate <");
    usingGameObject = false;
		gameObjUp = g;
    myNonStructUnits = gameObjUp.getUnitsInGame().getMyPlayersNonStructureUnits();
	}

  /**
   * This method return a worker class unit for the player if one is available
   * @return
   */
  public UnitObject WorkerAvailable(){
    UnitObject unit = null;

    return unit;
  }

  public void SmallUnitGroup(){

  }

  public void MediumUnitGroup(){

  }

  public void LargeUnitGroup(){

  }

  public void UnitsLost(){

  }



	/**
	 * DefaultActions will, inside UnitManager, get stuff to mine and make new drones.
	 */
	public void DefaultActions() {
		// TODO Auto-generated method stub
		
		//System.out.println("UnitManager is ATTEMPTING ACTIONS...");
		
//		System.out.flush();
//
//    PlayerObject my_player = game.getMyPlayer();
//		int my_id = my_player.getPlayerID();
//
//		//System.out.println("UnitManager attempting actions, my playerID is: "+ my_id + " number of Units to look through is: " + game.getUnitArray().size());
//
//    //for my units
//    Units u = game.getUnitsInGame();
//
//    //get my nexus
//    ArrayList<UnitObject> my_nexus = u.myPlayersUnits.get(Unit.Structure.Protoss.Protoss_Nexus.getNumValue());
//
//    if(!my_nexus.isEmpty()) {
//      //int c = Command.StarCraftCommand.train.ordinal();
//      UnitObject nexus = my_nexus.get(0); //get the first nexus
//      if(nexus.getOrder() != CommandId.Order.Train.getNumValue()) {
        //if(my_player.getResources().getMinerals() >= 50)

         //game.getCommandsToDo().add(new Command(CommandId.StarCraftCommand.train.getNumValue(), nexus.getID(), Unit.NonStructure.Protoss.Protoss_Probe.getNumValue(), 0, 0));
        //System.out.println("Command( train, unitID:"+u.getID()+", Protoss_Probe ID->"+UnitID.ProtossID.Protoss_Probe.getID()+", 0, 0");
        //System.out.flush();
      
    }
		
        
//		for(int i = 0; i < game.getUnitArray().size(); i++)
//		{
//			UnitsObject u = game.getUnitAt(i);
//			if(u.getPlayerID() == my_id)
//			{
//				if(u.getType().getID() == StructureID.Protoss_Nexus)
//				{
//					//int c = Command.StarCraftCommand.train.ordinal();
//					if(u.getOrder() != Order.Train.ordinal())
//					{
//						if(game.getMyPlayer().getResources().getMinerals() >= 50)
//							game.getCommandsToDo().add(new Command(Command.StarCraftCommand.train, u.getID(), UnitID.ProtossID.Protoss_Probe.getID(), 0, 0));
//						//System.out.println("Command( train, unitID:"+u.getID()+", Protoss_Probe ID->"+UnitID.ProtossID.Protoss_Probe.getID()+", 0, 0");
//						//System.out.flush();
//					}
//				}
//				else if(u.getType().getID() == UnitID.ProtossID.Protoss_Probe.getID())
//				{
//					if(u.getBuildTimer() == 0 && !((u.getOrder() == Order.MiningMinerals.ordinal()) ||
//					   (u.getOrder() == Order.MoveToMinerals.ordinal()) ||
//					   (u.getOrder() == Order.ReturnMinerals.ordinal()) ||
//					   (u.getOrder() == Order.WaitForMinerals.ordinal()) ||
//					   (u.getOrder() == Order.ResetCollision1.ordinal()) ||
//					   (u.getOrder() == Order.ResetCollision2.ordinal()) ||
//					   /*(u.getOrder() == Order.Nothing2.ordinal()) ||*/
//					   (u.getOrder() == Order.Build5.ordinal())))
//					{
//						//find mineral patches
//
//						System.out.print(" Probe ID: "+u.getID()+" not doing mining related-ness...was doing -> ");
//						System.out.print(u.getOrder() +":("+ Order.values()[u.getOrder()]+")");
//						System.out.print(" || probe buildtimer is at : " + u.getBuildTimer());
//						System.out.println(" || probe traintimer is at :"+ u.getTrainTimer());
//
//
//						//System.out.println("Going to assign this probe to mine?");
//						System.out.flush();
//						int patchID = -1;
//						double cdist = Double.MAX_VALUE;
//						//MapLocation closePatch;
//						for(int j = 0; j< game.getUnitArray().size(); j++)
//						{
//							UnitsObject resource = game.getUnitAt(j);
//							//int patchID = -1;
//							//MapLocation closePatch;
//							if(resource.getType().getID() == UnitsObject.Resource_Mineral_Field)
//							{
//									double dx = u.getX() - resource.getX();
//									double dy = u.getY() - resource.getY();
//									double dist = Math.sqrt(dx * dx + dy * dy);
//
//									if (dist < cdist) {
//										//System.out.println("found closer mineral patch to probe!");
//										System.out.flush();
//										patchID = resource.getID();
//										cdist = dist;
//									}
//								}
//									//game.getCommandQueue().rightClick(unit.getID(), patchI
//						}
//
//
//						if(patchID > 0)
//						{
//						  game.getCommandsToDo().add(new Command(Command.StarCraftCommand.rightClickUnit, u.getID(), patchID, 0, 0));
//						  //System.out.println("Command( rightClickUnit, unitID:"+u.getID()+", targetID:"+patchID+", 0, 0");
//						  //System.out.flush();
//						}
//					}
//
//				}
//			}
//		}
		
		//System.out.println("UnitManager added commands!");
		//System.out.flush();
  
}//end UnitManagerAgent


