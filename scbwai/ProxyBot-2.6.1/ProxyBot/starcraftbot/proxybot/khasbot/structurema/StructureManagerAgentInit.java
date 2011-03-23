/**
 * Agent Description:
 * 	Controls all the structure Units, this includes training new units and researching tech
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 *  <-> UnitManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 *  @see UnitManagerAgent
 */
package starcraftbot.proxybot.khasbot.structurema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

@SuppressWarnings("serial")
public class StructureManagerAgentInit extends AchieveREInitiator{
  
  public StructureManagerAgentInit(Agent a, ACLMessage msg) {
    super(a, msg);
  }
  protected void handleInform(ACLMessage inform){
    System.out.println("Agent responded!!!");
  }
  protected void handleRefuse(ACLMessage refuse) {
    System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
  }
  protected void handleFailure(ACLMessage failure){
    System.out.println("Failed to get response!!!");
  }
/*
  protected void handleAllResultNotifications(Vector notifications) {
    if (notifications.size() < nResponders) {
      //Some responder didn't reply within the specified timeout
      System.out.println("Timeout expired: missing " + (notifications.size()) + " responses");
    }
  }
*/
}//end StructureManagerAgentInit


