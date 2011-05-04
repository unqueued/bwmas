package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.UnitLastCommand;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class ResourceManagerAgentInfUnitM extends CyclicBehaviour{

  ResourceManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;

  public ResourceManagerAgentInfUnitM(ResourceManagerAgent a, MessageTemplate mt){
    super(a);
    this.agent = a;
    this.mt = mt;
    ds = agent.getDS();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null && msg.getConversationId() != null){
      if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObject.getConId())){
        try{
          GameObject gObj = (GameObject) msg.getContentObject();
          if(gObj != null){
            agent.parseGameObject(gObj);
          }
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.ResM.NeedWorker.getConId())){
        try{
          UnitObject unit = (UnitObject) msg.getContentObject();
          if(unit != null){
            agent.addWorker(unit);
          }
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.RetaskWorker.getConId())){
        System.out.println("I've been asked to retask a worker");

        int unitId = Integer.parseInt(msg.getContent());
        GameCommand last_cmd = agent.retaskWorker(unitId);

        //now send the last command to unitm so that it store it
        ACLMessage msg_out = new ACLMessage(ACLMessage.INFORM);
        msg_out.setConversationId(ConverId.UnitM.UnitLastCommand.getConId());
        try{
          msg_out.setContentObject(new UnitLastCommand(unitId, last_cmd));
        }catch(Exception e){
          System.out.println("Failed to set message object: " + e.toString());
        }
        msg_out.addReceiver(msg.getSender());
        agent.send(msg_out);
      }else if(msg.getConversationId().equals(ConverId.UnitM.SendWorker.getConId())){
        try{
          UnitObject unit = (UnitObject) msg.getContentObject();
          if(unit != null){
            agent.addWorker(unit);
          }
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.SendWorkerToMine.getConId())){
        ArrayList<UnitObject> my_units = (ArrayList<UnitObject>) ds.get("my_units");
        ArrayList<UnitObject> minerals = (ArrayList<UnitObject>) ds.get("minerals");
        HashMap<Integer, GameCommand> unit_last_cmd = (HashMap<Integer, GameCommand>) ds.get("unit_last_cmd");
        UnitObject unit = null;

        try{
          unit = (UnitObject) msg.getContentObject();
          if(unit != null){
            agent.addWorker(unit);
          }
        }catch(UnreadableException ex){
          Logger.getLogger(ResourceManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<GameCommand> lcommandsToDo = new ArrayList<GameCommand>();

        UnitObject mineralPatch = minerals.get(my_units.size() / 3);

        //assign only 3 workers per mineral patch
        lcommandsToDo.add(GameCommand.rightClickUnit(unit.getID(), mineralPatch.getID()));
        //store the last command that the unit executed
        unit_last_cmd.put(unit.getID(), GameCommand.rightClickUnit(unit.getID(), mineralPatch.getID()));

        agent.requestCommandsExe(lcommandsToDo);

      }
    }else{
      block();
    }
  }//end action
}//end ResourceManagerAgentRespInfCmd

