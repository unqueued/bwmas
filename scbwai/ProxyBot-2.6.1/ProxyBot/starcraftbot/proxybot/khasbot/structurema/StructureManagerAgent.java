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

import jade.lang.acl.ACLMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.game.PlayerObject;
import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class StructureManagerAgent extends KhasBotAgent{

  //NOTE: fix the way this gets updated, so that we can maintain a queue
  //based on how the structures are used to train units
//  HashMap<Integer,ArrayDeque<UnitObject>> structures = null;
//  PlayerObject myPlayer = null;

  HashMap<Integer,Integer> trainedAt = null;

  @Override
	protected void setup(){
    super.setup();

    unitTrainedAtMap();

    myDS.put("structures",null);
    
    StructureManagerAgentRespInfUnitM resp_inf_unitm =
            new StructureManagerAgentRespInfUnitM(this,unitm_inform_mt);
    StructureManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new StructureManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);

    resp_inf_unitm.setDataStore(myDS);
    resp_fipa_req_unitm.setDataStore(myDS);
   
    addThreadedBehaviour(resp_inf_unitm);
    addThreadedBehaviour(resp_fipa_req_unitm);
	}

	public void setGameObject(GameObject g){
    //structures = g.getUnitsInGame().getMyPlayersStructureUnits();
    myDS.put("structures", g.getUnitsInGame().getMyPlayersStructureUnits());
    //myPlayer = g.getMyPlayer();
    myDS.put("myPlayer",g.getMyPlayer());
  }
  
	public void setGameObjectUpdate(GameObjectUpdate g){
    //structures = g.getUnitsInGame().getMyPlayersStructureUnits();
    myDS.put("structures", g.getUnitsInGame().getMyPlayersStructureUnits());
    //myPlayer = g.getMyPlayer();
    myDS.put("myPlayer",g.getMyPlayer());
  }

  @SuppressWarnings("unchecked")
  public boolean trainNewUnit(Unit unit){
    HashMap<Integer,ArrayDeque<UnitObject>> structures = (HashMap<Integer,ArrayDeque<UnitObject>>)myDS.get("structures");

    if(structures == null)
      return false;
    
    int structureId = trainedAt.get(unit.getNumValue());

    UnitObject building = structures.get(structureId).remove();

    ArrayList<GameCommand> lcommandsToDo = new ArrayList<GameCommand>();

    lcommandsToDo.add(GameCommand.train(building.getID(),unit.getNumValue()));
    requestCommandsExe(lcommandsToDo);

    structures.get(structureId).add(building);
    
    return false;
  }

  public boolean canUpgradeTechLevel(){
    return false;
  }

  public boolean canResearchTechLevel(){
    return false;
  }

  /* may have to make this it's own thread */
  public void requestCommandsExe(ArrayList<GameCommand> cmds){
    if(!cmds.isEmpty()){
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setConversationId(ConverId.UnitM.NewCommands.getConId());
      try{
        msg.setContentObject((ArrayList<GameCommand>)cmds);
      }catch(Exception e){
        System.out.println("Failed to set message object: " + e.toString());
      }
      msg.addReceiver(unit_manager);
      send(msg);
      System.out.println(this.getLocalName() + "> Commands sent to " + unit_manager.getLocalName() + "cmd size: " + cmds.size());
    }
  }

  private void unitTrainedAtMap(){
    trainedAt = new HashMap<Integer,Integer>();

    //Protoss_Nexus(154),
    trainedAt.put(Unit.Protoss_Probe.getNumValue(), Unit.Protoss_Nexus.getNumValue());
    //Protoss_Robotics_Facility(155),
    //Protoss_Pylon(156),
    //Protoss_Assimilator(157),
    //Protoss_Observatory(159),
    //Protoss_Gateway(160),
    //Protoss_Photon_Cannon(162),
    //Protoss_Citadel_of_Adun(163),
    //Protoss_Cybernetics_Core(164),
    //Protoss_Templar_Archives(165),
    //Protoss_Forge(166),
    //Protoss_Stargate(167),
    //Protoss_Fleet_Beacon(169),
    //Protoss_Arbiter_Tribunal(170),
    //Protoss_Robotics_Support_Bay(171),
    //Protoss_Shield_Battery(172)

  }
}//end StructureManagerAgent

