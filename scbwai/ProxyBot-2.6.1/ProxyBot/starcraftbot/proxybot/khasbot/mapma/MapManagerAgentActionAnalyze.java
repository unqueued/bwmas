/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package starcraftbot.proxybot.khasbot.mapma;

import jade.core.behaviours.*;
import java.util.*;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;

/**
 *
 * @author Antonio Arredondo
 */
public class MapManagerAgentActionAnalyze extends Behaviour {

  MapManagerAgent agent = null;
  DataStore ds = null;
  MapLocation loc = null;

  public MapManagerAgentActionAnalyze(MapManagerAgent a, MapLocation location) {
    super(a);
    agent = a;
    loc = location;
  }

  @Override
  public void action() {
    
    /* find the nearest mineral field from out given map location */
    ArrayList<UnitObject> minerals = agent.minerals;
    ArrayList<UnitObject> gas = agent.gas;

    UnitObject temp = null;

    //for distance calculation I am going to assume that the map distance is no
    //greater than 500 diagonally across
    for( int i =0; i < minerals.size(); i++){
      temp = minerals.get(i);
      temp.setLoc(loc);
      minerals.set(i, temp);
    }

    //sort the minerals
    UnitObject.heapSort(minerals);

    System.out.println("Mineral List:");
    for(UnitObject obj: minerals)
      System.out.println("\tID: " + obj.getID() + " dist: " + obj.distance());

    //for distance calculation I am going to assume that the map distance is no
    //greater than 500 diagonally across
//    for( int i =0; i < gas.size(); i++){
//      temp = gas.get(i);
//      temp.setLoc(loc);
//      gas.set(i, temp);
//    }
//
//    //sort the minerals
//    UnitObject.heapSort(gas);
  }

  @Override
  public boolean done(){
    return true;
  }
  
}
