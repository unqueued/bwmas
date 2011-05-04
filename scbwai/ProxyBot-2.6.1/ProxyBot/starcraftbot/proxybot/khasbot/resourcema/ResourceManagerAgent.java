package starcraftbot.proxybot.khasbot.resourcema;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.*;
import jade.proto.*;

import java.io.*;
import java.util.*;

import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;


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


    ResourceManagerAgentInfUnitM resp_inf_unitm =
            new ResourceManagerAgentInfUnitM(this, unitm_inform_mt);
//    ResourceManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
//            new ResourceManagerAgentRespFIPAReqUnitM(this, unitm_fipa_req_mt);
//    ResourceManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
//            new ResourceManagerAgentRespFIPAReqMapM(this, mapm_fipa_req_mt);
    

    MessageTemplate inf_fromMapm_mt = MessageTemplate.and(
                                                        MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                                                        MessageTemplate.MatchSender(map_manager)
                                                       );
    ResourceManagerAgentInfMapM inf_mapm =
            new ResourceManagerAgentInfMapM(this, inf_fromMapm_mt, myDS);

    resp_inf_unitm.setDataStore(myDS);
//    resp_fipa_req_unitm.setDataStore(myDS);
//    resp_fipa_req_mapm.setDataStore(myDS);
    

    /* handle ACLMessgae.INFORM responders */
    addThreadedBehaviour(resp_inf_unitm);

    /* handle FIPA request responders */
//    addThreadedBehaviour(resp_fipa_req_unitm);
//    addThreadedBehaviour(resp_fipa_req_mapm);
    
    addThreadedBehaviour(inf_mapm);

  }

  @SuppressWarnings("unchecked")
  public void parseGameObject(GameObject g){
    GameObject lgameObj = g;
    ArrayList<UnitObject> minerals = (ArrayList<UnitObject>)myDS.get("minerals");

    assert g == null: "Incoming GameObject g cannot be null";
    assert lgameObj == null: "LocalGameObject lgameObj cannot be null";

    myDS.put("gameObj", lgameObj);

    if(minerals == null){
      System.out.println("MinearlList is empty!!!");
      requestMineralList();
    }

    if(gather_minerals == null){
      gather_minerals = new ResourceManagerAgentActionGatherMinerals(this);
      gather_minerals.setDataStore(myDS);
      addThreadedBehaviour(gather_minerals);
    }
    
//    if(gas == null){
//      sendRequestFor(ConverId.UnitM.GasList.getConId(), map_manager, 0);
//    }
  }


  public void setGameObjectUpdate(GameObjectUpdate g){
    GameObjectUpdate lgameObjUp = g;

    assert g == null: "Incoming GameObjectUpdate g cannot be null";
    assert lgameObjUp == null: "LocalGameObjectUpdate lgameObjUp cannot be null";

    myDS.put("gameObj", lgameObjUp);

  }

    /* may have to make this it's own thread */
  public void requestCommandsExe(ArrayList<GameCommand> cmds){
    if(!cmds.isEmpty()){
      System.out.println(this.getLocalName() + "> Sending commands to UnitM");
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.setConversationId(ConverId.UnitM.SendCommandsToCommander.getConId());
      try{
        msg.setContentObject((ArrayList<GameCommand>)cmds);
      }catch(Exception e){
        System.out.println("Failed to set message object: " + e.toString());
      }
      msg.addReceiver(unit_manager);
      send(msg);
    }
  }

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
 
    if(!found){
      System.out.println("Adding a worker!!");
      my_units.add(worker);
      myDS.put("my_units",my_units);
    }else{
      System.out.println("Duplicate worker found!!!");
      gather_minerals.duplicateWorkerFound();
    }
  }

  @SuppressWarnings("unchecked")
  public GameCommand retaskWorker(int workerId){
    ArrayList<UnitObject> my_units = (ArrayList<UnitObject>)myDS.get("my_units");
    HashMap<Integer,GameCommand> unit_last_cmd = (HashMap<Integer,GameCommand>)myDS.get("unit_last_cmd");
    GameCommand last_cmd = null;
    if(my_units == null)
      return null;
    for(Iterator itr = my_units.iterator(); itr.hasNext(); ){
      UnitObject u = (UnitObject)itr.next();
      if(u.getID() == workerId){
        if(unit_last_cmd == null){
          System.out.println("unit_last_cmd.get() is null");
        }else{
          last_cmd = unit_last_cmd.get(workerId);
          my_units.remove(u);
          unit_last_cmd.remove(workerId);
          break;
        }
      }
    }

    myDS.put("my_units", my_units);
    myDS.put("unit_last_cmd", unit_last_cmd);
    return last_cmd;
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
  public void requestWorker(int count){
    sendRequestFor(ConverId.ResM.NeedWorker.getConId(), unit_manager, count);
  }

  public void requestMineralList(){
    sendRequestFor(ConverId.UnitM.MineralList.getConId(), map_manager, 0);
  }

  /* message processing methods */
  public void sendRequestFor(String request, AID receiver, int payload){
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    //msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    
    if(request.equals(ConverId.ResM.NeedWorker.getConId())){
      msg.setConversationId(ConverId.ResM.NeedWorker.getConId());
      msg.setContent(payload+"");
      msg.setPerformative(ACLMessage.INFORM);
    }else if(request.equals(ConverId.UnitM.MineralList.getConId())){
      msg.setConversationId(ConverId.MapM.NearestMinerals.getConId());
      msg.setContent(ConverId.MapM.NearestMinerals.getConId());
    }else if(request.equals(ConverId.UnitM.GasList.getConId())){
      msg.setConversationId(ConverId.MapM.NearestGas.getConId());
      msg.setContent(ConverId.MapM.NearestGas.getConId());
    }

    msg.addReceiver(receiver);
    send(msg);
    System.out.println("Sending a request for: " + msg.getConversationId());
    
//    if( request.equals(ConverId.ResM.NeedWorker.getConId())){
//      ResourceManagerAgentInitFIPAReqUnitM init_fipa_req_unitm =
//              new ResourceManagerAgentInitFIPAReqUnitM(this, msg);
//      addThreadedBehaviour(init_fipa_req_unitm);
//      gather_minerals.setRequestWorker(true);
//    }
//    if( request.equals(ConverId.UnitM.MineralList.getConId())){
//      ResourceManagerAgentInitFIPAReqMapM init_fipa_req_mapm =
//              new ResourceManagerAgentInitFIPAReqMapM(this, msg);
//      init_fipa_req_mapm.setDataStore(myDS);
//      addThreadedBehaviour(init_fipa_req_mapm);
//    }
//    if( request.equals(ConverId.UnitM.GasList.getConId())){
//      ResourceManagerAgentInitFIPAReqMapM init_fipa_req_mapm =
//              new ResourceManagerAgentInitFIPAReqMapM(this, msg);
//      init_fipa_req_mapm.setDataStore(myDS);
//      addThreadedBehaviour(init_fipa_req_mapm);
//    }
    
  }//end sendRequestFor
}//end ResourceManagerAgent

