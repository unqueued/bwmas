package starcraftbot.proxybot.khasbot.structurema;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.logging.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.unitma.Unit;

@SuppressWarnings("serial")
public class StructureManagerAgentInfUnitM extends CyclicBehaviour{

  StructureManagerAgent agent = null;
  MessageTemplate mt = null;
  DataStore ds = null;

  public StructureManagerAgentInfUnitM(StructureManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
  }

  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null && msg.getConversationId() != null){
      if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObject.getConId())){
        try{
          agent.setGameObject((GameObject) msg.getContentObject());
        }catch(UnreadableException ex){
          Logger.getLogger(StructureManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.NeedGameObjectUpdate.getConId())){
        try{
          agent.setGameObjectUpdate((GameObjectUpdate) msg.getContentObject());
        }catch(UnreadableException ex){
          Logger.getLogger(StructureManagerAgentInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }
      //Message format defined at ConverId.StructM.TrainNewUnit
      }else if(msg.getConversationId().equals(ConverId.StructM.TrainNewUnit.getConId())){
        System.out.println("Train probe");
        //train a probe
        boolean trainUnit = false;        
        int unitId = -1;
        int count = 0;

        //parse the message based on format defined at ConverId.StructM.TrainNewUnit
        String []parsedOut = msg.getContent().split(":");

        unitId = Integer.parseInt(parsedOut[0]);
        count = Integer.parseInt(parsedOut[1]);

        //see if the units can be trained
        trainUnit = agent.canTrainNewUnit(Unit.getUnit(unitId));

        if(trainUnit){
          //train the actual unit
          agent.trainNewUnit(Unit.getUnit(unitId));
        }else{
          
        }
      }
    }else{
      block();
    }
  }//end action
}//end StructureManagerAgentRespInfCmd

