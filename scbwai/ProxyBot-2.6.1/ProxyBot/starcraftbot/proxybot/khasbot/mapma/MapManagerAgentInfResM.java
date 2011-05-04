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
public class MapManagerAgentInfResM extends CyclicBehaviour{

  MapManagerAgent agent = null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public MapManagerAgentInfResM(MapManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
  }

  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      ACLMessage msg_out = new ACLMessage(ACLMessage.INFORM);
      
      if(msg.getConversationId().equals(ConverId.MapM.NearestMinerals.getConId())){
//        System.out.println("Received NearestMinerals inform");
        ArrayList<UnitObject> minerals = agent.NearestMinerals();

        if(minerals != null){
//          System.out.println("Sending NearestMinerals to Resm");
          msg_out.setConversationId(ConverId.MapM.NearestMineralsSuccess.getConId());
          msg_out.addReceiver(msg.getSender());
          try{
            msg_out.setContentObject(minerals);
          }catch(IOException ex){
            Logger.getLogger(MapManagerAgentInfResM.class.getName()).log(Level.SEVERE, null, ex);
          }
        }else{
          msg_out.setConversationId(ConverId.MapM.NearestMineralsFailure.getConId());
          msg_out.addReceiver(msg.getSender());
        }
        agent.send(msg_out);
      }else if(msg.getConversationId().equals(ConverId.MapM.NearestGas.getConId())){
        if(agent.NearestGas() != null){
          msg_out.setConversationId(ConverId.MapM.NearestGasSuccess.getConId());
          try{
            msg_out.setContentObject(agent.NearestGas());
          }catch(IOException ex){
            Logger.getLogger(MapManagerAgentInfResM.class.getName()).log(Level.SEVERE, null, ex);
          }
        }else{
          msg_out.setConversationId(ConverId.MapM.NearestGasFailure.getConId());
        }
      }
      
    }else{
      block();
    }
  }//end action
}//end MapManagerAgentRespInform

