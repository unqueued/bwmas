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
public class UnitManagerAgentInitCmdsToCommander extends SimpleBehaviour{
	UnitManagerAgent agent=null;
	DataStore ds = null;
  AID commander = null;
  GameCommandQueue msg_cmds = null;

  public UnitManagerAgentInitCmdsToCommander(UnitManagerAgent a, AID commander) {
    super(a);
    this.agent=a;
    this.ds = this.agent.getDS();
    this.commander = commander;
  }

  public void cmdToSend(GameCommandQueue cmds){
    this.msg_cmds = cmds;
  }

  /*
   *
   */
  public void action() {
    //send command message back to the commander
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    msg.addReceiver(commander);
    msg.setConversationId(ConverId.Commands.ExecuteCommand.getConId());

    try {
      msg.setContentObject(this.msg_cmds);
    } catch (IOException ex) {
      Logger.getLogger(UnitManagerAgentInitCmdsToCommander.class.getName()).log(Level.SEVERE, null, ex);
    }
   
    this.agent.send(msg);

	}//end action

  @Override
  public boolean done() {
    return true;
  }

}//end UnitManagerAgentRespInform

