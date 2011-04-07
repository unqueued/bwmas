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
import jade.lang.acl.*;
import java.util.*;

import starcraftbot.proxybot.CommandId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.game.PlayerObject;
import starcraftbot.proxybot.game.Race;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

@SuppressWarnings("serial")
public class UnitManagerAgent extends KhasBotAgent {

  private boolean usingGameObject = true;

  UnitManagerAgentInitCmdsToCommander sendCommandsToCommander = null;

  PlayerObject myPlayer = null;
  //non structure units i get from the game objects that belong
  //to my player
  ArrayList<UnitObject> unitsTraining = null; //we may or may not need this

  /**
   *this will maintain a list of worker units
   *index 0 - will be idle workers, not assigned to any task
   *   this HashMap will use <k,v> as <unit id num,UnitObject>
   *index 1 - will be workers that are assigned tasks
   *   this HashMap will use <k,v> as <CommandId.Order, UnitObject>
   *
   *the same naming scheme is used for the other two lists
   *groundUnits and airUnits
   *index 0 - will be idle ground/air, not assigned to any task
   *   this HashMap will use <k,v> as <unit id num,UnitObject>
   *index 1 - will be ground/air that are assigned tasks
   *   this HashMap will use <k,v> as <CommandId.Order, UnitObject>
   *
   */
  List<HashMap<Integer,ArrayList<UnitObject>>> workerUnits = new ArrayList<HashMap<Integer,ArrayList<UnitObject>>>(2);
  List<HashMap<Integer,ArrayList<UnitObject>>> groundUnits = new ArrayList<HashMap<Integer,ArrayList<UnitObject>>>(2);
  List<HashMap<Integer,ArrayList<UnitObject>>> airUnits = new ArrayList<HashMap<Integer,ArrayList<UnitObject>>>(2);

	protected void setup(){
    super.setup();

    UnitManagerAgentRespInfCmd resp_inf_cmd =
            new UnitManagerAgentRespInfCmd(this,commander_inform_mt);
    UnitManagerAgentRespFIPAReqBuildM resp_fipa_req_buildm = 
            new UnitManagerAgentRespFIPAReqBuildM(this,buildm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqResM resp_fipa_req_resm =
            new UnitManagerAgentRespFIPAReqResM(this,resm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqBattM resp_fipa_req_battm =
            new UnitManagerAgentRespFIPAReqBattM(this,battm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqStructM resp_fipa_req_structm =
            new UnitManagerAgentRespFIPAReqStructM(this,structm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
            new UnitManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqCmd resp_fipa_req_cmd =
            new UnitManagerAgentRespFIPAReqCmd(this,cmd_fipa_req_mt);

    resp_fipa_req_buildm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_resm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_battm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_structm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_mapm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_cmd.setDataStore(resp_inf_cmd.getDataStore());

    /* handle ACLMessgae.INFORM responders */
    addThreadedBehaviour(resp_inf_cmd);

    /* handle FIPA request responders */
    addThreadedBehaviour(resp_fipa_req_buildm);
    addThreadedBehaviour(resp_fipa_req_resm);
    addThreadedBehaviour(resp_fipa_req_battm);
    addThreadedBehaviour(resp_fipa_req_structm);
    addThreadedBehaviour(resp_fipa_req_mapm);
    addThreadedBehaviour(resp_fipa_req_cmd);

//    sendCommandsToCommander = new UnitManagerAgentInitCmdsToCommander(this,commander);
//    root_behaviour.addSubBehaviour(sendCommandsToCommander);
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

  @Override
	public void setGameObject(GameObject g){
		gameObj = g;
    //from the game object get the non-structure units and place them in the appropriate
    //list
    //    myNonStructUnits = gameObjUp.getUnitsInGame().getMyPlayersNonStructureUnits();
    myPlayer = gameObj.getMyPlayer();
    extractUnits(gameObj.getUnitsInGame().getMyPlayersNonStructureUnits());
	}

  @Override
  public void setGameObjectUpdate(GameObjectUpdate g){
    usingGameObject = false;
		gameObjUp = g;
    myPlayer = gameObjUp.getMyPlayer();
    extractUnits(gameObjUp.getUnitsInGame().getMyPlayersNonStructureUnits());
	}

  /**
   * This method return a worker class unit for the player if one is available
   * @return
   */
  public int WorkerAvailable(){
    int unit_id = -1;
//    if(!workerUnits.get(0).isEmpty()){
//      //our idle list is not empty, so then there is at least one worker standing around doing nothing
//      ArrayList<UnitObject> temp = null;
//
//      //take the idle unit from the idle list
//      temp = workerUnits.get(0).get(Unit.NonStructure.Protoss.Protoss_Probe.getNumValue());
//      unit_id = temp.remove(0);
//
//      //place the unit on the working list
//      temp = workerUnits.get(1).get(Unit.NonStructure.Protoss.Protoss_Probe.getNumValue());
//      temp.add(unit_id);
//    }
    return unit_id;
  }

  public void SmallUnitGroup(){

  }

  public void MediumUnitGroup(){

  }

  public void LargeUnitGroup(){

  }

  public void UnitsLost(){

  }

  public void extractUnits(HashMap<Integer,ArrayList<UnitObject>> units){
    //separate out the units to their designated lists
    if( myPlayer.getPlayerRace() == Race.Protoss ){
      //add probes
      
    }
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


