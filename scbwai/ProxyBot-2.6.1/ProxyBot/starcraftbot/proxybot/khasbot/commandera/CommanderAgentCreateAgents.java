package starcraftbot.proxybot.khasbot.commandera;

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

public class CommanderAgentCreateAgents {
 
  //an array of strings will be used to store the agent names and paths
  //the values will be split via a ;
  String [] khasbot_agents; 

  public CommanderAgentCreateAgents(String [] input) {
    khasbot_agents = input;
    
  }

  public void createAgents(final Agent commander, String[] khasbot_agent_names) {
 
    final CreateAgent ca = new CreateAgent();
    Action actExpr = new Action();

    //all the agents created will get the names of all the other agents, so that they
    //can build AID's from them
    for( int i = 0; i < khasbot_agent_names.length; i++)
      ca.addArguments(khasbot_agent_names[i]);

    //Only creates the Building and Structure managers for now
    for( int i = 0; i < khasbot_agents.length ; i++) {
      ca.setAgentName((String)Array.get(khasbot_agents[i].split(";"),0));
      ca.setClassName((String)Array.get(khasbot_agents[i].split(";"),1));
      ca.setContainer(new ContainerID(AgentContainer.MAIN_CONTAINER_NAME, null));

      //DEBUG
      //System.out.println("Creating Agent: " + ca.getAgentName() + " with class " + ca.getClassName());

      actExpr.setActor(commander.getAMS());
      actExpr.setAction(ca);

      ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
      request.addReceiver(commander.getAMS());
      request.setOntology(JADEManagementOntology.getInstance().getName());
      request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
      request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

      try {
        commander.getContentManager().fillContent(request, actExpr);
        commander.addBehaviour(new AchieveREInitiator(commander, request) {
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
  }//end createAgents

}//end CommanderAgentCreateAgents

