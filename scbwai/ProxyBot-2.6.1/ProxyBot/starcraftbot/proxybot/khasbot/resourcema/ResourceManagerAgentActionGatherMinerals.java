/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;
import jade.tools.introspector.gui.MyDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import starcraftbot.proxybot.CommandId;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.game.GameObject;
import starcraftbot.proxybot.khasbot.unitma.Unit;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;
import starcraftbot.proxybot.khasbot.unitma.Units;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentActionGatherMinerals extends SimpleBehaviour {

  ResourceManagerAgent agent = null;
  DataStore ds = null;
  boolean RequestWorker = false;
  boolean RequestMinerals = false;
  boolean RequestGas = false;

  final int GameInitWorkers = 4;

  public ResourceManagerAgentActionGatherMinerals(ResourceManagerAgent a) {
    super(a);
    agent = a;
    ds = agent.getDS();
    
  }


  @Override
  public void action() {
    //Retrieve the GameObject from the datastore
    GameObject lgameObj = (GameObject)ds.get("gameObj");
  
    if( lgameObj != null ){
//      assert ds.get("RequestWorker") == null: "ds.get(RequestWorker) cannot be null";
      //RequestWorker = ((Boolean)ds.get("RequestWorker")).booleanValue();
      if (agent.numOfWorkers() < GameInitWorkers && !RequestWorker) {
        agent.requestWorker();
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean done(){
    if( agent.numOfWorkers() == GameInitWorkers ){
      RequestWorker = false;

      ArrayList<UnitObject> my_units = (ArrayList<UnitObject>)ds.get("my_units");
      ArrayList<UnitObject> minerals = (ArrayList<UnitObject>)ds.get("minerals");

      ArrayList<GameCommand> lcommandsToDo = new  ArrayList<GameCommand>();

      if(minerals != null && my_units != null){
//        if(!minerals.isEmpty() && !my_units.isEmpty()){
          //for(UnitObject mineralPatch : minerals){
            UnitObject mineralPatch = minerals.get(0);

            for(UnitObject u : my_units){
              //assign only 3 workers per mineral patch
              lcommandsToDo.add(GameCommand.rightClickUnit(u.getID(),mineralPatch.getID()));
            }
        //}
        agent.requestCommandsExe(lcommandsToDo);
      }
    }
    return agent.numOfWorkers() == GameInitWorkers;
  }

  public void setRequestWorker(boolean flag){
    RequestWorker = flag;
  }

}
