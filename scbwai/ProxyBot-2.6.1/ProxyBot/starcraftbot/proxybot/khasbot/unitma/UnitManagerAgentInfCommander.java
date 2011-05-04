package starcraftbot.proxybot.khasbot.unitma;

import jade.core.AID;
import java.io.IOException;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.GameCommandQueue;
import starcraftbot.proxybot.ConverId;

/**
 * This behaviour will forward the command requests it needs to the commander so that
 * it can process them.
 * @author Antonio Arredondo
 */
@SuppressWarnings("serial")
public class UnitManagerAgentInfCommander extends CyclicBehaviour{

  UnitManagerAgent agent = null;
  DataStore ds = null;
  MessageTemplate mt = null;
  AID commander = null;

  public UnitManagerAgentInfCommander(UnitManagerAgent in_a, MessageTemplate in_mt, DataStore in_ds, AID in_commander){
    super(in_a);
    agent = in_a;
    mt = in_mt;
    ds = in_ds;
    commander = in_commander;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void action(){
//    ArrayList<GameCommand> lcommandsToDo = new ArrayList<GameCommand>();
//    for(int i = 0; i <= agent.getCurQueueSize(); i++){
//      ACLMessage msg = agent.receive(mt);
//      if(msg != null && msg.getConversationId() != null){
//        System.out.println("UnitManagerAgent NewCommands Messages Queued: " + i + " of " + agent.getCurQueueSize());
//        if(msg.getConversationId().equals(ConverId.UnitM.SendCommandsToCommander.getConId())){
//          System.out.println(agent.getLocalName() + "> NewCommands > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM "
//                  + msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
//          try{
//            ArrayList<GameCommand> temp = (ArrayList<GameCommand>) msg.getContentObject();
//            lcommandsToDo.addAll(temp);
////            for(GameCommand cmd : lcommandsToDo)
////              System.out.println(cmd.toString());
//          }catch(UnreadableException ex){
//            Logger.getLogger(UnitManagerAgentInfCommander.class.getName()).log(Level.SEVERE, null, ex);
//          }
//        }
//      }
//    }
//
//    for(int i = 0; i < lcommandsToDo.size(); i++){
//      System.out.println("ToCommander " + i + " of " + (lcommandsToDo.size()-1) + " " + lcommandsToDo.get(i));
//    }
//
//    if(!lcommandsToDo.isEmpty()){
//      agent.processCmdsReceived(lcommandsToDo);
//    }
//
//    block();


//    ACLMessage msg = null;
//    do{
//      msg = agent.receive(mt);
//      if(msg != null && msg.getConversationId() != null){
//        System.out.println("UnitManagerAgent NewCommands QueueSize: " + agent.getCurQueueSize());
//        if(msg.getConversationId().equals(ConverId.UnitM.SendCommandsToCommander.getConId())){
//          System.out.println(agent.getLocalName() + "> NewCommands > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM "
//            + msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
//          try{
//            ArrayList<GameCommand> lcommandsToDo = (ArrayList<GameCommand>)msg.getContentObject();
//            for(GameCommand cmd : lcommandsToDo)
//              System.out.println(cmd.toString());
//            agent.processCmdsReceived(lcommandsToDo);
//          }catch(UnreadableException ex){
//            Logger.getLogger(UnitManagerAgentInfCommander.class.getName()).log(Level.SEVERE, null, ex);
//          }
//        }
//      }else{
//        block();
//        continue;
//      }
//    }while(msg != null);


//    ACLMessage msg = agent.receive(mt);
//    if(msg != null && msg.getConversationId() != null){
//      System.out.println("UnitManagerAgent NewCommands QueueSize: " + agent.getCurQueueSize());
//      if(msg.getConversationId().equals(ConverId.UnitM.SendCommandsToCommander.getConId())){
//        System.out.println(agent.getLocalName() + "> NewCommands > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM "
//          + msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
//        try{
//          ArrayList<GameCommand> lcommandsToDo = (ArrayList<GameCommand>)msg.getContentObject();
////          for(GameCommand cmd : lcommandsToDo)
////            System.out.println(cmd.toString());
//          agent.processCmdsReceived(lcommandsToDo);
//        }catch(UnreadableException ex){
//          Logger.getLogger(UnitManagerAgentInfCommander.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
//    }else{
//      block();
//    }

    ACLMessage msg = agent.receive(mt);
    if(msg != null && msg.getConversationId() != null){
      ACLMessage msg_out = new ACLMessage(ACLMessage.INFORM);

      msg_out.addReceiver(commander);
      msg_out.setConversationId(ConverId.Commands.ExecuteCommand.getConId());

      try{
        msg_out.setContentObject(msg.getContentObject());
      }catch(IOException ex){
        Logger.getLogger(UnitManagerAgentInfCommander.class.getName()).log(Level.SEVERE, null, ex);
      }catch(UnreadableException ex){
        Logger.getLogger(UnitManagerAgentInfCommander.class.getName()).log(Level.SEVERE, null, ex);
      }
      agent.send(msg_out);
    }

  }//end action
}//end UnitManagerAgentRespInform

