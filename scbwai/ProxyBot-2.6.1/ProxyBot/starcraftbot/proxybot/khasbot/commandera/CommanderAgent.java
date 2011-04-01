
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
  AID [] game_update_agents = null; //this stores> agent_names that get game updates

  AID proxybotagent = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;
  AID unit_manager = null;

  public void setup() {
    System.out.println(getAID().getLocalName() + ": is alive !!!");

    //
    //Message Templates
    //
  
    MessageTemplate proxybotagent_inform_mt = null;
    MessageTemplate unitmanager_inform_mt = null;
    MessageTemplate fipa_request_mt = null; 

    //register the SL codec with the content manager
    getContentManager().registerLanguage(codec);
    getContentManager().registerOntology(ontology);
   
    //arguments passed into agent
    Object[] args = getArguments();
    
    //System.out.println(getAID().getLocalName() + ": RX " + args.length + " arguments");
    
    //set the proxy bot agent name, this index is HARDCODE
    String temp = (String)args[0];
    proxybotagent = new AID(temp,AID.ISLOCALNAME);

    khasbot_agents = new String[args.length-1];
    khasbot_agent_names = new String[args.length-1];
    game_update_agents = new AID[6]; //HARDCODE: predefined agents that will get game updates
    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    //NOTE: skip index 0 since this is the name of the proxy bot agent
    for(int i=1, j=0, k=0; i < args.length; i++, j++){
      khasbot_agents[j] = (String)args[i];
      khasbot_agent_names[j] = (String)Array.get(khasbot_agents[j].split(";"),0);

      //add the agents that will get game updates
      if(khasbot_agent_names[j].matches(".*[Bb]uilding[Mm]anager.*")){
        building_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        game_update_agents[k++] = building_manager;
      }
      else if(khasbot_agent_names[j].matches(".*[Uu]nit[Mm]anager.*")){
        unit_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        game_update_agents[k++] = unit_manager;
      }
      else if(khasbot_agent_names[j].matches(".*[Ss]tructure[Mm]anager.*"))
      {
        structure_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        game_update_agents[k++] = structure_manager;
      }
      else if(khasbot_agent_names[j].matches(".*[Bb]attle[Mm]anager.*"))
      {
    	  battle_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  game_update_agents[k++] = battle_manager;
      }
      else if(khasbot_agent_names[j].matches(".*[Rr]esource[Mm]anager.*"))
      {
    	  resource_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  game_update_agents[k++] = resource_manager;
      }
      else if(khasbot_agent_names[j].matches(".*[Mm]ap[Mm]anager.*"))
      {
    	  map_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  game_update_agents[k++] = map_manager;
      }
    } 

    //this template will only respond to INFORM messages 
    proxybotagent_inform_mt = /*MessageTemplate.and(*/
                                                MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                                                /*MessageTemplate.MatchSender(proxybotagent)
                                                );*/

    unitmanager_inform_mt = MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                                MessageTemplate.MatchSender(unit_manager)
                                                );

    //this template will only respond to FIPA_REQUEST messages 
    fipa_request_mt = MessageTemplate.and(
                                 MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                 MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                 );

    //This call will create all of the other agents that will be needed
    CommanderAgentCreateAgents agents = new CommanderAgentCreateAgents(khasbot_agents);
    agents.createAgents(this,khasbot_agent_names);

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
   
    //send game updates to the agents in the game_update_agents array
    root_behaviour.addSubBehaviour(new CommanderAgentRespInform(this,proxybotagent_inform_mt,game_update_agents));
    //root_behaviour.addSubBehaviour(new CommanderAgentRespInformProxyBotAgent(this,proxybotagent_inform_mt,game_update_agents));
    //root_behaviour.addSubBehaviour(new CommanderAgentRespInformUnitManager(this,unitmanager_inform_mt));
    root_behaviour.addSubBehaviour(new CommanderAgentRespFIPARequest(this,fipa_request_mt));


    
    /* now create the object that will be issuing orders */
    //CommanderAgentOrders issue_orders = new CommanderAgentOrders(this,root_behaviour);
    
    addBehaviour(root_behaviour);

  }//end setup

}//end CommanderAgent

