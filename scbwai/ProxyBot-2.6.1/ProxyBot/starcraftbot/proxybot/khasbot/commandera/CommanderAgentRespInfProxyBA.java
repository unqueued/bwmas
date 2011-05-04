package starcraftbot.proxybot.khasbot.commandera;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.game.*;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class CommanderAgentRespInfProxyBA extends CyclicBehaviour{
  CommanderAgent agent = null;
  MessageTemplate mt = null;
  AID unit_manager = null;
  boolean InitGameObj_BuildOrders = false;
  boolean GameObjUpdate_BuildOrders = false;

  int lastUnitCount = 0;

  public CommanderAgentRespInfProxyBA(CommanderAgent a,
          MessageTemplate mt,
          AID unit_m){
    super(a);
    agent = a;
    this.mt = mt;
    unit_manager = unit_m;
  }

  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      //now i'm going to match the conversation to see if its a
      //GameObject or a GameObjectUpdate
      if(msg.getConversationId().equals(ConverId.Game.InitGameObj.getConId())){
        GameObject g = null;
        int totalUnitCount=0;
        //send the initial game object
        ACLMessage msg_gameObj = new ACLMessage(ACLMessage.INFORM);
        msg_gameObj.setConversationId(ConverId.Game.InitGameObj.getConId());
        try{
          g = (GameObject)msg.getContentObject();
          msg_gameObj.setContentObject(msg.getContentObject());
          agent.extractBuildOrders((GameObject) msg.getContentObject());
        }catch(Exception e){
          System.out.println("Failed to serialize the GameObject: " + e.toString());
        }
        msg_gameObj.addReceiver(unit_manager);
        agent.send(msg_gameObj);

        if(g != null){
          Collection<ArrayList<UnitObject>> allUnits = g.getUnitsInGame().getMyPlayersNonStructureUnits().values();
          Collection<ArrayDeque<UnitObject>> allStructures = g.getUnitsInGame().getMyPlayersStructureUnits().values();

          for( ArrayList<UnitObject> x : allUnits)
            totalUnitCount += x.size();
          for( ArrayDeque<UnitObject> x : allStructures)
            totalUnitCount += x.size();

          if(lastUnitCount!=totalUnitCount){

          //if(!InitGameObj_BuildOrders){
            InitGameObj_BuildOrders = true;
            //now send the build orders to unitm
            ACLMessage msg_orders = new ACLMessage(ACLMessage.INFORM);
            msg_orders.setConversationId(ConverId.Commander.BuildOrders.getConId());
            try{
              msg_orders.setContentObject(agent.getBuildOrders());
              //          agent.extractBuildOrders((GameObject)msg.getContentObject());
            }catch(Exception e){
              System.out.println("Failed to serialize the GameObject: " + e.toString());
            }
            msg_orders.addReceiver(unit_manager);
            agent.send(msg_orders);

            System.out.println("Sending out the first build order along side initGameObj");
          //}
            lastUnitCount = totalUnitCount;
          }
        }
      }else if(msg.getConversationId().equals(ConverId.Game.GameObjUpdate.getConId())){
        GameObjectUpdate g = null;
        int totalUnitCount=0;

        ACLMessage msg_gameObjUp = new ACLMessage(ACLMessage.INFORM);
        msg_gameObjUp.setConversationId(ConverId.Game.GameObjUpdate.getConId());
        try{
          g = (GameObjectUpdate)msg.getContentObject();
          msg_gameObjUp.setContentObject(msg.getContentObject());
          agent.updatedBuildOrders((GameObjectUpdate) msg.getContentObject());
        }catch(Exception e){
          System.out.println("Failed to serialize the GameObjectUpdate: " + e.toString());
        }
        msg_gameObjUp.addReceiver(unit_manager);
        agent.send(msg_gameObjUp);

        if(g != null){
          Collection<ArrayList<UnitObject>> allUnits = g.getUnitsInGame().getMyPlayersNonStructureUnits().values();
          Collection<ArrayDeque<UnitObject>> allStructures = g.getUnitsInGame().getMyPlayersStructureUnits().values();

          for( ArrayList<UnitObject> x : allUnits)
            totalUnitCount += x.size();
          for( ArrayDeque<UnitObject> x : allStructures)
            totalUnitCount += x.size();

          if(lastUnitCount!=totalUnitCount ){

        //if(!GameObjUpdate_BuildOrders){
          GameObjUpdate_BuildOrders = true;
          //now send the build orders to unitm
          ACLMessage msg_orders = new ACLMessage(ACLMessage.INFORM);
          msg_orders.setConversationId(ConverId.Commander.BuildOrders.getConId());
          try{
            msg_orders.setContentObject(agent.getBuildOrders());
            //          agent.updatedBuildOrders((GameObjectUpdate)msg.getContentObject());
          }catch(Exception e){
            System.out.println("Failed to serialize the GameObject: " + e.toString());
          }
          msg_orders.addReceiver(unit_manager);
          agent.send(msg_orders);
        //}
          lastUnitCount = totalUnitCount;
          }
        }


      }
    }else{
      block();
    }
  }//end action
}//end CommanderAgentRespInform

