
package starcraftbot.proxybot.khasbot;

import jade.content.*;
import jade.content.lang.*;
import jade.content.onto.Ontology;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;

abstract public class KhasBotAgent extends Agent {
  protected ContentManager manager = null;
	protected Codec codec = null;
  protected Ontology ontology = null;

  protected GameObject gameObj;
  protected GameObjectUpdate gameObjUp;

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

  /*
   * Message Template: FIPANames.InteractionProtocol.FIPA_REQUEST
   */
  protected MessageTemplate cmd_fipa_req_mt = null;
  protected MessageTemplate unitm_fipa_req_mt = null;
  protected MessageTemplate mapm_fipa_req_mt = null;
  protected MessageTemplate buildm_fipa_req_mt = null;
  protected MessageTemplate structm_fipa_req_mt = null;
  protected MessageTemplate resm_fipa_req_mt = null;
  protected MessageTemplate battm_fipa_req_mt = null;


  @Override
  protected void setup(){
    tbf = new ThreadedBehaviourFactory();
    extractAgentNames(getArguments());


    commander_inform_mt = MessageTemplate.and(
                                               MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                               MessageTemplate.MatchSender(commander)
                                             );

    unitm_inform_mt = MessageTemplate.and(
                                          MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                          MessageTemplate.MatchSender(unit_manager)
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

    resm_fipa_req_mt = MessageTemplate.and(
                                             MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                                             MessageTemplate.and(
                                                                  MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                                                  MessageTemplate.MatchSender(resource_manager)
                                             )
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
  }

	abstract protected void setGameObject(GameObject g);
  abstract protected void setGameObjectUpdate(GameObjectUpdate g);

  public GameObject getGameObject(){
		return this.gameObj;
	}

  public GameObjectUpdate getGameObjectUpdate(){
		return this.gameObjUp;
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
