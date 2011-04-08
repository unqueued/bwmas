
package starcraftbot.proxybot.khasbot.mapma;


import java.util.logging.Level;
import java.util.logging.Logger;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.game.GameObjectUpdate;
import starcraftbot.proxybot.khasbot.KhasBotAgent;

import java.io.*;

@SuppressWarnings("serial")
public class MapManagerAgent extends KhasBotAgent {
		
  @Override
	protected void setup(){
    super.setup();

    MapManagerAgentRespInfCmd resp_inf_cmd =
            new MapManagerAgentRespInfCmd(this,commander_inform_mt);
    MapManagerAgentRespFIPAReqUnitM resp_fipa_req_unitm =
            new MapManagerAgentRespFIPAReqUnitM(this,unitm_fipa_req_mt);
    MapManagerAgentRespFIPAReqResM resp_fipa_req_resm =
            new MapManagerAgentRespFIPAReqResM(this,resm_fipa_req_mt);
    MapManagerAgentRespFIPAReqCmd resp_fipa_req_cmd =
            new MapManagerAgentRespFIPAReqCmd(this,cmd_fipa_req_mt);

    resp_fipa_req_unitm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_resm.setDataStore(resp_inf_cmd.getDataStore());
    resp_fipa_req_cmd.setDataStore(resp_inf_cmd.getDataStore());

    addThreadedBehaviour(resp_inf_cmd);
    addThreadedBehaviour(resp_fipa_req_unitm);
    addThreadedBehaviour(resp_fipa_req_resm);
    addThreadedBehaviour(resp_fipa_req_cmd);
	}

  @Override
	protected void setGameObject(GameObject g)
	{
		gameObj = g;

    try {
      //FileOutputStream output = new FileOutputStream(new File("map-data" + System.currentTimeMillis() + ".txt"));
      BufferedWriter output = new BufferedWriter(new FileWriter(new File("map-data" + System.currentTimeMillis() + ".txt")));
      output.write(gameObj.getMapObj().toString());
      output.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(MapManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
    }catch (IOException ex) {
        Logger.getLogger(MapManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
    }

	}
  
  @Override
  protected void setGameObjectUpdate(GameObjectUpdate g) {
    gameObjUp = g;
  }

  public void PathToChokePoint(){
    
  } 
 
  public void PathToNewBuilding(){
    
  } 
 
  public void NearestMinerals(){
    
  } 
 
  public void NearestGas(){
    
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



