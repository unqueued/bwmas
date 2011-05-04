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
public class UnitManagerAgentRespInfCommands extends CyclicBehaviour{
	UnitManagerAgent agent=null;
	DataStore ds = null;
  MessageTemplate mt = null;
  GameCommandQueue msg_cmds = null;

  public UnitManagerAgentRespInfCommands(UnitManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    ds = agent.getDS();
    this.mt = mt;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
      if(msg.getConversationId().equals(ConverId.UnitM.NewCommands.getConId())){
        try{
          System.out.println(agent.getLocalName() + "> NewCommands > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM "
            + msg.getSender().getLocalName() + " FOR " + msg.getConversationId() + "\n\t" + msg.getContentObject());
          ArrayList<GameCommand> lcommandsToDo = (ArrayList<GameCommand>)msg.getContentObject();
          agent.processCmdsReceived(lcommandsToDo);
        }catch(UnreadableException ex){
          Logger.getLogger(UnitManagerAgentRespInfCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }else{
      block();
    }
  }//end action

}//end UnitManagerAgentRespInform

