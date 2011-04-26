/**
 * Incorporates Construction Manager and Building Placer from "bwsal".
 * 
 * Agent Description:
 * 	Controls units to build buildings, and communicates with MapManagerAgent to place possible 
 * 	future buildings known from Commander
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 *  <-> UnitManagerAgent
 *  <-  MapManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 *  @see UnitManagerAgent
 *  @see MapManagerAgent
 * 
 */

package starcraftbot.proxybot.khasbot.buildingma;


import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.KhasBotAgent;
import starcraftbot.proxybot.khasbot.mapma.MapObject;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

@SuppressWarnings("serial")
public class BuildingManagerAgent extends KhasBotAgent {

  private MapObject map = null;

  @Override
  protected void setup(){
    super.setup();

    myDS.put("worker",null);

    BuildingManagerAgentRespInfUnitM resp_inf_unitm =
            new BuildingManagerAgentRespInfUnitM(this,unitm_inform_mt);
    BuildingManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new BuildingManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);

    resp_inf_unitm.setDataStore(myDS);
    resp_fipa_req_unitm.setDataStore(myDS);

    addThreadedBehaviour(resp_inf_unitm);
    addThreadedBehaviour(resp_fipa_req_unitm);

	}//end setup
	
	public void setGameObject(GameObject g){
		map = g.getMapObj();
	}
  
//  public void setGameObjectUpdate(GameObjectUpdate g)
//	{
//		gameObjUp = g;
//	}

  public void addWorker(UnitObject worker){
    myDS.put("worker", worker);
  }
}//end BuildingManagerAgent

