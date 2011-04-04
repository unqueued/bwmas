
package starcraftbot.proxybot.khasbot.commandera;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;

import java.lang.reflect.*;

public class CommanderAgent extends Agent{
  private Codec codec = new SLCodec();
  private Ontology ontology = JADEManagementOntology.getInstance();

  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = null; // this stores> agent_name;class_name
  String [] khasbot_agent_names = null; //this stores> agent_name
  AID [] init_game_agents = null; //this stores> agent_names that get initial game obj
  AID [] game_update_agents = null; //this stores> agent_names that get game obj updates

  /* khasbot agent names */
  AID proxybotagent = null;
  AID building_manager = null;
  AID structure_manager = null;
  AID battle_manager = null;
  AID resource_manager = null;
  AID map_manager = null;
  AID unit_manager = null;

  /*
   * Commander agent specifically
   */
  AID[] CAHelpers = null;

  /* Commander Agent Helper agent names */
  AID CAH_build_order = null;


  public void setup() {
    //DEBUG
    //System.out.println(getAID().getLocalName() + ": is alive !!!");

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
    
    //set the proxy bot agent name, this index is HARDCODE
    String temp = (String)args[0];
    proxybotagent = new AID(temp,AID.ISLOCALNAME);

    khasbot_agents = new String[args.length-1];
    khasbot_agent_names = new String[args.length-1];
    init_game_agents = new AID[6]; //HARDCODE: predefined agents that will get the initial game obj
    game_update_agents = new AID[4]; //HARDCODE: predefined agents that will get game obj updates
    
    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    //NOTE: skip index 0 since this is the name of the proxy bot agent
    for(int i=1, j=0, k=0, l=0; i < args.length; i++, j++){
      khasbot_agents[j] = (String)args[i];
      khasbot_agent_names[j] = (String)Array.get(khasbot_agents[j].split(";"),0);

      //add the agents that will get game updates
      if(khasbot_agent_names[j].matches(".*[Bb]uilding[Mm]anager.*")){
        building_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        init_game_agents[k++] = building_manager;
      } else if(khasbot_agent_names[j].matches(".*[Uu]nit[Mm]anager.*")){
        unit_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        init_game_agents[k++] = unit_manager;
        game_update_agents[l++] = unit_manager;
      } else if(khasbot_agent_names[j].matches(".*[Ss]tructure[Mm]anager.*")) {
        structure_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
        init_game_agents[k++] = structure_manager;
        game_update_agents[l++] = structure_manager;
      } else if(khasbot_agent_names[j].matches(".*[Bb]attle[Mm]anager.*")) {
    	  battle_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  init_game_agents[k++] = battle_manager;
        game_update_agents[l++] = battle_manager;
      } else if(khasbot_agent_names[j].matches(".*[Rr]esource[Mm]anager.*")) {
    	  resource_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  init_game_agents[k++] = resource_manager;
        game_update_agents[l++] = resource_manager;
      } else if(khasbot_agent_names[j].matches(".*[Mm]ap[Mm]anager.*")) {
    	  map_manager = new AID(khasbot_agent_names[j],AID.ISLOCALNAME);
    	  init_game_agents[k++] = map_manager;
      }
    } 

    /*
     * This section consists of the ACLMessage.INFORM templates
     */
    proxybotagent_inform_mt = MessageTemplate.and(
                                                  MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                                  MessageTemplate.MatchSender(proxybotagent)
                                                  );

    unitmanager_inform_mt = MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                                MessageTemplate.MatchSender(unit_manager)
                                                );
    /*
     * This section consists of the FIPANames.InteractionProtocol.FIPA_REQUEST templates
     */
    fipa_request_mt = MessageTemplate.and(
                                         MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                         MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                                         );

    //This call will create all of the other agents that will be needed
    CommanderAgentCreateAgents agents = new CommanderAgentCreateAgents(khasbot_agents);
    agents.createAgents(this,khasbot_agent_names);

    ParallelBehaviour root_behaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
   
    //this behaviour is used to handle the GameObject and GameObjectUpdate that is sent by ProxyBot
    root_behaviour.addSubBehaviour(new CommanderAgentRespInfProxyBA(this,proxybotagent_inform_mt,init_game_agents,game_update_agents));

    //This behaviour is used to handle commands received by the unit manager
    root_behaviour.addSubBehaviour(new CommanderAgentRespInfUnitM(this,unitmanager_inform_mt,proxybotagent));

    
    //root_behaviour.addSubBehaviour(new CommanderAgentRespFIPARequest(this,fipa_request_mt));

    addBehaviour(root_behaviour);

//    CAHelpers = new AID[1];
//
//    CAH_build_order = new AID("CAH_build_order",AID.ISLOCALNAME);
//
//    CAHelpers[0] = CAH_build_order;
//
//    createMyHelperAgents("CAH_build_order");

  }//end setup

  /**
   * This method is used to create a whole new set of helper agents that the commander will
   * use to
   * @param khasbot_agent_names
   *
  public void createMyHelperAgents(String[] helper_agent_names) {
    final CreateAgent ca = new CreateAgent();
    Action actExpr = new Action();

    //all the agents created will get the names of all the other agents, so that they
    //can build AID's from them
    for( int i = 0; i < helper_agent_names.length; i++)
      ca.addArguments(helper_agent_names[i]);

    //Only creates the Building and Structure managers for now
    for( int i = 0; i < helper_agent_names.length ; i++) {
      ca.setAgentName((String)Array.get(helper_agent_names[i].split(";"),0));
      ca.setClassName((String)Array.get(helper_agent_names[i].split(";"),1));
      ca.setContainer(new ContainerID(AgentContainer.MAIN_CONTAINER_NAME, null));

      //DEBUG
      //System.out.println("Creating Agent: " + ca.getAgentName() + " with class " + ca.getClassName());

      actExpr.setActor(this.getAMS());
      actExpr.setAction(ca);

      ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
      request.addReceiver(this.getAMS());
      request.setOntology(JADEManagementOntology.getInstance().getName());
      request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
      request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

      try {
        this.getContentManager().fillContent(request, actExpr);
        this.addBehaviour(new AchieveREInitiator(this, request) {
          protected void handleInform(ACLMessage inform) {
            //System.out.println(commander.getAID().getLocalName() + ": Agent successfully created");
          }
          protected void handleFailure(ACLMessage failure) {
            System.out.println("Error creating agent.");
          }
        } );
      } catch (Exception e) {
        e.printStackTrace();
      }//end try-catch
    }// for
  }
*/
}//end CommanderAgent

