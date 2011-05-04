
package starcraftbot.proxybot.khasbot;

import jade.content.*;
import jade.content.lang.*;
import jade.content.onto.Ontology;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import starcraftbot.proxybot.ConverId;

@SuppressWarnings("serial")
abstract public class KhasBotAgent extends Agent {
  protected ContentManager manager = null;
	protected Codec codec = null;
  protected Ontology ontology = null;

  protected DataStore myDS = null;

  protected AID commander = null;
  protected AID building_manager = null;
  protected AID structure_manager = null;
  protected AID battle_manager = null;
  protected AID resource_manager = null;
  protected AID map_manager = null;
  protected AID unit_manager = null;

  protected ThreadedBehaviourFactory tbf = null;

  /*
   * Message Template: ACLMessage.INFORM
   */
  protected MessageTemplate commander_inform_mt = null;
  protected MessageTemplate unitm_inform_mt = null;
  protected MessageTemplate exe_cmds_mt = null;
  protected MessageTemplate inform_commands_mt = null;

  /*
   * Message Template: FIPANames.InteractionProtocol.FIPA_REQUEST
   */
  protected MessageTemplate cmd_fipa_req_mt = null;
  protected MessageTemplate unitm_fipa_req_mt = null;
  protected MessageTemplate mapm_fipa_req_mt = null;
  protected MessageTemplate buildm_fipa_req_mt = null;
  protected MessageTemplate structm_fipa_req_mt = null;
  protected MessageTemplate resm_fipa_unitm_mt = null;
  protected MessageTemplate resm_fipa_mapm_mt = null;
  protected MessageTemplate battm_fipa_req_mt = null;


  @Override
  protected void setup(){
    tbf = new ThreadedBehaviourFactory();
    extractAgentNames(getArguments());

    myDS = new DataStore();

    commander_inform_mt = MessageTemplate.and(
                                               MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                               MessageTemplate.MatchSender(commander)
                                             );
    unitm_inform_mt = MessageTemplate.and(
                                           MessageTemplate.not(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST)),
                                           MessageTemplate.and(
                                             MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                             MessageTemplate.MatchSender(unit_manager)
                                           )
                                       );

    exe_cmds_mt = MessageTemplate.and(
                                      MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                      MessageTemplate.MatchConversationId(ConverId.Commands.ExecuteCommand.getConId())
                                      );

    unitm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                MessageTemplate.MatchSender(unit_manager)
                                             )
                                           );

    mapm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                MessageTemplate.MatchSender(map_manager)
                                              )
                                            );

    buildm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                MessageTemplate.MatchSender(building_manager)
                                             )
                                           );

    structm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                MessageTemplate.MatchSender(structure_manager)
                                              )
                                            );

    AID [] rx = new AID[1];
    rx[0] = unit_manager;
//    resm_fipa_unitm_mt = MessageTemplate.and(
//                                            // MessageTemplate.and(
//                                                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
//                                          //      MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
//                                             //),
//                                            // MessageTemplate.and(
//                                           //     MessageTemplate.MatchReceiver(rx),
//                                                MessageTemplate.MatchSender(resource_manager)
//                                           //  )
//                                           );
   rx[0] = map_manager;
   resm_fipa_mapm_mt = MessageTemplate.and(
//                                             MessageTemplate.and(
                                                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
//                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
//                                             ),
//                                             MessageTemplate.and(
//                                                MessageTemplate.MatchReceiver(rx),
                                                MessageTemplate.MatchSender(resource_manager)
//                                             )
                                           );
   
    battm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                MessageTemplate.MatchSender(battle_manager)
                                              )
                                            );

    cmd_fipa_req_mt = MessageTemplate.and(
                                           MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                           MessageTemplate.and(
                                              MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                              MessageTemplate.MatchSender(commander)
                                            )
                                          );
    inform_commands_mt = MessageTemplate.and(
                                             MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                             MessageTemplate.MatchConversationId(ConverId.UnitM.NewCommands.getConId())
                                            );

  }

  public DataStore getDS(){
	  return this.myDS;
  }
  public void setDS(DataStore d){
	  this.myDS = d;
  }

  @Override
  protected void takeDown(){
     this.tbf.interrupt();
     super.takeDown();
  }
  
  protected void extractAgentNames(Object[] args){
    String temp = null;

    //
    //now strip out the khasbot agents that the commander will create from the arguments passed in
    //
    for(int i=0; i < args.length; i++){
      temp = (String) args[i];
      //add the agents that will get game updates
      if(temp.matches(".*[Cc]ommander.*"))
        commander = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Bb]uilding[Mm]anager.*"))
        building_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Ss]tructure[Mm]anager.*"))
        structure_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Bb]attle[Mm]anager.*"))
        battle_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Rr]esource[Mm]anager.*"))
        resource_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Mm]ap[Mm]anager.*"))
        map_manager = new AID(temp,AID.ISLOCALNAME);
      else if(temp.matches(".*[Uu]nit[Mm]anager.*"))
        unit_manager = new AID(temp,AID.ISLOCALNAME);
    }
  }

  protected void addThreadedBehaviour(Behaviour b) {
      addBehaviour(this.tbf.wrap(b));
  }

}
