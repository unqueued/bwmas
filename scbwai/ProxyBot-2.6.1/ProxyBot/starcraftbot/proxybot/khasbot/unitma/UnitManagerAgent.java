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

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.mapma.MapLocation;
import starcraftbot.proxybot.wmes.unit.UnitWME;
import starcraftbot.proxybot.Constants.Order;
import starcraftbot.proxybot.command.*;
import starcraftbot.proxybot.command.Command;
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
	
	private GameObject game;

  AID commander = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;
  
	protected void setup(){
    //DEBUG 
    //System.out.println(getAID().getLocalName() + ": is alive !!!");
   
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
    MessageTemplate commander_inform_mt = //MessageTemplate.and(
                                          MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                                          /*MessageTemplate.MatchSender(commander)*/
                                          //);

    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new UnitManagerAgentRespInform(this,commander_inform_mt));
    //root_behaviour.addSubBehaviour(new UnitManagerAgentInitInform(this,inform_mt));
    //root_behaviour.addSubBehaviour(new UnitManagerAgentGatherResources(this, resource_manager));
    

    addBehaviour(root_behaviour);

	}

	public void setGameObject(GameObject game) {
		this.game = game;
	}

	public GameObject getGameObject() {
		return game;
	}
	/**
	 * DefaultActions will, inside UnitManager, get stuff to mine and make new drones.
	 */
	public void DefaultActions() {
		// TODO Auto-generated method stub
		
		//System.out.println("UnitManager is ATTEMPTING ACTIONS...");
		
		System.out.flush();
		
		int my_id = game.getMyPlayer().getPlayerID();
		
		//System.out.println("UnitManager attempting actions, my playerID is: "+ my_id + " number of Units to look through is: " + game.getUnitArray().size());
		
		for(int i = 0; i < game.getUnitArray().size(); i++)
		{
			UnitsObject u = game.getUnitAt(i);
			if(u.getPlayerID() == my_id)
			{
				if(u.getType().getID() == StructureID.Protoss_Nexus)
				{
					//int c = Command.StarCraftCommand.train.ordinal();
					if(u.getOrder() != Order.Train.ordinal())
					{
						if(game.getMyPlayer().getResources().getMinerals() >= 50)
							game.getCommandsToDo().add(new Command(Command.StarCraftCommand.train, u.getID(), UnitID.ProtossID.Protoss_Probe.getID(), 0, 0));
						//System.out.println("Command( train, unitID:"+u.getID()+", Protoss_Probe ID->"+UnitID.ProtossID.Protoss_Probe.getID()+", 0, 0");
						//System.out.flush();
					}
				}
				else if(u.getType().getID() == UnitID.ProtossID.Protoss_Probe.getID())
				{
					if(u.getBuildTimer() == 0 && !((u.getOrder() == Order.MiningMinerals.ordinal()) ||
					   (u.getOrder() == Order.MoveToMinerals.ordinal()) ||
					   (u.getOrder() == Order.ReturnMinerals.ordinal()) ||
					   (u.getOrder() == Order.WaitForMinerals.ordinal()) ||
					   (u.getOrder() == Order.ResetCollision1.ordinal()) ||
					   (u.getOrder() == Order.ResetCollision2.ordinal()) ||
					   /*(u.getOrder() == Order.Nothing2.ordinal()) ||*/
					   (u.getOrder() == Order.Build5.ordinal())))
					{
						//find mineral patches
						
						System.out.print(" Probe ID: "+u.getID()+" not doing mining related-ness...was doing -> ");
						System.out.print(u.getOrder() +":("+ Order.values()[u.getOrder()]+")");
						System.out.print(" || probe buildtimer is at : " + u.getBuildTimer());
						System.out.println(" || probe traintimer is at :"+ u.getTrainTimer());
						
						
						//System.out.println("Going to assign this probe to mine?");
						System.out.flush();
						int patchID = -1;
						double cdist = Double.MAX_VALUE;
						//MapLocation closePatch;
						for(int j = 0; j< game.getUnitArray().size(); j++)
						{
							UnitsObject resource = game.getUnitAt(j);
							//int patchID = -1;
							//MapLocation closePatch;
							if(resource.getType().getID() == UnitsObject.Resource_Mineral_Field)
							{
									double dx = u.getX() - resource.getX();
									double dy = u.getY() - resource.getY();
									double dist = Math.sqrt(dx * dx + dy * dy);
			
									if (dist < cdist) {
										//System.out.println("found closer mineral patch to probe!");
										System.out.flush();
										patchID = resource.getID();
										cdist = dist;
									}
								}
									//game.getCommandQueue().rightClick(unit.getID(), patchI 
						}
						
						
						if(patchID > 0)
						{
						  game.getCommandsToDo().add(new Command(Command.StarCraftCommand.rightClickUnit, u.getID(), patchID, 0, 0));
						  //System.out.println("Command( rightClickUnit, unitID:"+u.getID()+", targetID:"+patchID+", 0, 0");
						  //System.out.flush();
						}
					}
						
				}
			}
		}
		
		//System.out.println("UnitManager added commands!");
		//System.out.flush();
	}
  
}//end UnitManagerAgent


