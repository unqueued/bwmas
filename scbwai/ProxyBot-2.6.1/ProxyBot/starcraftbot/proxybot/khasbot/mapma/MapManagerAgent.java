
package starcraftbot.proxybot.khasbot.mapma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

@SuppressWarnings("serial")
public class MapManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    MessageTemplate fipa_request_mt = null;

    //this template will only respond to FIPA_REQUEST messages 
    fipa_request_mt = MessageTemplate.and(
                                 MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                 );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new MapManagerAgentRespFIPARequest(this,fipa_request_mt));

    addBehaviour(root_behaviour);

	}
  
}//end MapManagerAgent



