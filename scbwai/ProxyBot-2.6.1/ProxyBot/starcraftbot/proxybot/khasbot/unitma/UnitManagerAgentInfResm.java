/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.UnitLastCommand;


@SuppressWarnings("serial")
public class UnitManagerAgentInfResm extends CyclicBehaviour{

  UnitManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;

  public UnitManagerAgentInfResm(UnitManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null && msg.getConversationId() != null){
      ACLMessage msg_out = new ACLMessage(ACLMessage.INFORM);
      if(msg.getConversationId().equals(ConverId.ResM.NeedWorker.getConId())){
        UnitObject unit = agent.WorkerAvailable();
        if(unit != null){
//          System.out.println(this.agent.getLocalName() + ": avaiable worker ... unit is:" + unit);
          msg_out.addReceiver(msg.getSender());
          msg_out.setConversationId(msg.getConversationId());
          try{
            msg_out.setContentObject(unit);
          }catch(IOException ex){
            Logger.getLogger(UnitManagerAgentInfResm.class.getName()).log(Level.SEVERE, null, ex);
          }
          agent.send(msg_out);
        }else{

        }
      }else if(msg.getConversationId().equals(ConverId.UnitM.UnitLastCommand.getConId())){
//        HashMap<Integer,GameCommand> unit_last_cmd = (HashMap<Integer,GameCommand>)ds.get("unit_last_cmd");
//        UnitLastCommand input = null;
//        try{
//          input = (UnitLastCommand) msg.getContentObject();
//        }catch(UnreadableException ex){
//          Logger.getLogger(UnitManagerAgentInfResm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        if(input != null){
//          unit_last_cmd.put(input.getUnitId(), input.getCommand());
//        }
//        ds.put("unit_last_cmd",unit_last_cmd);
      }
    }else{
      block();
    }
  }//end action
  
}
