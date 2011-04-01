package starcraftbot.proxybot.khasbot.buildingma;

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
public class BuildingManagerAgentRespInform extends CyclicBehaviour{
	BuildingManagerAgent agent=null;	
  MessageTemplate mt = null;

  public BuildingManagerAgentRespInform(BuildingManagerAgent a, MessageTemplate mt) {
    super(a);
    agent=a;
    this.mt=mt;
  }

  public void action() {
    //process only the Inform messages
    ACLMessage msg = agent.receive(mt);
    if (msg != null) {
      //System.out.println(agent.getLocalName() + ": MSG RX : " + msg.getContent() ); 
      if (msg.getPerformative() == ACLMessage.INFORM) {
        //System.out.println(agent.getLocalName() + ": MSG INFORM : " + msg.getContent() ); 
        
        //handle the messages that come from CommanderAgent which will be the game object 
        if(ParseACLMessage.isSenderCommander(msg)) {


          //
          //process the game update that was received
          //
          
          try {
            //DEBUG
            //System.out.println(agent.getLocalName() + "$ INFORM RX from " + msg.getSender().getLocalName() + " Action: " + ((GameObject)(msg.getContentObject())).toString());
			agent.setGameObject((GameObject) msg.getContentObject());
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		msg.reset();
		
        }
      }
    } else {
      block();
    }

  }//end action

}//end BuildingManagerAgentRespInform

