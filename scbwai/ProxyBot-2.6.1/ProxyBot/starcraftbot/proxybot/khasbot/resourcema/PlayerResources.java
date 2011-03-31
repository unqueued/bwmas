
package starcraftbot.proxybot.khasbot.resourcema;

import java.io.Serializable;

public class PlayerResources implements Serializable {

  private String minerals;
  private String gas;
  private String supplyUsed;
  private String supplyTotal;

  public void update(String in_minerals, String in_gas, String in_supplyUsed, String in_supplyTotal){
    minerals = in_minerals;
    gas = in_gas;
    supplyUsed =  in_supplyUsed;
    supplyTotal = in_supplyTotal;
  }

  public String toString()
  {
	  return "PlayerResources";
  }

}

