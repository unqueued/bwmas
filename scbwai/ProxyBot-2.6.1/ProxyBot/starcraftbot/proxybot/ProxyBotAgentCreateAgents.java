
package starcraftbot.proxybot;

import jade.content.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.*;
import jade.core.*;
import jade.domain.FIPANames;
import jade.domain.FIPANames.ContentLanguage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.lang.reflect.*;

import starcraftbot.proxybot.ReadyToGo;

public class ProxyBotAgentCreateAgents {
 
  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;

  String agent_name = null;
  String agent_classname = null;
  String [] khasbot_agents = null;

  public ProxyBotAgentCreateAgents(String[] agents, String name, String classname) {
    khasbot_agents = agents;
    agent_name = name;
    agent_classname = classname;
  }

  public void createAgents(final Agent agent) {

    final CreateAgent ca = new CreateAgent();
    final CreateAgent ca2 = new CreateAgent();
    Action actExpr = new Action();
	Action actExpr2 = new Action();
    /*
     * Now create the agents for our application
     */
    //pass a wait switch so that we can communicate with the agent once it has
    //been created
    ca.setAgentName(agent_name);
    ca.setClassName(agent_classname);

    ca2.setAgentName("SnifferBot");
    ca2.setClassName("jade.tools.sniffer.Sniffer");
    
    //now send the khasbot agents as individual strings. I wasn't able
    //to get the String[] to work (I'm also too tired to figure it out now,
    //I might come back to it later)
    //NOTE: skip index 0, since it refers to the commander and we don't want 
    //the commander creating another commander agent
    for( int i=1; i < khasbot_agents.length; i++ )
      ca.addArguments(khasbot_agents[i]);
  
    ca.setContainer(new ContainerID(AgentContainer.MAIN_CONTAINER_NAME, null));

    ca2.setContainer(new ContainerID(AgentContainer.MAIN_CONTAINER_NAME,null));
    
    //DEBUG
    //System.out.println("Creating Agent: " + ca.getAgentName() + " with class " + ca.getClassName());

    actExpr.setActor(agent.getAMS());
    actExpr.setAction(ca);

    actExpr2.setActor(agent.getAMS());
    actExpr2.setAction(ca2);
    
    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
    request.addReceiver(agent.getAMS());
    request.setOntology(JADEManagementOntology.getInstance().getName());
    request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    
    ACLMessage request2 = new ACLMessage(ACLMessage.REQUEST);
    request2.addReceiver(agent.getAMS());
    request2.setOntology(JADEManagementOntology.getInstance().getName());
    request2.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
    request2.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

    try {
      //request.setContentObject((Object[])agentArgs);
        /*agent.getContentManager().fillContent(request2, actExpr2);
        
        agent.addBehaviour(new AchieveREInitiator(agent, request2) {
          protected void handleInform(ACLMessage inform) {
            //DEBUG
            System.out.println(agent.getAID().getLocalName() + ": " + ca2.getAgentName() + " successfully created");
          }
          protected void handleFailure(ACLMessage failure) {
            System.out.println("Error creating sniffer agent.");
          }
        } );	
    	*/
      agent.getContentManager().fillContent(request, actExpr);
    
      agent.addBehaviour(new AchieveREInitiator(agent, request) {
        protected void handleInform(ACLMessage inform) {
          //DEBUG
          //System.out.println(agent.getAID().getLocalName() + ": " + ca.getAgentName() + " successfully created");
        }
        protected void handleFailure(ACLMessage failure) {
          System.out.println("Error creating agent.");
        }
      } );

    } catch (Exception e) {
      e.printStackTrace();
    }//end try-catch



  }//end createAgents

}//end ProxyBotAgentCreateAgents


