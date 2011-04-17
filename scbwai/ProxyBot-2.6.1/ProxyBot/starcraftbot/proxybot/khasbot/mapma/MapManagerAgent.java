
package starcraftbot.proxybot.khasbot.mapma;


import jade.content.lang.sl.SLCodec;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import java.util.logging.*;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

import java.io.*;
import java.util.*;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class MapManagerAgent extends KhasBotAgent {

  ArrayList<UnitObject> minerals = null;
  ArrayList<UnitObject> gas = null;
  MapObject map = null;

  MapLocation myStartLocation = null;

  boolean init = false;

  @Override
	protected void setup(){
    super.setup();

    this.codec = new SLCodec();
    this.getContentManager().registerLanguage(this.codec);
    this.ontology=JADEManagementOntology.getInstance();
    this.getContentManager().registerOntology(this.ontology);

    MapManagerAgentRespInfUnitM resp_inf_unitm =
            new MapManagerAgentRespInfUnitM(this,unitm_inform_mt);
    MapManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new MapManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);
    MapManagerAgentRespFIPAReqResM resp_fipa_req_resm =
            new MapManagerAgentRespFIPAReqResM(this,resm_fipa_req_mt);
//    MapManagerAgentRespFIPAReqCmd resp_fipa_req_cmd =
//            new MapManagerAgentRespFIPAReqCmd(this,cmd_fipa_req_mt);

    resp_inf_unitm.setDataStore(myDS);
    resp_fipa_req_unitm.setDataStore(myDS);
    resp_fipa_req_resm.setDataStore(myDS);
//    resp_fipa_req_cmd.setDataStore(myDS);

    addThreadedBehaviour(resp_inf_unitm);
    addThreadedBehaviour(resp_fipa_req_unitm);
    addThreadedBehaviour(resp_fipa_req_resm);
//    addThreadedBehaviour(resp_fipa_req_cmd);
	}

  
	public void parseGameObject(GameObject g)
	{
    //set the map object
    map = g.getMapObj();

    //get the start location
    myStartLocation = map.getStartLocation(g.getMyPlayer().getPlayerID());

    /* here setup the list of minerals and the gas geyers */
    minerals = g.getUnitsInGame().getNeutralPlayersUnit(Unit.Resource_Mineral_Field);
    gas = g.getUnitsInGame().getNeutralPlayersUnit(Unit.Resource_Vespene_Geyser);

    if( !init ){
      init = true;
      MapManagerAgentActionAnalyze map_analyze = new MapManagerAgentActionAnalyze(this,myStartLocation);
      addThreadedBehaviour(map_analyze);
    }
	}

  public void PathToChokePoint(){
    
  } 
 
  public void PathToNewBuilding(){
    
  } 

//  public void setMineralList(ArrayList<UnitObject> mineralList) {
//
//  }

  /**
   * This method returns a list of mineral patches in sorted order from closet to furthest.
   * The list uses the initial starting location to compute the distance, which can be
   * changed by calling the setLoc() on the UnitObject.
   * @return
   */
  public ArrayList<UnitObject> NearestMinerals(){
    return minerals;
  } 

//  public void setGasList(ArrayList<UnitObject> gasList) {
//
//  }
  /**
   * This method returns a list of gas geyers in sorted order from closet to furthest.
   * The list uses the initial starting location to compute the distance, which can be
   * changed by calling the setLoc() on the UnitObject.
   * @return
   */
  public ArrayList<UnitObject> NearestGas(){
    return gas;
  } 

  public void EndGamePhaseOne(){
    
  }

  public void StartGamePhaseTwo(){
    
  }

  public void EndGamePhaseTwo(){
    
  }

  public void StartGamePhaseThree(){
    
  }


  
}//end MapManagerAgent



