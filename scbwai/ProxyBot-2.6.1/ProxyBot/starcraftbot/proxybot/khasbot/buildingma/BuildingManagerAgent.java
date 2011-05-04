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


import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.khasbot.mapma.MapLocation;
import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class BuildingManagerAgent extends KhasBotAgent {

  boolean [][] poweredMap = null;
  int mapLength=0;
  int mapWidth=0;
  boolean buildPoweredMap = false;

  int count = 0;
  @Override
  protected void setup(){
    super.setup();

    myDS.put("worker",null);
    myDS.put("RequestWorker",false);
    myDS.put("workerTasks",null);

    MessageTemplate inf_fromUnitm_mt = MessageTemplate.and(
                                                            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                                            MessageTemplate.MatchSender(unit_manager)
                                                           );

    BuildingManagerAgentInfUnitM inf_unitm =
            new BuildingManagerAgentInfUnitM(this,inf_fromUnitm_mt,myDS);

//    BuildingManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
//            new BuildingManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);

    
//    resp_fipa_req_unitm.setDataStore(myDS);

    addThreadedBehaviour(inf_unitm);
//    addThreadedBehaviour(resp_fipa_req_unitm);

	}//end setup
	
	public void setGameObject(GameObject g){
    myDS.put("mapObj", g.getMapObj());
    if(!buildPoweredMap){
      buildPoweredMap = true;
      poweredMap = new boolean [g.getMapObj().getMapLength()][g.getMapObj().getMapWidth()];
      for(int i=0; i < mapLength; i++)
        for(int j=0; j < mapWidth; j++)
          poweredMap[i][j] = false;
    }
    myDS.put("startingLoc",g.getMyStartLocation());
	}
  
  public void setGameObjectUpdate(GameObjectUpdate g){
		
	}

  public void addWorker(UnitObject worker){
    myDS.put("worker", worker);
  }

  @SuppressWarnings("unchecked")
  public void addWorkerTasks(int structureId, int structureCount){
    //ideally this ArrayDeque will store a pair (structureId,location)
    ArrayDeque<Integer> taskQueue = (ArrayDeque<Integer>)myDS.get("workerTasks");
    if(taskQueue == null){
      taskQueue = new ArrayDeque<Integer>();
    }

    for( int i=0; i < structureCount; i++)
      taskQueue.add(structureId);
    
    myDS.put("workerTasks", taskQueue);
  }
  
  @SuppressWarnings("unchecked")
  public void buildNewStructures(){
    //Now we read through the taskQueue for the worker and build the structures that
    //were requested

    ArrayDeque<Integer> taskQueue = (ArrayDeque<Integer>)myDS.get("workerTasks");
    if(taskQueue != null && !taskQueue.isEmpty()){
      ArrayList<GameCommand> lcommandsToDo = new ArrayList<GameCommand>();
      while(!taskQueue.isEmpty()){
        int unitId = taskQueue.pop();
        System.out.println("Building a " + Unit.getUnit(unitId));
        UnitObject worker = (UnitObject)myDS.get("worker");
        MapLocation startingLoc = (MapLocation)myDS.get("startingLoc");

        System.out.println("LOC: starting location -> (" + startingLoc.getX() + "," + startingLoc.getY() + ")");
        //determine the location to place the new building
        MapLocation pos = startingLoc;

        if(startingLoc.getX() > 50){
          if(count == 0){
             pos = startingLoc.distToNewLoc(7,5);
             count++;
          }else if(count == 1){
            pos = startingLoc.distToNewLoc(7,4);
            count++;
          }else if(count == 2){
            pos = startingLoc.distToNewLoc(7,6);
            count++;
          }else if(count == 3){
            pos = startingLoc.distToNewLoc(14,5);
            count++;
          }
        }else{
          if(count == 0){
             pos = startingLoc.distToNewLoc(7,3);
             count++;
          }else if(count == 1){
            pos = startingLoc.distToNewLoc(7,2);
            count++;
          }else if(count == 2){
            pos = startingLoc.distToNewLoc(7,4);
            count++;
          }else if(count == 3){
            pos = startingLoc.distToNewLoc(14,3);
            count++;
          }
        }

        System.out.println("LOC: build location -> (" + pos.getX() + "," + pos.getY() + ")");

        //place the first pylon close to the nexus
        lcommandsToDo.add(GameCommand.build(worker.getID(), pos.getX(), pos.getY(), unitId));
//        lcommandsToDo.add(GameCommand.rightClick(worker.getID(), pos.getX(), pos.getY()));
      }
      requestCommandsExe(lcommandsToDo);
    }
    System.out.println("Finished building orders.");
    //now return the worker
    returnWorker();
  }

  public void requestWorker(){
    boolean requestWorker = (Boolean)myDS.get("RequestWorker");
    if(!requestWorker)
      sendInformFor(ConverId.BuildM.NeedWorker.getConId(), unit_manager, Unit.Protoss_Probe.getNumValue(), 1);
    else{
      System.out.println("###BuildingManagerAgent: already requesting a worker!!!");
    }
  }

  public void returnWorker(){
    System.out.println("Returning the worker");
    UnitObject worker = (UnitObject)myDS.get("worker");
    sendInformFor(ConverId.BuildM.ReturningWorker.getConId(), unit_manager, worker);
    myDS.put("worker",null);
  }

  public void sendInformFor(String request, AID receiver, UnitObject unit){
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    if(request.equals(ConverId.BuildM.ReturningWorker.getConId())){
      msg.setConversationId(ConverId.BuildM.ReturningWorker.getConId());
      try{
        msg.setContentObject(unit);
      }catch(IOException ex){
        Logger.getLogger(BuildingManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    msg.addReceiver(receiver);
    send(msg);
  }
  
  public void sendInformFor(String request, AID receiver, int unitId, int count){
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

    if(request.equals(ConverId.BuildM.NeedWorker.getConId())){
      msg.setConversationId(ConverId.BuildM.NeedWorker.getConId());
      msg.setContent(unitId + ":" + count);
    }

    msg.addReceiver(receiver);
    send(msg);

  }

  /* may have to make this it's own thread */
  public void requestCommandsExe(ArrayList<GameCommand> cmds){
    if(!cmds.isEmpty()){
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setConversationId(ConverId.UnitM.SendCommandsToCommander.getConId());
      try{
        msg.setContentObject((ArrayList<GameCommand>)cmds);
      }catch(Exception e){
        System.out.println("Failed to set message object: " + e.toString());
      }
      msg.addReceiver(unit_manager);
      send(msg);
      System.out.println(this.getLocalName() + "> Commands sent to " + unit_manager.getLocalName());
    }
  }

}//end BuildingManagerAgent

