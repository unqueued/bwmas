package starcraftbot.proxybot.khasbot.commandera;



import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.io.*;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;

/**
 * This class/behaviour will only process messages it receives from the
 * UnitManager and send them off to ProxyBotAgent
 *
 */
@SuppressWarnings("serial")
public class CommanderAgentRespInfUnitM extends CyclicBehaviour{
	Agent agent=null;	
  MessageTemplate mt = null;
  AID proxybot_agent = null;

  public CommanderAgentRespInfUnitM(Agent a, MessageTemplate mt, AID proxybot_agent_name) {
    super(a);
    agent=a;
    this.mt=mt;
    proxybot_agent =  proxybot_agent_name;
  }

  public void action() {
    //process only the message based on the message template 
    ACLMessage msg = agent.receive(mt);
    if (msg != null){
      if( msg.getConversationId().equals(ConverId.Commands.ExecuteCommand.getConId())){
        //The Unit Manager wants me to execute a command for it

        //OPTIONAL
        //I might want to override it or change it, depending on my overall view of how
        //the game is going.
        try{
          if( msg.getContentObject() != null){
            //Send the command to ProxyBotAgent
            ACLMessage msg_cmd = new ACLMessage(ACLMessage.INFORM);
            msg_cmd.addReceiver(proxybot_agent);
            msg_cmd.setConversationId(ConverId.Commands.ExecuteCommand.getConId());
            msg_cmd.setContentObject(msg.getContentObject());
            agent.send(msg_cmd);
            msg_cmd.reset();
          }
        }catch(UnreadableException ex) {
          Logger.getLogger(CommanderAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	msg.reset();
        }
      }
    } else {
      block();
    }

  }//end action

  //OPTIONAL
  //Here I will add method to process the incoming message from
  //UnitManager if I so choose to change or alter the message
}//end CommanderAgentRespInform

