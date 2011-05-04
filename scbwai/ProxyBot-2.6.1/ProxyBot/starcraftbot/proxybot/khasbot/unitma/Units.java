/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package starcraftbot.proxybot.khasbot.unitma;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Antonio Arredondo
 */
public class Units implements Serializable {


  /*
   * the hashmap key off the Unit.java class where the unit type num is unique.
   */
  HashMap<Integer,ArrayList<UnitObject>> myPlayersUnits = null;
  HashMap<Integer,ArrayList<UnitObject>> enemyPlayersUnits = null;
  HashMap<Integer,ArrayList<UnitObject>> neutralPlayersUnits = null;

	public Units(){
    //this will create the arraylist to store all game units

    //to save us from a massive (read 230 size initial static)
    //call for all the Unit.java values, we will just start with
    //the unit type Mineral field, since we WILL have a mineral
    //patch somewhere

    myPlayersUnits = new HashMap<Integer, ArrayList<UnitObject>>();
    enemyPlayersUnits = new HashMap<Integer, ArrayList<UnitObject>>();
    neutralPlayersUnits = new HashMap<Integer, ArrayList<UnitObject>>();

  }

	/**
	 * creates a new ArrayList of the units it parses from 'update' in game.
   * This list will be my players units.
	 *
	 * @param update
   * @param myPlayerID
   * @param game
	 * @return newList
	 */
	public void parseUpdateUnits(String update, int myPlayerID, int enemyPlayerID)	{
		String[] unitDatas = update.split(":");
		boolean first = true;

		for (String data : unitDatas) {
			if (first) {
				first = false;
				continue;
			}

      //this array list will point to the Number(amount) of
      //units of this type you have
      ArrayList<UnitObject> unit_list = null;

      //unit object to be added to the list
			UnitObject unit = new UnitObject();

//      System.out.println("msg data> " + data);

			String[] attributes = data.split(";");

			int ID = Integer.parseInt(attributes[0]);
//      System.out.println("ID> " + ID);
      
      //player id
			int pID = Integer.parseInt(attributes[1]);
			int unit_type = Integer.parseInt(attributes[2]);

//      System.out.println("pID> " + pID);
//      System.out.println("utype> " + unit_type);


			unit.setID(Integer.parseInt(attributes[0]));
      //unit.setPlayerID(attributes[1]);  //skipped since separate units for player and enemy
			unit.setType(Integer.parseInt(attributes[2]));  //skipped since the units are stored by type (key) in a hashmap
			unit.setRealX(Integer.parseInt(attributes[3]));
			unit.setRealY(Integer.parseInt(attributes[4]));
			unit.setHitPoints(Integer.parseInt(attributes[5]));
			unit.setShields(Integer.parseInt(attributes[6]));
			unit.setEnergy(Integer.parseInt(attributes[7]));
			unit.setBuildTimer(Integer.parseInt(attributes[8]));
			unit.setTrainTimer(Integer.parseInt(attributes[9]));
			unit.setResearchTimer(Integer.parseInt(attributes[10]));
			unit.setUpgradeTimer(Integer.parseInt(attributes[11]));
			unit.setOrderTimer(Integer.parseInt(attributes[12]));
			unit.setOrder(Integer.parseInt(attributes[13]));
			//unit.lifted = (Integer.parseInt(attributes[14]) != 0);
			unit.setResources(Integer.parseInt(attributes[14]));
			unit.setAddonID(Integer.parseInt(attributes[15]));
			unit.setMineCount(Integer.parseInt(attributes[16]));

      /* moved these above the add, old location was after the add */
      unit.setX(unit.getRealX() / 32);
			unit.setY(unit.getRealY() / 32);

      /* now test for Minerals and Gas and assign them to the Neutral player */
      if(Unit.getUnit(unit_type) == Unit.Resource_Mineral_Field ||
         Unit.getUnit(unit_type) == Unit.Resource_Vespene_Geyser ){
        if(neutralPlayersUnits.containsKey((int)unit_type)) {
          //key exits so get the ArrayList and add the new UnitObject
          unit_list = (ArrayList<UnitObject>)neutralPlayersUnits.remove(unit_type);
          //this insert must be done to avoid adding duplicate unit ids for the same unit
          unit_list = unitInsertIntoUnits(unit_list,unit);
          //unit_list.trimToSize();
          neutralPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          //unit_list.trimToSize();
          neutralPlayersUnits.put(unit_type,unit_list);
        }
      }
      /* I will only put units into my map that belong to me. All the other ones will go into
       * netural.
       * BUG: the playerID of neutral is not coming in properly, so i'm going to set it here
       */
      /*
       *  NOTE: if i'm not creating a hashmap everytime, then I must check for the units based 
       *  on their id so that i don't put duplicates
       */
      if(pID == myPlayerID){
        /* since hashmap is dynamically generated, we must check for the key */
        if(myPlayersUnits.containsKey((int)unit_type)) {
          //key exits so get the ArrayList and add the new UnitObject
          unit_list = (ArrayList<UnitObject>)myPlayersUnits.remove(unit_type);
          //this insert must be done to avoid adding duplicate unit ids for the same unit
          unit_list = unitInsertIntoUnits(unit_list,unit);
          //unit_list.trimToSize();
          myPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          //unit_list.trimToSize();
          myPlayersUnits.put(unit_type,unit_list);
        }
      } else if(pID == enemyPlayerID){
        /* since hashmap is dynamically generated, we must check for the key */
        if(enemyPlayersUnits.containsKey((int)unit_type)) {
          //key exits so get the ArrayList and add the new UnitObject
          unit_list = (ArrayList<UnitObject>)enemyPlayersUnits.remove(unit_type);
          //this insert must be done to avoid adding duplicate unit ids for the same unit
          unit_list = unitInsertIntoUnits(unit_list,unit);
          //unit_list.trimToSize();
          enemyPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          //unit_list.trimToSize();
          enemyPlayersUnits.put(unit_type,unit_list);
        }
      }
      
		}//end for 
		
		
	
		
	}//end parseUpdateUnits


  /**
   * This method will check to see if the unit with the unit_id is already in the arraylist.
   */
  private ArrayList<UnitObject> unitInsertIntoUnits(ArrayList<UnitObject> unit_list, UnitObject unit){
    boolean found = false;
    for(UnitObject u : unit_list){
      if(u.getID() == unit.getID()){
        found=true;
        //update the changes that has to be done for this unit
        u.setID(unit.getID());

        //u.setPlayerID(unit);  //skipped since separate units for player and enemy
        u.setType(unit.getType().getNumValue());  
        u.setRealX(unit.getRealX());
        u.setRealY(unit.getRealY());
        u.setHitPoints(unit.getHitPoints());
        u.setShields(unit.getShields());
        u.setEnergy(unit.getEnergy());
        u.setBuildTimer(unit.getBuildTimer());
        u.setTrainTimer(unit.getTrainTimer());
        u.setResearchTimer(unit.getResearchTimer());
        u.setUpgradeTimer(unit.getUpgradeTimer());
        u.setOrderTimer(unit.getOrderTimer());
        u.setOrder(unit.getOrder());
        //u.lifted = (Integer.parseInt(unit) != 0);
        u.setResources(unit.getResources());
        u.setAddonID(unit.getAddonID());
        u.setMineCount(unit.getMineCount());

        /* moved these above the add, old location was after the add */
        u.setX(unit.getRealX() / 32);
        u.setY(unit.getRealY() / 32);
        break;
      }
    }//end for
    if(!found) 
      unit_list.add(unit);
    return unit_list;
  }//end unitInsertIntoUnits


  //
  // my player's units
  //

  /*
   * This method will return a specific arraylist based on the Unit passed in.
   */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* get only structures from the unit list */
  @SuppressWarnings("unchecked")
  public HashMap<Integer,ArrayDeque<UnitObject>> getMyPlayersStructureUnits(){

    /**
     *  Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
     */
    HashMap<Integer,ArrayDeque<UnitObject>> structures = new HashMap<Integer, ArrayDeque<UnitObject>>();

    //get only the keys in the hashmap
    for(Iterator itr = myPlayersUnits.keySet().iterator(); itr.hasNext(); ){
      Unit key = Unit.getUnit(Integer.parseInt(itr.next().toString()));

      switch(key) {
        case Terran_Command_Center:
        case Terran_Comsat_Station:
        case Terran_Nuclear_Silo:
        case Terran_Supply_Depot:
        case Terran_Refinery:
        case Terran_Barracks:
        case Terran_Academy:
        case Terran_Factory:
        case Terran_Starport:
        case Terran_Control_Tower:
        case Terran_Science_Facility:
        case Terran_Covert_Ops:
        case Terran_Physics_Lab:
        case Terran_Machine_Shop:
        case Terran_Engineering_Bay:
        case Terran_Armory:
        case Terran_Missile_Turret:
        case Terran_Bunker:
        
        case Zerg_Infested_Command_Center:
        case Zerg_Hatchery:
        case Zerg_Lair:
        case Zerg_Hive:
        case Zerg_Nydus_Canal:
        case Zerg_Hydralisk_Den:
        case Zerg_Defiler_Mound:
        case Zerg_Greater_Spire:
        case Zerg_Queen_s_Nest:
        case Zerg_Evolution_Chamber:
        case Zerg_Ultralisk_Cavern:
        case Zerg_Spire:
        case Zerg_Spawning_Pool:
        case Zerg_Creep_Colony:
        case Zerg_Spore_Colony:
        case Zerg_Sunken_Colony:
        case Zerg_Extractor:
        
        case Protoss_Nexus:
        case Protoss_Robotics_Facility:
        case Protoss_Pylon:
        case Protoss_Assimilator:
        case Protoss_Observatory:
        case Protoss_Gateway:
        case Protoss_Photon_Cannon:
        case Protoss_Citadel_of_Adun:
        case Protoss_Cybernetics_Core:
        case Protoss_Templar_Archives:
        case Protoss_Forge:
        case Protoss_Stargate:
        case Protoss_Fleet_Beacon:
        case Protoss_Arbiter_Tribunal:
        case Protoss_Robotics_Support_Bay:
        case Protoss_Shield_Battery:
          structures.put(key.getNumValue(), new ArrayDeque(myPlayersUnits.get(key.getNumValue())));
      }
    }

    return structures;
  }

  /**
   *  get only non-structures from the unit list
   *  
   *  return structure is like:
   *  		Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
   *  
   *  @return HashMap<Integer, ArrayList<UnitObject>> 
   */
  public HashMap<Integer,ArrayList<UnitObject>> getMyPlayersNonStructureUnits(){

    /**
     *  Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
     */
    HashMap<Integer,ArrayList<UnitObject>> nonstructures = new HashMap<Integer, ArrayList<UnitObject>>();

    //get only the keys in the hashmap
    for(Iterator itr = myPlayersUnits.keySet().iterator(); itr.hasNext(); ){
      Unit key = Unit.getUnit(Integer.parseInt(itr.next().toString()));

      switch(key) {
        case Terran_Marine:
        case Terran_Ghost:
        case Terran_Vulture:
        case Terran_Goliath:
        case Terran_Siege_Tank_Tank_Mode:
        case Terran_SCV:
        case Terran_Wraith:
        case Terran_Science_Vessel:
        case Terran_Dropship:
        case Terran_Battlecruiser:
        case Terran_Vulture_Spider_Mine:
        case Terran_Nuclear_Missile:
        case Terran_Siege_Tank_Siege_Mode:
        case Terran_Firebat:
        case Spell_Scanner_Sweep:
        case Terran_Medic:
        case Terran_Valkyrie:

        case Zerg_Larva:
        case Zerg_Egg:
        case Zerg_Zergling:
        case Zerg_Hydralisk:
        case Zerg_Ultralisk:
        case Zerg_Broodling:
        case Zerg_Drone:
        case Zerg_Overlord:
        case Zerg_Mutalisk:
        case Zerg_Guardian:
        case Zerg_Queen:
        case Zerg_Defiler:
        case Zerg_Scourge:
        case Zerg_Infested_Terran:
        case Zerg_Cocoon:
        case Zerg_Devourer:
        case Protoss_Corsair:
        case Protoss_Dark_Templar:
        case Protoss_Dark_Archon:
        case Protoss_Probe:
        case Protoss_Zealot:
        case Protoss_Dragoon:
        case Protoss_High_Templar:
        case Protoss_Archon:
        case Protoss_Shuttle:
        case Protoss_Scout:
        case Protoss_Arbiter:
        case Protoss_Carrier:
        case Protoss_Interceptor:
        case Protoss_Reaver:
        case Protoss_Observer:
        case Protoss_Scarab:

        case Critter_Rhynadon:
        case Critter_Bengalaas:
        case Critter_Scantid:
        case Critter_Kakaru:
        case Critter_Ragnasaur:
        case Critter_Ursadon:
        case Special_Crashed_Norad_II:
        case Special_Ion_Cannon:
        case Special_Overmind_With_Shell:
        case Special_Overmind:
        case Special_Mature_Chrysalis:
        case Special_Cerebrate:
        case Special_Cerebrate_Daggoth:
        case Special_Stasis_Cell_Prison:
        case Special_Khaydarin_Crystal_Form:
        case Special_Protoss_Temple:
        case Special_XelNaga_Temple:
        case Resource_Mineral_Field:
        case Resource_Vespene_Geyser:
        case Special_Warp_Gate:
        case Special_Psi_Disrupter:
        case Special_Power_Generator:
        case Special_Overmind_Cocoon:
        case Spell_Dark_Swarm:
        case None:
        case Unknown:
          nonstructures.put(key.getNumValue(), myPlayersUnits.get(key.getNumValue()));
       }
    }
    
    return nonstructures;
  }

  
  //
  // Enemy units
  //
  /* neutral units */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  //
  // Neutral units
  //
  /* neutral units */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /**
   * This method determines if a unit type is a structure.
   * This one takes in a Unit and makes a call to is
   * @param type - Unit is passed in and a call to isStructure(int type) is done.
   * @return
   * @see isStructure(int type)
   */
  public boolean isStructure(Unit type){
    return isStructure(type.getNumValue());
  }
  
  /**
   * This method determines if a unit type is a structure.
   * This takes an integer and determines of that int is a Unit that is a structure.
   * @param type
   * @return
   */
  public boolean isStructure(int type){

      Unit key = Unit.getUnit(type);

      switch(key) {
        case Terran_Command_Center:
        case Terran_Comsat_Station:
        case Terran_Nuclear_Silo:
        case Terran_Supply_Depot:
        case Terran_Refinery:
        case Terran_Barracks:
        case Terran_Academy:
        case Terran_Factory:
        case Terran_Starport:
        case Terran_Control_Tower:
        case Terran_Science_Facility:
        case Terran_Covert_Ops:
        case Terran_Physics_Lab:
        case Terran_Machine_Shop:
        case Terran_Engineering_Bay:
        case Terran_Armory:
        case Terran_Missile_Turret:
        case Terran_Bunker:

        case Zerg_Infested_Command_Center:
        case Zerg_Hatchery:
        case Zerg_Lair:
        case Zerg_Hive:
        case Zerg_Nydus_Canal:
        case Zerg_Hydralisk_Den:
        case Zerg_Defiler_Mound:
        case Zerg_Greater_Spire:
        case Zerg_Queen_s_Nest:
        case Zerg_Evolution_Chamber:
        case Zerg_Ultralisk_Cavern:
        case Zerg_Spire:
        case Zerg_Spawning_Pool:
        case Zerg_Creep_Colony:
        case Zerg_Spore_Colony:
        case Zerg_Sunken_Colony:
        case Zerg_Extractor:

        case Protoss_Nexus:
        case Protoss_Robotics_Facility:
        case Protoss_Pylon:
        case Protoss_Assimilator:
        case Protoss_Observatory:
        case Protoss_Gateway:
        case Protoss_Photon_Cannon:
        case Protoss_Citadel_of_Adun:
        case Protoss_Cybernetics_Core:
        case Protoss_Templar_Archives:
        case Protoss_Forge:
        case Protoss_Stargate:
        case Protoss_Fleet_Beacon:
        case Protoss_Arbiter_Tribunal:
        case Protoss_Robotics_Support_Bay:
        case Protoss_Shield_Battery:
          return true;
        default:
          return false;

    }
  }

}
