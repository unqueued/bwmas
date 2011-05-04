
package starcraftbot.proxybot.khasbot.battlema;

import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

@SuppressWarnings("serial")
public class BattleManagerAgent extends KhasBotAgent {

  @Override
	protected void setup(){
    super.setup();

    BattleManagerAgentRespInfUnitM resp_inf_unitm =
            new BattleManagerAgentRespInfUnitM(this,unitm_inform_mt);
    BattleManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new BattleManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);
    BattleManagerAgentRespFIPAReqMapM resp_fipa_req_mapm =
            new BattleManagerAgentRespFIPAReqMapM(this,mapm_fipa_req_mt);
//    BattleManagerAgentRespFIPAReqCmd resp_fipa_req_cmd =
//            new BattleManagerAgentRespFIPAReqCmd(this,cmd_fipa_req_mt);

    resp_fipa_req_unitm.setDataStore(myDS);
    resp_fipa_req_mapm.setDataStore(myDS);
//    resp_fipa_req_cmd.setDataStore(myDS);
    
    addThreadedBehaviour(resp_inf_unitm);
    addThreadedBehaviour(resp_fipa_req_unitm);
    addThreadedBehaviour(resp_fipa_req_mapm);
//    addThreadedBehaviour(resp_fipa_req_cmd);
    
	}

  //unit manager
  public boolean addNewUnit(){
    return false; 
  }


  //commander
  public boolean endPhaseOne(){
    return false; 
  }

//  @Override
//	public void setGameObject(GameObject g)
//	{
//    //System.out.println("GameObject: " + g);
//   	gameObj = g;
//	}

//  @Override
//  public void setGameObjectUpdate(GameObjectUpdate g)
//	{
//    //System.out.println("GameObjectUpdate: " + g);
//		gameObjUp = g;
//    testGameObjUp();
//	}

//  public void testGameObjUp(){
//    if( gameObjUp == null ){
//      //System.out.println("GameObjectUpdate: is null" );
//    }else{
//      //System.out.println("GameObjectUpdate: is NOT null" + gameObjUp);
//    }
//  }

}//end BattleManagerAgent



