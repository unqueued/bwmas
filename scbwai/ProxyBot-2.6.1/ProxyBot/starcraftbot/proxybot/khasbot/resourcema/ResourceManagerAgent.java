
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
public class ResourceManagerAgent extends KhasBotAgent {

  //this will maintain a list of worker units
  //    this HashMap will use <k,v> as <unit id num,UnitObject>
  HashMap<Integer,UnitObject> my_units = null;

  ArrayList<UnitObject> minerals = null;
  
  GameCommandQueue commandsToDo = null;
  
  boolean RequestingWorker = false;
  
  ResourceManagerAgentInitFIPAReqUnitM unitm_Init = null;

  @Override
	protected void setup(){
    super.setup();

    this.my_units = new HashMap<Integer,UnitObject>();
    
    this.minerals = new ArrayList<UnitObject>();
  
    this.commandsToDo = new GameCommandQueue();
    
    //this.RequestingWorker = false;
    
    this.myDS.put(getLocalName()+"agent", this);
    
    ResourceManagerAgentRespInfCmd resp_inf_cmd =
            new ResourceManagerAgentRespInfCmd(this,this.commander_inform_mt);
    ResourceManagerAgentActionGatherResources gather_res =
            new ResourceManagerAgentActionGatherResources(this);
    ResourceManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new ResourceManagerAgentRespFIPAReqUnitM(this,this.unitm_fipa_req_mt);
//    ResourceManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
//            new ResourceManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt);

    resp_inf_cmd.setDataStore(this.myDS);
    gather_res.setDataStore(this.myDS);
    resp_fipa_req_unitm.setDataStore(this.myDS);
    
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
	  	System.out.println(getLocalName()+"Setting GameObject?");
		this.gameObj = this.getGameObject();
		this.gameObj = g;
		//myDS.put("game", gameObj);
		
		if(!(this.gameObj == null))
		{
			//System.out.println(getLocalName() +":: gameObj is->"+this.gameObj +" g is ->"+g);
			//this.myDS.put(getLocalName()+"game", this.gameObj);
			Units us = this.gameObj.getUnitsInGame();
			if(us != null)
			{	//System.out.println("Units in game is NULL wtf?!");
				ArrayList<UnitObject> min = us.getNeutralPlayersUnit(Unit.NonStructure.Neutral.Resource_Mineral_Field);
				if(min != null)
				{	
					for(UnitObject u : min)//gameObj.getUnitsInGame().getNeutralPlayersUnit(Unit.NonStructure.Neutral.Resource_Mineral_Field))
					{
						if(!this.minerals.contains(u))
							this.minerals.add(u);
					}
				}	
			}
			this.myDS.put(getLocalName()+"agent", this);
		}
		else
			System.out.println(getLocalName() + "::just set game object, and yet... its null.");
		
	}

  @Override
  public void setGameObjectUpdate(GameObjectUpdate g)
	{
		this.gameObjUp = g;
		if(this.gameObjUp != null)
		{
			this.myDS.put(getLocalName()+"gameUpdate", this.gameObjUp);
			for(UnitObject u : this.gameObjUp.getUnitsInGame().getNeutralPlayersUnit(Unit.NonStructure.Neutral.Resource_Mineral_Field))
			{
				if(!this.minerals.contains(u))
					this.minerals.add(u);
			}
			this.myDS.put(getLocalName()+"agent", this);
		}
	}

  public void addWorker(UnitObject worker){
	this.my_units = ((ResourceManagerAgent)this.myDS.get(getLocalName()+"agent")).getMyUnits();  
    this.my_units.put(worker.getID(),worker);
    this.RequestingWorker = false;
    this.myDS.put(getLocalName()+"agent", this);
  }

  public HashMap<Integer, UnitObject> getMyUnits(){
	  return this.my_units;
  }
  
  public int numOfWorkers(){
    return this.my_units.size();
  }

  /* message request methods */
  public void canEnoughResourcesToBuild(){

  }
  
  public void requestWorker(){
    this.sendRequestFor(ResMRequests.RequestWorker,unit_manager);
  }
  
  public void requestCommandsExe(){
	  this.sendRequestFor(ResMRequests.doCommands, unit_manager);
  }

  /* message processing methods */
  public void sendRequestFor(ResMRequests request, AID receiver){

	  
	  
    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

    if(request == ResMRequests.RequestWorker){
      //set a conversatinon id for this FIPA-Request
      //this.getDS().put(this.getLocalName()+"reqWorker", true);
      this.RequestingWorker = ((ResourceManagerAgent)this.myDS.get(getLocalName()+"agent")).RequestingWorker;
      msg.setConversationId(ConverId.UnitM.NeedWorker.getConId());
      msg.setContent(ConverId.UnitM.NeedWorker.getConId());
    }
    else if(request == ResMRequests.doCommands)
    {
    	msg.setConversationId(ConverId.UnitM.NewCommands.getConId());
    	try {
			msg.setContentObject(this.commandsToDo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    msg.addReceiver(receiver);
    this.send(msg);

    
    if(!this.RequestingWorker || request== ResMRequests.doCommands)
    {
    	if(!this.RequestingWorker && request == ResMRequests.RequestWorker)
    		this.RequestingWorker = true;
	    ResourceManagerAgentInitFIPAReqUnitM init_fipa_req_unitm = 
	            new ResourceManagerAgentInitFIPAReqUnitM(this,msg);
	
	    init_fipa_req_unitm.setDataStore(this.getDS());//myDS);
	
	    this.addThreadedBehaviour(init_fipa_req_unitm);
	    
	    this.myDS.put(getLocalName()+"agent", this);
    }
 }//end sendRequestFor

}//end ResourceManagerAgent



