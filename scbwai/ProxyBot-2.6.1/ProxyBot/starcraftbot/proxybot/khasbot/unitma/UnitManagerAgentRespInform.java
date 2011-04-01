package starcraftbot.proxybot.khasbot.unitma;

import java.io.IOException;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.ParseACLMessage;

@SuppressWarnings("serial")
public class UnitManagerAgentRespInform extends CyclicBehaviour{
	UnitManagerAgent agent=null;	
  MessageTemplate mt = null;

  public UnitManagerAgentRespInform(UnitManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
  }

  public void action() {
	    //process only the Inform messages
	    ACLMessage msg = agent.receive(mt);
	    while(agent.receive(mt) != null){System.out.println(agent.getLocalName() + " is purging queue!");}
	   // System.out.println(agent.getLocalName()+" action called, have a msg...");
	    if (msg != null) {
	      //System.out.println(agent.getLocalName() + ": MSG not null.");// : " + msg.getContent() ); 
	      if (msg.getPerformative() == ACLMessage.INFORM) {
	        //System.out.println(agent.getLocalName() + ": MSG INFORM : " + msg.getContent() ); 
	        
	        //handle the messages that come from CommanderAgent which will be the game object 
	        if(ParseACLMessage.isSenderCommander(msg)) {
	          //
	          //process the game update that was received
	          //
	        	//System.out.println("here.");
	          try {
	            //System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + ((GameObject)(msg.getContentObject())).toString());
	            //System.out.flush();
				agent.setGameObject((GameObject) msg.getContentObject());
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			//System.out.println("defaultActions call!");
			//System.out.flush();
			//wait for updates from Managers
	  		//  Right now do some 'default actions'
			agent.DefaultActions();
	  		
			//System.out.println(agent.getLocalName() + "$ Done with setting up commands, sending reply to " + msg.getSender().getLocalName());
			//System.out.flush();
			
	  		//send "new" game object back to commander.
	  		ACLMessage response = msg.createReply();
	  		response.setPerformative(ACLMessage.INFORM);
	  		try {
				response.setContentObject(agent.getGameObject());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		agent.send(response);
	  		
	  		msg.reset();
	  		response.reset();
			
	        }
	      }
	    } else {
	      block();
	    }

	  }//end action

}//end UnitManagerAgentRespInform

