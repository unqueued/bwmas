/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;


import jade.core.behaviours.DataStore;
import jade.core.behaviours.SimpleBehaviour;

import java.util.ArrayList;
import java.util.HashMap;

import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 * This class should be instantiated every time a FIPA-Request is made.
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentActionGatherMinerals extends SimpleBehaviour{

  ResourceManagerAgent agent = null;
  DataStore ds = null;
  boolean RequestWorker = false;
  boolean RequestMinerals = false;
  boolean RequestGas = false;
  final int GameInitWorkers = 4;
  int requestCounter = 0;

  public ResourceManagerAgentActionGatherMinerals(ResourceManagerAgent a){
    super(a);
    agent = a;
    ds = agent.getDS();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void action(){
    //Retrieve the GameObject from the datastore
    GameObject lgameObj = (GameObject) ds.get("gameObj");

    if(lgameObj != null){
      if(requestCounter < GameInitWorkers){
        agent.requestWorker(1);
        requestCounter++;
      }
    }
  }

  public void duplicateWorkerFound(){
    requestCounter--;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean done(){
  
    if(agent.numOfWorkers() == GameInitWorkers){
      ArrayList<UnitObject> my_units = (ArrayList<UnitObject>)ds.get("my_units");
      ArrayList<UnitObject> minerals = (ArrayList<UnitObject>)ds.get("minerals");
      HashMap<Integer,GameCommand> unit_last_cmd = (HashMap<Integer,GameCommand>)ds.get("unit_last_cmd");

      if(minerals == null){
        System.out.println("No minerals for workers!!! ### Requesting minerallist");
        agent.requestMineralList();
        return false;
      }

      if(my_units == null){
        System.out.println("No workers!!!");
        return false;
      }

      if(unit_last_cmd == null){
        unit_last_cmd = new HashMap<Integer,GameCommand>();
      }

      System.out.println("I have enough workers!!!");
      System.out.println("### Sending workers");
      ArrayList<GameCommand> lcommandsToDo = new ArrayList<GameCommand>();
      if(minerals != null && my_units != null){
        UnitObject mineralPatch = minerals.get(0);

        for(UnitObject u : my_units){
          //assign only 3 workers per mineral patch
          lcommandsToDo.add(GameCommand.rightClickUnit(u.getID(), mineralPatch.getID()));
          //store the last command that the unit executed
          unit_last_cmd.put(u.getID(),GameCommand.rightClickUnit(u.getID(), mineralPatch.getID()));
        }
        agent.requestCommandsExe(lcommandsToDo);
      }
      return true;
    }else{
      return false;
    }
  }

  
}
