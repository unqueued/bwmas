
package starcraftbot.proxybot.khasbot.commandera;

import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;

import java.lang.reflect.*;

import starcraftbot.proxybot.khasbot.KhasBotAgent;

public class CommanderAgent extends KhasBotAgent {


  @Override
  public void setup() {
    super.setup();

    //an array of strings will be used to store the agent names and paths
    //the values will be split via a ;
    String [] khasbot_agents = null; // this stores> agent_name;class_name
    String [] khasbot_agent_names = null; //this stores> agent_name

    /* khasbot agent names */
    AID proxybotagent = null;

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
      } else if(khasbot_agent_names[k].matches(".*[Uu]nit[Mm]anager.*")){
        unit_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
      } else if(khasbot_agent_names[k].matches(".*[Ss]tructure[Mm]anager.*")) {
        structure_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
      } else if(khasbot_agent_names[k].matches(".*[Bb]attle[Mm]anager.*")) {
    	  battle_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
      } else if(khasbot_agent_names[k].matches(".*[Rr]esource[Mm]anager.*")) {
    	  resource_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
      } else if(khasbot_agent_names[k].matches(".*[Mm]ap[Mm]anager.*")) {
    	  map_manager = new AID(khasbot_agent_names[k],AID.ISLOCALNAME);
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

    resp_inf_pba = new CommanderAgentRespInfProxyBA(this,pba_inform_mt,unit_manager);
    resp_inf_unitm = new CommanderAgentRespInfUnitM(this,exe_cmds_mt,proxybotagent);

    resp_inf_pba.setDataStore(myDS);
    resp_inf_unitm.setDataStore(myDS);

    //this behaviour is used to handle the GameObject and GameObjectUpdate that is sent by ProxyBot
    addThreadedBehaviour(resp_inf_pba);

    //This behaviour is used to handle commands received by the unit manager
    addThreadedBehaviour(resp_inf_unitm);

  }//end setup

//  @Override
//  protected void setGameObject(GameObject g) {
//    //does not store gameObj
//	  //myDS.put("gameObj", g);
//  }

//  @Override
//  protected void setGameObjectUpdate(GameObjectUpdate g) {
//    //does not store gameObjUp
//	  //myDS.put("gameObjUp", g);
//  }

}//end CommanderAgent

