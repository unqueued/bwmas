
package starcraftbot.proxybot.khasbot.structurema;

import java.io.Serializable;

public class PlayerTech implements Serializable{
  int[] researched_tech = null;
  int[] current_tech_level = null;


  public PlayerTech(){
    /* both of these values are HARDCODE due to bwapi const */
    researched_tech = new int[47];
    current_tech_level = new int[63];
    
  }
  
  public void update(String research, String level) {

    //System.out.println("research > " + research);
    //System.out.println("level > " + level);
    updateTechResearch(research);
    updateTechLevel(level);

    //System.out.println("PlayerTech > " + PLayerTech);
  }
  
  public void updateTechResearch(String research){
    //dumping the value should be safe, since we only care about
    //1 and 4
    for(int i=0; i < research.length(); i++) {
      researched_tech[i] = Integer.parseInt(research.charAt(i)+"");
    }
  }

  public void updateTechLevel(String level){
    //dumping the value should be safe, since we only care about
    //0,1,2,3 and 4
    for(int i=0; i < level.length(); i++) {
      current_tech_level[i] = Integer.parseInt(level.charAt(i)+"");
    }
  }

  public boolean isTechResearched(Tech.Researched research_query) {
    if( researched_tech[research_query.getNumValue()] == 4 )
      return true;
    else
      return false;
  }

  /**
   * This MUST be called before isTechResearched.
   * @param research_query
   * @return
   */
  public boolean isTechBeingResearched(Tech.Researched research_query) {
    if( researched_tech[research_query.getNumValue()] == 1 )
      return true;
    else
      return false;
  }

  public int currentTechLevel(Tech.Level level_query) {
    return current_tech_level[level_query.getNumValue()];
  }

  /**
   * This MUST be called before currentTechLevel.
   * @param level_query
   * @return
   */
  public boolean isTechLevelUpgrading(Tech.Level level_query) {
    if( current_tech_level[level_query.getNumValue()] == 4 )
      return true;
    else
      return false;
  }

  @Override
	public String toString()
	{
    String output = "PlayerTech> \n";

    /* print the Tech Research */
    for( Tech.Researched r: Tech.Researched.values() ){
      if( researched_tech[r.getNumValue()] == 4 )
        output += r.name() + ": Tech is researched" + "\n";
      else
        output += r.name() + ": Tech is NOT researched" + "\n";
    }
    
    output += "\n";

    /* print the Tech Research */
    for( Tech.Level l: Tech.Level.values() ){
      if( current_tech_level[l.getNumValue()] != 4 )
        output += l.name() + ": Level " + current_tech_level[l.getNumValue()] + "\n";
      else
        output += l.name() + ": Level is being upgraded\n";
    }

		return output + "\n";
	}

}//end Tech Object

