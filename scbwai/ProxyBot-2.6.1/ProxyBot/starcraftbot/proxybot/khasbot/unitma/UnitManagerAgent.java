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
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.*;
import java.util.*;

import starcraftbot.proxybot.CommandId;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.GameCommandQueue;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.game.PlayerObject;
import starcraftbot.proxybot.game.Race;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

@SuppressWarnings("serial")
public class UnitManagerAgent extends KhasBotAgent{

  //private GameCommandQueue commandsToDo = null;
  UnitManagerAgentInitCmdsToCommander sendCommandsToCommander = null;
  //PlayerObject myPlayer = null;

  @Override
  protected void setup(){
    super.setup();

    this.codec = new SLCodec();
    this.getContentManager().registerLanguage(this.codec);
    this.ontology=JADEManagementOntology.getInstance();
    this.getContentManager().registerOntology(this.ontology);

    //non structure units i get from the game objects that belong
    //to my player
    ArrayList<UnitObject> unitsTraining = null; //we may or may not need this
    /**
     *this will maintain a list of worker units
     *index 0 - will be idle workers, not assigned to any task
     *   this HashMap will use <k,v> as <unit id num,UnitObject>
     *index 1 - will be workers that are assigned tasks
     *   this HashMap will use <k,v> as <unit id num,UnitObject>
     */
    List<ArrayList<UnitObject>> workerUnits = new ArrayList<ArrayList<UnitObject>>(2);
    /**
     *groundUnits
     *index 0 - will be idle ground, not assigned to any task
     *   this HashMap will use <k,v> as <unit id num,UnitObject>
     *index 1 - will be ground that are assigned tasks
     *   this HashMap will use <k,v> as <unit id num, UnitObject>
     */
    List<HashMap<Integer, ArrayList<UnitObject>>> groundUnits = new ArrayList<HashMap<Integer, ArrayList<UnitObject>>>(2);
    /**
     *airUnits
     *index 0 - will be idle air, not assigned to any task
     *   this HashMap will use <k,v> as <unit id num,UnitObject>
     *index 1 - will be air that are assigned tasks
     *   this HashMap will use <k,v> as <unit id num, UnitObject>
     *
     */
    List<HashMap<Integer, ArrayList<UnitObject>>> airUnits = new ArrayList<HashMap<Integer, ArrayList<UnitObject>>>(2);

    //this.commandsToDo = new GameCommandQueue();
    workerUnits.add(0, new ArrayList<UnitObject>());
    workerUnits.add(1, new ArrayList<UnitObject>());

    //myDS.put(getLocalName() + "agent", this);
    myDS.put("workerUnits", workerUnits);
    myDS.put("usingGameObject", true);

    UnitManagerAgentRespInfCmd resp_inf_cmd =
            new UnitManagerAgentRespInfCmd(this, commander_inform_mt);
    UnitManagerAgentRespFIPAReqBuildM resp_fipa_req_buildm =
            new UnitManagerAgentRespFIPAReqBuildM(this, buildm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqResM resp_fipa_req_resm =
            new UnitManagerAgentRespFIPAReqResM(this, resm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqBattM resp_fipa_req_battm =
            new UnitManagerAgentRespFIPAReqBattM(this, battm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqStructM resp_fipa_req_structm =
            new UnitManagerAgentRespFIPAReqStructM(this, structm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
            new UnitManagerAgentRespFIPAReqMapM(this, mapm_fipa_req_mt);
    UnitManagerAgentRespFIPAReqCmd resp_fipa_req_cmd =
            new UnitManagerAgentRespFIPAReqCmd(this, cmd_fipa_req_mt);
    UnitManagerAgentRespInfCommands resp_inf_commands =
            new UnitManagerAgentRespInfCommands(this, inform_commands_mt);

    resp_inf_cmd.setDataStore(myDS);
    resp_fipa_req_buildm.setDataStore(myDS);
    resp_fipa_req_resm.setDataStore(myDS);
    resp_fipa_req_battm.setDataStore(myDS);
    resp_fipa_req_structm.setDataStore(myDS);
    resp_fipa_req_mapm.setDataStore(myDS);
    resp_fipa_req_cmd.setDataStore(myDS);
    resp_inf_commands.setDataStore(myDS);

    /* handle ACLMessgae.INFORM responders */
    addThreadedBehaviour(resp_inf_cmd);

    /* handle FIPA request responders */
    addThreadedBehaviour(resp_fipa_req_buildm);
    addThreadedBehaviour(resp_fipa_req_resm);
    addThreadedBehaviour(resp_fipa_req_battm);
    addThreadedBehaviour(resp_fipa_req_structm);
    addThreadedBehaviour(resp_fipa_req_mapm);
    addThreadedBehaviour(resp_fipa_req_cmd);
    addThreadedBehaviour(resp_inf_commands);

    sendCommandsToCommander = new UnitManagerAgentInitCmdsToCommander(this, commander);
    sendCommandsToCommander.setDataStore(myDS);
  }

  public void processCmdsReceived(ArrayList<GameCommand> cmds){

    sendCommandsToCommander.cmdToSend(cmds);

    addThreadedBehaviour(sendCommandsToCommander);

    /*
     * Done last.
     * once the command has been processed reset
     * the behaviour so that it can be scheduled again
     */
    sendCommandsToCommander.reset();
  }

  public void setGameObject(GameObject g){   
    GameObject lgameObj = g;
    PlayerObject myPlayer = null;

    assert g == null: "Incoming GameObject g cannot be null";
    assert lgameObj == null: "LocalGameObject lgameObj cannot be null";

    //System.out.println("Sending game object to MapManager");
    /* sent the GameObject to MapManager so that he can start to analyze the map info */
    /* MUST FIX THIS CRAP */
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    msg.setConversationId(ConverId.UnitM.NeedGameObject.getConId());
    msg.addReceiver(map_manager);
    try{
        msg.setContentObject(lgameObj);
    }catch(Exception e){
      System.out.println("Failed to set message object: " + e.toString());
    }
    
    send(msg);

    msg.clearAllReceiver();
    
    msg.addReceiver(resource_manager);
    try{
        msg.setContentObject(lgameObj);
    }catch(Exception e){
      System.out.println("Failed to set message object: " + e.toString());
    }
    send(msg);


    myDS.put("gameObj", lgameObj);
    
    myPlayer = lgameObj.getMyPlayer();

    myDS.put("myPlayer", myPlayer);

    assert lgameObj.getUnitsInGame() == null: "LocalGameObject lgameObj -> Units unitsInGame cannot be null";
    assert lgameObj.getUnitsInGame().getMyPlayersNonStructureUnits() == null: "LocalGameObject lgameObj -> Units unitsInGame  ->  HashMap<Integer,ArrayList<UnitObject>> getMyPlayersNonStructureUnits cannot be null";

    extractUnits(lgameObj.getUnitsInGame().getMyPlayersNonStructureUnits());
    //extractUnits(toExtract.getMyPlayersStructureUnits());

  }

  public void setGameObjectUpdate(GameObjectUpdate g){
    GameObjectUpdate lgameObjUp = g;
    PlayerObject myPlayer = null;

    assert g == null: "Incoming GameObjectUpdate g cannot be null";
    assert lgameObjUp == null: "LocalGameObjectUpdate lgameObjUp cannot be null";

    myDS.put("gameObjUp", lgameObjUp);
    myDS.put("usingGameObject", false);

    myPlayer = lgameObjUp.getMyPlayer();

    myDS.put("myPlayer", myPlayer);

    assert lgameObjUp.getUnitsInGame() == null: "LocalGameObjectUpdate lgameObjUp -> Units unitsInGame cannot be null";
    assert lgameObjUp.getUnitsInGame().getMyPlayersNonStructureUnits() == null: "LocalGameObjectUpdate lgameObjUp -> Units unitsInGame  ->  HashMap<Integer,ArrayList<UnitObject>> getMyPlayersNonStructureUnits cannot be null";

    extractUnits(lgameObjUp.getUnitsInGame().getMyPlayersNonStructureUnits());
    //extractUnits(gameObjUp.getUnitsInGame().getMyPlayersStructureUnits());
      
  }

  /**
   * This method return a worker class unit for the player if one is available
   * @return
   */
  @SuppressWarnings("unchecked")
  public UnitObject WorkerAvailable(){

    List<ArrayList<UnitObject>> workerUnits = (List<ArrayList<UnitObject>>) myDS.get("workerUnits");
    ArrayList<UnitObject> workerUnitsIdle = workerUnits.get(0);
    ArrayList<UnitObject> workerUnitsWorking = workerUnits.get(1);

    //int unit_id = -1;
    //System.out.println(getLocalName()+":: Checking for an Available Worker...");
    if(workerUnitsIdle != null){
      if(!workerUnitsIdle.isEmpty()){
        //our idle list is not empty, so then there is at least one worker standing around doing nothing
        //ArrayList<UnitObject> temp = null;

        UnitObject u = workerUnitsIdle.remove(0);
        //System.out.println(getLocalName() + "> Assigning an Available Worker...");
        workerUnitsWorking.add(u);
        
        workerUnits.set(0, workerUnitsIdle);
        workerUnits.set(1, workerUnitsWorking);
        myDS.put("workerUnits", workerUnits);
        
        return u;

      }else{
        //System.out.println(getLocalName()+"> this.workerUnits.get(0).isEmpty");
      }
    }else{
      // System.out.println(getLocalName()+"> this.workerUnits == null");
    }
    return null;
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
   * Extract Units into workers, ground, and air units.
   * 
   * @param units
   */
  @SuppressWarnings("unchecked")
  public void extractUnits(HashMap<Integer, ArrayList<UnitObject>> units){
    /*
     *  units set up like:
     *  	Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
     */
    GameObject gameObj = (GameObject)myDS.get("gameObj");
    List<ArrayList<UnitObject>> workerUnits = (List<ArrayList<UnitObject>>) myDS.get("workerUnits");

    Unit workerType;
    if(gameObj.getMyPlayer().getPlayerRace() == Race.Protoss){
      workerType = Unit.Protoss_Probe;
    }else if(gameObj.getMyPlayer().getPlayerRace() == Race.Zerg){
      workerType = Unit.Zerg_Drone;
    }else{
      workerType = Unit.Terran_SCV;
    }

    ArrayList<UnitObject> unitsToCheck = units.get(workerType.getNumValue());
    ArrayList<UnitObject> idle = new ArrayList<UnitObject>();
    ArrayList<UnitObject> working = new ArrayList<UnitObject>();

    for(UnitObject u : unitsToCheck){
      if(u.isIdle() || u.isGaurding()){
        idle.add(u);
      }else{
        working.add(u);
      }
    }

    workerUnits.set(0, idle);
    workerUnits.set(1, working);
    myDS.put("workerUnits", workerUnits);

    //extract ground units...

    //extract air units...


  }

//  /**
//   * adds the new Commands to its existing queue.
//   * @param newCommands
//   */
//  public void addCommands(GameCommandQueue newCommands){
//    while(!newCommands.isEmpty()){
//      commandsToDo.push(newCommands.pop());
//    }
//
//  }

  /**
   * DefaultActions will, inside UnitManager, get stuff to mine and make new drones.
   */
  public void DefaultActions(){
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

