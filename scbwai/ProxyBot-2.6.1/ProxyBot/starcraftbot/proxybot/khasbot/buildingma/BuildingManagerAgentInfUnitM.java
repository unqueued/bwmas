package starcraftbot.proxybot.khasbot.buildingma;

import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class/behaviour is responsible for handling all the GameObj
 * and GameObjUpdate from Commander.
 */
@SuppressWarnings("serial")
public class BuildingManagerAgentInfUnitM extends CyclicBehaviour{

  BuildingManagerAgent agent = null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public BuildingManagerAgentInfUnitM(BuildingManagerAgent a, MessageTemplate mt, DataStore in_ds){
    super(a);
    agent = a;
    this.mt = mt;
    ds = in_ds;
  }

  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObject.getConId())){
        try{
          agent.setGameObject((GameObject) msg.getContentObject());
        }catch(UnreadableException ex){
          Logger.getLogger(BuildingManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObjectUpdate.getConId())){
        try{
          agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
        }catch(UnreadableException ex){
          Logger.getLogger(BuildingManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.SendWorker.getConId())){
        System.out.println("BuildM received my worker from UnitM");
        try{
          agent.addWorker((UnitObject) msg.getContentObject());
          agent.buildNewStructures();
          ds.put("retaskedWorkerReceived", true);
        }catch(UnreadableException ex){
          Logger.getLogger(BuildingManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Message format defined at ConverId.BuildM.BuildStructure
      }else if(msg.getConversationId().equals(ConverId.BuildM.BuildStructure.getConId())){
        System.out.println("Build a pylon");
        //train a probe
        int unitId = -1;
        int count = 0;

        //parse the message based on format defined at ConverId.BuildM.BuildStructure
        String[] parsedOut = msg.getContent().split(":");

        unitId = Integer.parseInt(parsedOut[0]);
        count = Integer.parseInt(parsedOut[1]);

        //now we check to see if we have a worker at our disposal
        UnitObject worker = (UnitObject) ds.get("worker");
        if(worker == null){
          // request a worker from the UnitManager
          agent.requestWorker();
          agent.addWorkerTasks(unitId,count);
        }else{
          agent.addWorkerTasks(unitId,count);
          agent.buildNewStructures();
        }
      }
    }else{
      block();
    }

  }//end action
}//end BuildingManagerAgentRespInform

