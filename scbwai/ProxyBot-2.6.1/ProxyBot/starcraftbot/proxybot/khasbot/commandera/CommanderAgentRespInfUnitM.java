package starcraftbot.proxybot.khasbot.commandera;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.GameCommandQueue;

/**
 * This class/behaviour will only process messages it receives from the
 * UnitManager and send them off to ProxyBotAgent
 *
 */
@SuppressWarnings("serial")
public class CommanderAgentRespInfUnitM extends CyclicBehaviour{
  CommanderAgent agent = null;
  MessageTemplate mt = null;
  AID proxybot_agent = null;

  public CommanderAgentRespInfUnitM(CommanderAgent a, MessageTemplate mt, AID proxybot_agent_name){
    super(a);
    agent = a;
    this.mt = mt;
    proxybot_agent = proxybot_agent_name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void action(){
    ACLMessage msg = agent.receive(mt);
    if(msg != null){
//      System.out.println(agent.getLocalName() + "> ??? > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM " +
//          msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
      if(msg.getConversationId().equals(ConverId.Commands.ExecuteCommand.getConId())){
//        System.out.println(agent.getLocalName() + "> ExecuteCommand > " + ACLMessage.getPerformative(msg.getPerformative()) + " FROM " +
//          msg.getSender().getLocalName() + " FOR " + msg.getConversationId());
        
        //OPTIONAL
        //I might want to override it or change it, depending on my overall view of how
        //the game is going.

        //Send the command to ProxyBotAgent
        ACLMessage msg_cmd = new ACLMessage(ACLMessage.INFORM);
        msg_cmd.addReceiver(proxybot_agent);
        msg_cmd.setConversationId(ConverId.Commands.ExecuteCommand.getConId());

        try{
          //The ArrayList<> are broken up here in order to throttle the messages going out.
          //The hope is that the continuous stream of messages will/should prevent Starcraft from
          //waiting for commands from our agent
          msg_cmd.setContentObject((ArrayList<GameCommand>)msg.getContentObject());
          agent.send(msg_cmd);
          
        }catch(UnreadableException ex){
          Logger.getLogger(CommanderAgentRespInfUnitM.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException e){
          System.out.println(agent.getLocalName() + " failed to read message object: " + e.toString());
        }
        
      }
    }else{
      block();
    }

  }//end action
  //OPTIONAL
  //Here I will add method to process the incoming message from
  //UnitManager if I so choose to change or alter the message
}//end CommanderAgentRespInform

