
package starcraftbot.proxybot.khasbot.commandera;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import java.lang.reflect.*;

public class CommanderAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = null; // this stores> agent_name;class_name
  String [] khasbot_agent_names = null; //this stores> agent_name
  String [] game_update_agents = null; //this stores> agent_names that get game updates

  public void setup() {
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    //
    //Message Templates
    //
  
    MessageTemplate inform_mt = null;
    MessageTemplate fipa_request_mt = null; 

    //register the SL codec with the content manager
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontology);
   
    //arguments passed into agent
    Object[] args = getArguments();
    
    //ReadyToGo commander_ready = (ReadyToGo) args[0];
    
    //notify Client that the object is up and ready to go
    //commander_ready.signal(); 

    //System.out.println(getAID().getLocalName() + ": RX " + args.length + " arguments");

    khasbot_agents = new String[args.length];
    khasbot_agent_names = new String[args.length];
    game_update_agents = new String[2]; //HARDCODE: predefined agents that will get game updates
    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    for(int i=0, j=0; i < args.length; i++){
      khasbot_agents[i] = (String)args[i];
      khasbot_agent_names[i] = (String)Array.get(khasbot_agents[i].split(";"),0);

      //add the agents that will get game updates
      if(khasbot_agent_names[i].matches(".*[Bb]uilding[Mm]anager.*"))
        game_update_agents[j++] = khasbot_agent_names[i];
      else if(khasbot_agent_names[i].matches(".*[Uu]nit[Mm]anager.*"))
        game_update_agents[j++] = khasbot_agent_names[i];
    } 
 
    //this template will only respond to INFORM messages 
    inform_mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    //this template will only respond to FIPA_REQUEST messages 
    fipa_request_mt = MessageTemplate.and(
                                 MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                 );

    //This call will create all of the other agents that will be needed
    CommanderAgentCreateAgents agents = new CommanderAgentCreateAgents(khasbot_agents);
    agents.createAgents(this);

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
    
    root_behaviour.addSubBehaviour(new CommanderAgentRespInform(this,inform_mt,game_update_agents));
    root_behaviour.addSubBehaviour(new CommanderAgentRespFIPARequest(this,fipa_request_mt));

    addBehaviour(root_behaviour);

  }//end setup

}//end CommanderAgent

