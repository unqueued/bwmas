package starcraftbot.proxybot.khasbot.unitma;

import jade.core.AID;
import java.io.IOException;

import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.ConverId;

/**
 * This behaviour will forward the command requests it needs to the commander so that
 * it can process them.
 * @author Antonio Arredondo
 */
@SuppressWarnings("serial")
public class UnitManagerAgentInitCmdsToCommander extends SimpleBehaviour{
	UnitManagerAgent agent=null;
	DataStore ds = null;
  AID commander = null;
  ArrayList<GameCommand> msg_cmds = null;

  public UnitManagerAgentInitCmdsToCommander(UnitManagerAgent a, AID commander) {
    super(a);
    agent=a;
    ds = agent.getDS();
    this.commander = commander;
  }

  public void cmdToSend(ArrayList<GameCommand> cmds){
    msg_cmds = cmds;
  }

  /*
   *
   */
  @Override
  public void action() {
    //System.out.println(agent.getLocalName() + " sending commands to commander");
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    msg.addReceiver(commander);
    msg.setConversationId(ConverId.Commands.ExecuteCommand.getConId());

    try {
      if(msg_cmds != null){
        msg.setContentObject((ArrayList<GameCommand>)msg_cmds);
        agent.send(msg);
      }
    } catch (IOException ex) {
      Logger.getLogger(UnitManagerAgentInitCmdsToCommander.class.getName()).log(Level.SEVERE, null, ex);
    }
    
	}//end action

  @Override
  public boolean done() {
    System.out.println(agent.getLocalName() + " finished sending commands to Commander");
    return true;
  }

}//end UnitManagerAgentRespInform

