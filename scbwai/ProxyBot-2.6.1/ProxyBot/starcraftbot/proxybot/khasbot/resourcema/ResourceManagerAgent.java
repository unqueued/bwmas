
package starcraftbot.proxybot.khasbot.resourcema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;
import java.util.*;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.ConverId.UnitM;

import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class ResourceManagerAgent extends KhasBotAgent {

  //this will maintain a list of worker units
  //    this HashMap will use <k,v> as <unit id num,UnitObject>
  HashMap<Integer,UnitObject> my_units = null;

  ResourceManagerAgentInitFIPAReqUnitM unitm_Init = null;

  @Override
	protected void setup(){
    super.setup();

    my_units = new HashMap<Integer,UnitObject>();
    
    ResourceManagerAgentRespInfCmd resp_inf_cmd =
            new ResourceManagerAgentRespInfCmd(this,this.commander_inform_mt);
    ResourceManagerAgentActionGatherResources gather_res =
            new ResourceManagerAgentActionGatherResources(this);
    ResourceManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new ResourceManagerAgentRespFIPAReqUnitM(this,this.unitm_fipa_req_mt);
//    ResourceManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
//            new ResourceManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt);

    gather_res.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_unitm.setDataStore(resp_inf_cmd.getDataStore());
    
    /* handle ACLMessgae.INFORM responders */
    addThreadedBehaviour(resp_inf_cmd);
    
    /* handle FIPA request responders */
    addThreadedBehaviour(gather_res);
    addThreadedBehaviour(resp_fipa_req_unitm);
    //can't think of a good reason why MapManager is going to request something from ResourceManager
    //addThreadedBehaviour(resp_fipa_req_mapm);
	}

  /* agents data manipulation methods */
  @Override
	public void setGameObject(GameObject g)
	{
		gameObj = g;
	}

  @Override
  public void setGameObjectUpdate(GameObjectUpdate g)
	{
		gameObjUp = g;
	}

  public void addWorker(UnitObject worker){
    my_units.put(worker.getID(),worker);
  }

  public int numOfWorkers(){
    return my_units.size();
  }

  /* message request methods */
  public void canEnoughResourcesToBuild(){

  }
  
  public void requestWorker(){
    sendRequestFor(ResMRequests.RequestWorker,unit_manager);
  }

  /* message processing methods */
  public void sendRequestFor(ResMRequests request, AID receiver){

//    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
//    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
//   // msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));
//
//    if(request == ResMRequests.RequestWorker){
//      //set a conversatinon id for this FIPA-Request
//      msg.setConversationId(ConverId.UnitM.NeedWorker.getConId());
//      //System.out.println(this.getLocalName() + " >>> REQUEST: for a worker");
//    }
//
//    msg.addReceiver(receiver);
//    this.send(msg);
//    root_behaviour.addSubBehaviour(new ResourceManagerAgentInitFIPAReqUnitM(this,msg));

    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    //if(request == ResMRequests.RequestWorker){
      //set a conversatinon id for this FIPA-Request
      msg.setConversationId(ConverId.UnitM.NeedWorker.getConId());
      msg.setContent(ConverId.UnitM.NeedWorker.getConId());
      //System.out.println(this.getLocalName() + " >>> REQUEST: for a worker");
    //}
   
    msg.addReceiver(receiver);
    this.send(msg);

  }//end sendRequestFor

}//end ResourceManagerAgent



