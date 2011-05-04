/**
 * 
 */
package starcraftbot.proxybot.khasbot.unitma;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;


@SuppressWarnings("serial")
public class UnitManagerAgentInfBuildm extends CyclicBehaviour{

  UnitManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;
  AID resource_manager = null;

  public UnitManagerAgentInfBuildm(UnitManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
  }

  public void setResMAID(AID resm){
    resource_manager = resm;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null && msg.getConversationId() != null){
      ACLMessage msg_out = new ACLMessage(ACLMessage.INFORM);
      //parse the message based on format defined at ConverId.BuildM.NeedWorker
      if(msg.getConversationId().equals(ConverId.BuildM.NeedWorker.getConId())){
        //here I will retask a worker (one that is controlled by ResourceManager)
        int unitId = -1;
        int count = 0;
        UnitObject retaskedUnit = null;

        //parse the message based on format defined at ConverId.BuildM.NeedWorker
        String []parsedOut = msg.getContent().split(":");

        unitId = Integer.parseInt(parsedOut[0]);
        count = Integer.parseInt(parsedOut[1]);

        //retask the worker
        if(agent.retaskWorker()){
          retaskedUnit = (UnitObject)ds.get("retaskWorker");
        }else{
          //maybe we create one here
        }

        System.out.println("Sending retasked worker to BuildM");
        //now send the retasked worker to Building Manager
        msg_out.addReceiver(msg.getSender());
        msg_out.setConversationId(ConverId.UnitM.SendWorker.getConId());

        try{
          msg_out.setContentObject(retaskedUnit);
        }catch(IOException ex){
          Logger.getLogger(UnitManagerAgentInfBuildm.class.getName()).log(Level.SEVERE, null, ex);
        }
        agent.send(msg_out);
      }else if(msg.getConversationId().equals(ConverId.BuildM.ReturningWorker.getConId())){
        System.out.println("UnitM received a Returning Worker from BuildM");
        HashMap<Integer,GameCommand> unit_last_cmd = (HashMap<Integer,GameCommand>)ds.get("unit_last_cmd");
        List<ArrayList<UnitObject>> workerUnits = (List<ArrayList<UnitObject>>)ds.get("workerUnits");
        ArrayList<UnitObject> workerUnitsWorking = workerUnits.get(1);

        if(unit_last_cmd == null){
          unit_last_cmd = new HashMap<Integer,GameCommand>();
        }
        
        UnitObject unit = null;
        
        try{
          unit = (UnitObject)msg.getContentObject();
        }catch(UnreadableException ex){
          Logger.getLogger(UnitManagerAgentInfBuildm.class.getName()).log(Level.SEVERE, null, ex);
        }

        //1)place the worker on the working list
        workerUnitsWorking.add(unit);
        workerUnits.set(1, workerUnitsWorking);
        ds.put("workerUnits", workerUnits);

        //2)send the worker back into UnitM

        //2a) first check to see if the GameCommand is null
        ArrayList<GameCommand> cmds = new ArrayList<GameCommand>();
        GameCommand last_command = unit_last_cmd.get(unit.getID());

        if(last_command == null){
          msg_out.setConversationId(ConverId.UnitM.SendWorkerToMine.getConId());
        }else{
          msg_out.setConversationId(ConverId.UnitM.SendWorker.getConId());
        }
        
        msg_out.addReceiver(resource_manager);
        
        try{
          msg_out.setContentObject(unit);
        }catch(IOException ex){
          Logger.getLogger(UnitManagerAgentInfBuildm.class.getName()).log(Level.SEVERE, null, ex);
        }
        agent.send(msg_out);

        //send the last command
 
        if(last_command == null){

        }else{
          cmds.add(last_command);
        }
        //agent.processCmdsReceived(cmds);

        unit_last_cmd.remove(unit.getID());
        ds.put("unit_last_cmd", unit_last_cmd);
      }
    }else{
      block();
    }
  }//end action
  
}
