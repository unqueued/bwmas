
package starcraftbot.proxybot.khasbot.resourcema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

@SuppressWarnings("serial")
public class ResourceManagerAgent extends Agent{
	private ContentManager manager = (ContentManager) getContentManager();
	private Codec codec = new SLCodec();
	
	protected void setup(){
    System.out.println(getAID().getLocalName() + ": is alive !!!");

		MessageTemplate mt = MessageTemplate.and(
		  		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
		  		MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );

    /*
    //use the arguments for testing purposes only
    Object[] args = getArguments();
    String arg1 = args[0].toString(); 
    String arg2 = args[1].toString(); 
    String arg3 = args[2].toString(); 


		addBehaviour(new ResourceManagerAgentResp(this, mt));
	  */	
	}
  
}//end ResourceManagerAgent



