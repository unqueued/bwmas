package starcraftbot.proxybot.khasbot.resourcema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.*;
import jade.proto.*;

import java.io.IOException;
import java.util.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.ConverId.UnitM;

import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.GameCommandQueue;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;
import starcraftbot.proxybot.khasbot.unitma.Units;

@SuppressWarnings("serial")
public class ResourceManagerAgent extends KhasBotAgent{


  ResourceManagerAgentActionGatherMinerals gather_minerals = null;
  ResourceManagerAgentActionGatherGas gather_gas = null;

  @Override
  protected void setup(){
    super.setup();

    this.codec = new SLCodec();
    this.getContentManager().registerLanguage(this.codec);
    this.ontology=JADEManagementOntology.getInstance();
    this.getContentManager().registerOntology(this.ontology);

    /* initialize the some, not all the elements of the datastore */
    myDS.put("gameObj",null);
    myDS.put("RequestMinerals",false);
    myDS.put("RequestGas",false);

    ResourceManagerAgentRespInfUnitM resp_inf_unitm =
            new ResourceManagerAgentRespInfUnitM(this, unitm_inform_mt);
    ResourceManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new ResourceManagerAgentRespFIPAReqUnitM(this, unitm_fipa_req_mt);
    ResourceManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
            new ResourceManagerAgentRespFIPAReqMapM(this, mapm_fipa_req_mt);
    gather_minerals = new ResourceManagerAgentActionGatherMinerals(this);

    resp_inf_unitm.setDataStore(myDS);
    resp_fipa_req_unitm.setDataStore(myDS);
    resp_fipa_req_mapm.setDataStore(myDS);
    gather_minerals.setDataStore(myDS);

    /* handle ACLMessgae.INFORM responders */
    addThreadedBehaviour(resp_inf_unitm);

    /* handle FIPA request responders */
    addThreadedBehaviour(resp_fipa_req_unitm);
    addThreadedBehaviour(resp_fipa_req_mapm);
    addThreadedBehaviour(gather_minerals);

  }

  /* agents data manipulation methods */
  public void parseGameObject(GameObject g){
    GameObject lgameObj = g;

    assert g == null: "Incoming GameObject g cannot be null";
    assert lgameObj == null: "LocalGameObject lgameObj cannot be null";

    myDS.put("gameObj", lgameObj);
    //System.out.println(this.getLocalName() + "> finished parseGameObject ");
  }

//  @Override
//  public void setGameObjectUpdate(GameObjectUpdate g){
//    GameObjectUpdate lgameObjUp = g;
//
//    assert g == null: "Incoming GameObjectUpdate g cannot be null";
//    assert lgameObjUp == null: "LocalGameObjectUpdate lgameObjUp cannot be null";
//
//    myDS.put("gameObjUp", lgameObjUp);
//
//  }

  @SuppressWarnings("unchecked")
  public void addWorker(UnitObject worker){
    ArrayList<UnitObject> my_units = (ArrayList<UnitObject>)myDS.get("my_units");
    boolean found = false;

    if( my_units == null)
      my_units = new ArrayList<UnitObject>();
    
    for(Iterator itr = my_units.iterator(); itr.hasNext(); ){
      UnitObject u = (UnitObject)itr.next();
      if(worker.equalTo(u)){
        found = true;
        break;
      }
    }
 
    if( found ){
      gather_minerals.setRequestWorker(false);
    }else{
      my_units.add(worker);
      myDS.put("my_units",my_units);
    }
  }

  @SuppressWarnings("unchecked")
  public ArrayList<UnitObject> getMyUnits(){
    return (ArrayList<UnitObject>)myDS.get("my_units");
  }

  @SuppressWarnings("unchecked")
  public int numOfWorkers(){
    ArrayList<UnitObject> my_units = (ArrayList<UnitObject>)myDS.get("my_units");
    if( my_units == null ){
      return 0;
    }else{
      return ((ArrayList<UnitObject>)myDS.get("my_units")).size();
    }
  }

  /* message request methods */
  public void canEnoughResourcesToBuild(){
  }

  @SuppressWarnings("unchecked")
  public void requestWorker(){
    boolean RequestMinerals = ((Boolean)myDS.get("RequestMinerals"));
    ArrayList<UnitObject> minerals = ((ArrayList<UnitObject>)myDS.get("minerals"));

    if(minerals == null && !RequestMinerals ){
      sendRequestFor(ResMRequests.MineralList, map_manager);
      RequestMinerals = true;
      myDS.put("RequestMinerals", RequestMinerals);
    }
//    if(gas == null && !RequestGas ){
//      sendRequestFor(ResMRequests.GasList, map_manager);
//      RequestMinerals = true;
//      myDS.put("RequestMinerals", RequestMinerals);
//    }
    sendRequestFor(ResMRequests.RequestWorker, unit_manager);
    
  }

  /* may have to make this it's own thread */
  public void requestCommandsExe(ArrayList<GameCommand> cmds){
    if( !cmds.isEmpty()){
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setConversationId(ConverId.UnitM.NewCommands.getConId());
      try{
        msg.setContentObject((ArrayList<GameCommand>)cmds);
      }catch(Exception e){
        System.out.println("Failed to set message object: " + e.toString());
      }
      msg.addReceiver(unit_manager);
      this.send(msg);
      System.out.println(this.getLocalName() + "> Commands sent to " + unit_manager.getLocalName());
    }
  }

  /* message processing methods */
  public void sendRequestFor(ResMRequests request, AID receiver){
    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    
    if(request == ResMRequests.RequestWorker){
      msg.setConversationId(ConverId.UnitM.NeedWorker.getConId());
      msg.setContent(ConverId.UnitM.NeedWorker.getConId());
    }else if(request == ResMRequests.MineralList){
      msg.setConversationId(ConverId.MapM.NearestMinerals.getConId());
      msg.setContent(ConverId.MapM.NearestMinerals.getConId());
    }

    msg.addReceiver(receiver);
    this.send(msg);
    
    if( request == ResMRequests.RequestWorker ){
      ResourceManagerAgentInitFIPAReqUnitM init_fipa_req_unitm =
              new ResourceManagerAgentInitFIPAReqUnitM(this, msg);
      gather_minerals.setRequestWorker(true);
      addThreadedBehaviour(init_fipa_req_unitm);
    }
    if( request == ResMRequests.MineralList ){
      ResourceManagerAgentInitFIPAReqMapM init_fipa_req_mapm =
              new ResourceManagerAgentInitFIPAReqMapM(this, msg);
      init_fipa_req_mapm.setDataStore(myDS);
      addThreadedBehaviour(init_fipa_req_mapm);
    }
    
  }//end sendRequestFor
}//end ResourceManagerAgent

