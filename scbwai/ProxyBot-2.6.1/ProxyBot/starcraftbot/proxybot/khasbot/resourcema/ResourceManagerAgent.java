
package starcraftbot.proxybot.khasbot.resourcema;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.mapma.MapManagerAgentRespInform;
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
public class ResourceManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	private GameObject game;
	
	protected void setup(){
    //DEBUG
    //System.out.println(getAID().getLocalName() + ": is alive !!!");

    MessageTemplate fipa_request_mt = null;
    MessageTemplate inform_mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    //this template will only respond to FIPA_REQUEST messages 
    fipa_request_mt = MessageTemplate.and(
                                 MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                 );

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new ResourceManagerAgentRespFIPARequest(this,fipa_request_mt));
    root_behaviour.addSubBehaviour(new ResourceManagerAgentRespInform(this,inform_mt));
    addBehaviour(root_behaviour);

	}

	public void setGameObject(GameObject game) {
		this.game = game;
	}

	public GameObject getGameObject() {
		return game;
	}
  
}//end ResourceManagerAgent



