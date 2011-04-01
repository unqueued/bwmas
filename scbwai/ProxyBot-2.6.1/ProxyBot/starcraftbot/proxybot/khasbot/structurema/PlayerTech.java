
package starcraftbot.proxybot.khasbot.structurema;

import java.io.Serializable;

public class PlayerTech implements Serializable{

  private TechObject myTech = null;

  public PlayerTech(){

    myTech = new TechObject();
  }
  public void update(String research, String level) {

    //System.out.println("research > " + research);
    //System.out.println("level > " + level);
    myTech.updateTechResearch(research);
    myTech.updateTechLevel(level);

    //System.out.println("myTech > " + myTech);
  }
  
  public String toString() {
    return "PlayerTech:Obj: " + myTech;
  }

}//end Tech Object

