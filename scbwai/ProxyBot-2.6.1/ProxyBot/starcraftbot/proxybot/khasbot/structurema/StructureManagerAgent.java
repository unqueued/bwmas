/**
 * Agent Description:
 * 	Controls all the structure Units, this includes training new units and researching tech
 * 
 * Communicates with:
 * 	<-> CommanderAgent
 *  <-> UnitManagerAgent
 *  
 * Associated Agents:
 * 	@see CommanderAgent
 *  @see UnitManagerAgent
 */
package starcraftbot.proxybot.khasbot.structurema;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

@SuppressWarnings("serial")
public class StructureManagerAgent extends KhasBotAgent{

	protected void setup(){
    super.setup();

    StructureManagerAgentRespInfUnitM resp_inf_unitm =
            new StructureManagerAgentRespInfUnitM(this,unitm_inform_mt);
    StructureManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new StructureManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);

    resp_inf_unitm.setDataStore(myDS);
    resp_fipa_req_unitm.setDataStore(myDS);

   
    addThreadedBehaviour(resp_inf_unitm);
    addThreadedBehaviour(resp_fipa_req_unitm);
	}

//	public void setGameObject(GameObject g)
//	{
//		gameObj = g;
//	}
//
//  public void setGameObjectUpdate(GameObjectUpdate g)
//	{
//		gameObjUp = g;
//	}

  public boolean canBuildNewUnit(){
    return false;
  }

  public boolean canUpgradeTechLevel(){
    return false;
  }

  public boolean canResearchTechLevel(){
    return false;
  }


}//end StructureManagerAgent

