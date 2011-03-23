
package starcraftbot.proxybot.khasbot.commandera;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

public class CommanderAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = null; //new String [6];
    
  public void setup() {
    System.out.println(getAID().getLocalName() + ": is alive !!!");
    /*
    MessageTemplate mt = MessageTemplate.and(
                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
    */

    //register the SL codec with the content manager
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontology);
    
    
    Object[] args = getArguments();
    System.out.println(getAID().getLocalName() + ": RX " + args.length + " arguments");

    khasbot_agents = new String[args.length];
    //now strip out the khasbot agents that the commander will create from the arguments 
    //passed in
    for(int i=0; i < args.length; i++)
      khasbot_agents[i] = (String)args[i];

    /*
    CommanderAgentCreateAgents agents = new CommanderAgentCreateAgents(khasbot_agents);
    agents.createAgents(this);
  
    addBehaviour(new CommanderAgentResp(this,mt));
    */
  }//end setup

}//end CommanderAgent

