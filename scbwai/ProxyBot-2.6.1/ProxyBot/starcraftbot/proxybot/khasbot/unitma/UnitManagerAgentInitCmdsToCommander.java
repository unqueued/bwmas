package starcraftbot.proxybot.khasbot.unitma;

import jade.core.AID;
import java.io.IOException;

import jade.core.behaviours.*;
import jade.lang.acl.*;
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
  AID commander = null;
  GameCommand msg_cmd = null;

  public UnitManagerAgentInitCmdsToCommander(UnitManagerAgent a, AID commander) {
    super(a);
    agent=a;
    this.commander = commander;
  }

  public void cmdToSend(GameCommand cmd){
    msg_cmd = cmd;
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
      msg.setContentObject((GameCommand) msg_cmd);
    } catch (IOException ex) {
      Logger.getLogger(UnitManagerAgentInitCmdsToCommander.class.getName()).log(Level.SEVERE, null, ex);
    }
   
    agent.send(msg);

	}//end action

  @Override
  public boolean done() {
    return true;
  }

}//end UnitManagerAgentRespInform

