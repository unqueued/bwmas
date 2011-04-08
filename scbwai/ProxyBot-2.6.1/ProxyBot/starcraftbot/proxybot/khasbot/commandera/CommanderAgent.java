
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
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;

import starcraftbot.proxybot.khasbot.KhasBotAgent;

public class CommanderAgent extends KhasBotAgent {

  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents = null; // this stores> agent_name;class_name
  String [] khasbot_agent_names = null; //this stores> agent_name
  AID [] init_game_agents = null; //this stores> agent_names that get initial game obj
  AID [] game_update_agents = null; //this stores> agent_names that get game obj updates

  /* khasbot agent names */
  AID proxybotagent = null;

  /*
   * Commander agent specifically
   */
  AID[] CAHelpers = null;

  /* Commander Agent Helper agent names */
  AID CAH_build_order = null;


  public void setup() {
    super.setup();
    
    //
    //Message Templates
    //
  
    MessageTemplate pba_inform_mt = null;

    this.codec = new SLCodec();
    this.getContentManager().registerLanguage(this.codec);
    this.ontology=JADEManagementOntology.getInstance();
    this.getContentManager().registerOntology(this.ontology);


    CommanderAgentRespInfProxyBA resp_inf_pba = null;
    CommanderAgentRespInfUnitM resp_inf_unitm = null;
    
    //arguments passed into agent
    Object[] args = getArguments();
    
    //set the proxy bot agent name, this index is HARDCODE
    String temp = (String)args[0];
    proxybotagent = new AID(temp,AID.ISLOCALNAME);

    khasbot_agents = new String[args.length-1];
    khasbot_agent_names = new String[args.length];
    init_game_agents = new AID[6]; //HARDCODE: predefined agents that will get the initial game obj
    game_update_agents = new AID[4]; //HARDCODE: predefined agents that will get game obj updates

    khasbot_agent_names[0] = this.getAID().getLocalName();
    
    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    //NOTE: for i skip index 0 since this is the name of the proxy bot agent
    //NOTE: for k skip index 0 since this is the name of the commander agent
    for(int i=1, j=0, k=1, l=0, m=0; i < args.length; i++, j++, k++){
      khasbot_agents[j] = (String)args[i];
      khasbot_agent_names[k] = (String)Array.get(khasbot_agents[j].split(";"),0);

      //add the agents that will get game updates
      if(khasbot_agent_names[k].matches(".*[Bb]uilding[Mm]anager.*")){
        building_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
        init_game_agents[l++] = building_manager;
      } else if(khasbot_agent_names[k].matches(".*[Uu]nit[Mm]anager.*")){
        unit_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
        init_game_agents[l++] = unit_manager;
        game_update_agents[m++] = unit_manager;
      } else if(khasbot_agent_names[k].matches(".*[Ss]tructure[Mm]anager.*")) {
        structure_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
        init_game_agents[l++] = structure_manager;
        game_update_agents[m++] = structure_manager;
      } else if(khasbot_agent_names[k].matches(".*[Bb]attle[Mm]anager.*")) {
    	  battle_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
    	  init_game_agents[l++] = battle_manager;
        game_update_agents[m++] = battle_manager;
      } else if(khasbot_agent_names[k].matches(".*[Rr]esource[Mm]anager.*")) {
    	  resource_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
    	  init_game_agents[l++] = resource_manager;
        game_update_agents[m++] = resource_manager;
      } else if(khasbot_agent_names[k].matches(".*[Mm]ap[Mm]anager.*")) {
    	  map_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
    	  init_game_agents[l++] = map_manager;
      }
    } 

    /*
     * This section consists of the ACLMessage.INFORM templates
     */

    pba_inform_mt = MessageTemplate.and(
                                        MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                        MessageTemplate.MatchSender(proxybotagent)
                                        );


    //This call will create all of the other agents that will be needed
    CommanderAgentCreateAgents agents = new CommanderAgentCreateAgents(khasbot_agents);
    agents.createAgents(this,khasbot_agent_names);


    resp_inf_pba = new CommanderAgentRespInfProxyBA(this,pba_inform_mt,init_game_agents,game_update_agents);
    resp_inf_unitm = new CommanderAgentRespInfUnitM(this,unitm_inform_mt,proxybotagent);

    resp_inf_unitm.setDataStore(resp_inf_pba.getDataStore());

    //this behaviour is used to handle the GameObject and GameObjectUpdate that is sent by ProxyBot
    addThreadedBehaviour(resp_inf_pba);

    //This behaviour is used to handle commands received by the unit manager
    addThreadedBehaviour(resp_inf_unitm);

    

//    CAHelpers = new AID[1];
//
//    CAH_build_order = new AID("CAH_build_order",AID.ISLOCALNAME);
//
//    CAHelpers[0] = CAH_build_order;
//
//    createMyHelperAgents("CAH_build_order");

  }//end setup

  @Override
  protected void setGameObject(GameObject g) {
    //does not store gameObj
	  myDS.put("game", g);  
  }

  @Override
  protected void setGameObjectUpdate(GameObjectUpdate g) {
    //does not store gameObjUp
	  myDS.put("gameUpate", g);  
  }

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

