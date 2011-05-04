package starcraftbot.proxybot.khasbot.mapma;

import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.io.IOException;
import java.util.ArrayList;

import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class MapManagerAgentInfUnitM extends CyclicBehaviour{

  MapManagerAgent agent = null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public MapManagerAgentInfUnitM(MapManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
  }

  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObject.getConId())){
//        System.out.println("Received NeedGameObject inform");
        GameObject gObj = null;
        try{
          gObj = (GameObject) msg.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(MapManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(gObj != null){
          agent.parseGameObject(gObj);
        }
      }
    }else{
      block();
    }
  }//end action
}//end MapManagerAgentRespInform

