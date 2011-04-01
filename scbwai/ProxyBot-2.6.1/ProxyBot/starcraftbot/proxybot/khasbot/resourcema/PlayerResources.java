
package starcraftbot.proxybot.khasbot.resourcema;

import java.io.Serializable;

public class PlayerResources implements Serializable {

  private int minerals;
  private int gas;
  private int supplyUsed;
  private int supplyTotal;

  /**
   * This is to update the attributes the player has. This is
   * not related to any of the gas or mineral objects. This is
   * just player game environment changes.
   * @param in_minerals
   * @param in_gas
   * @param in_supplyUsed
   * @param in_supplyTotal
   */
  public void update(String in_minerals, String in_gas, String in_supplyUsed, String in_supplyTotal){
    minerals = Integer.parseInt(in_minerals);
    gas = Integer.parseInt(in_gas);
    supplyUsed =  Integer.parseInt(in_supplyUsed);
    supplyTotal = Integer.parseInt(in_supplyTotal);
  }

  public int getMinerals()
  {
	  return minerals;
  }
  public int getGas()
  {
	  return gas;
  }
  public int getSupply()
  {
	  return supplyUsed;
  }
  public int getSupplyTotal()
  {
	  return supplyTotal;
  }
  
  public String toString()
  {
	  return "PlayerResources:Obj: " + "Minerals: " + minerals + " " +
                                     "Gas: " + gas + " " +
                                     "supplyUsed: " + supplyUsed + " " +
                                     "supplyTotal: " + supplyTotal ;
  }

}

