/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package starcraftbot.proxybot.khasbot.unitma;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
	 * @param game
	 * @return newList
	 */
	public void parseUpdateUnits(String update, int myPlayerID)	{
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
			//unit.setType(attributes[2]);  //skipped since the units are stored by type (key) in a hashmap 
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
          myPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          myPlayersUnits.put(unit_type,unit_list);
        }
      }else if(pID == -1){
        if(neutralPlayersUnits.containsKey((int)unit_type)) {
          //key exits so get the ArrayList and add the new UnitObject
          unit_list = (ArrayList<UnitObject>)neutralPlayersUnits.remove(unit_type);
          //this insert must be done to avoid adding duplicate unit ids for the same unit
          unit_list = unitInsertIntoUnits(unit_list,unit);
          neutralPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          neutralPlayersUnits.put(unit_type,unit_list);
        }
      }else{
        if(enemyPlayersUnits.containsKey((int)unit_type)) {
          //key exits so get the ArrayList and add the new UnitObject
          unit_list = (ArrayList<UnitObject>)enemyPlayersUnits.remove(unit_type);
          //this insert must be done to avoid adding duplicate unit ids for the same unit
          unit_list = unitInsertIntoUnits(unit_list,unit);
          enemyPlayersUnits.put(unit_type, unit_list);
        }else{
          //key doesn't exits so we create the ArrayList and add the new UnitObject
          unit_list = new ArrayList<UnitObject>();
          unit_list.add(unit);
          enemyPlayersUnits.put(unit_type,unit_list);
        }
      }//end player id checks
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
        //u.setType(unit);  //skipped since the units are stored by type (key) in a hashmap 
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
       
      }
    }//end for
    if(!found) 
      unit_list.add(unit);
    return unit_list;
  }//end unitInsertIntoUnits

  //
  // For these definitions look see Unit.java
  //

  //
  // my player's units
  //

  /* neutral units */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.NonStructure.Neutral u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* terran units */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.NonStructure.Terran u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* terran structures */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.Structure.Terran u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* zerg units */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.NonStructure.Zerg u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* zerg structures */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.Structure.Zerg u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* protoss units */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.NonStructure.Protoss u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* protoss structures */
  public ArrayList<UnitObject> getMyPlayersUnit(Unit.Structure.Protoss u){
    return myPlayersUnits.get(u.getNumValue());
  }

  /* get only structures from the unit list */
  public HashMap<Integer,ArrayList<UnitObject>> getMyPlayersStructureUnits(){

    /**
     *  Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
     */
    HashMap<Integer,ArrayList<UnitObject>> structures = new HashMap<Integer, ArrayList<UnitObject>>();

    //get only the keys in the hashmap
    for(Iterator itr = myPlayersUnits.keySet().iterator(); itr.hasNext(); ){
      Enum key = (Enum) itr.next();

      if( key == Unit.Structure.Terran.Terran_Command_Center ||
          key == Unit.Structure.Terran.Terran_Comsat_Station ||
          key == Unit.Structure.Terran.Terran_Nuclear_Silo ||
          key == Unit.Structure.Terran.Terran_Supply_Depot ||
          key == Unit.Structure.Terran.Terran_Refinery ||
          key == Unit.Structure.Terran.Terran_Barracks ||
          key == Unit.Structure.Terran.Terran_Academy ||
          key == Unit.Structure.Terran.Terran_Factory ||
          key == Unit.Structure.Terran.Terran_Starport ||
          key == Unit.Structure.Terran.Terran_Control_Tower ||
          key == Unit.Structure.Terran.Terran_Science_Facility ||
          key == Unit.Structure.Terran.Terran_Covert_Ops ||
          key == Unit.Structure.Terran.Terran_Physics_Lab ||
          key == Unit.Structure.Terran.Terran_Machine_Shop ||
          key == Unit.Structure.Terran.Terran_Engineering_Bay ||
          key == Unit.Structure.Terran.Terran_Armory ||
          key == Unit.Structure.Terran.Terran_Missile_Turret ||
          key == Unit.Structure.Terran.Terran_Bunker ||
          
          key == Unit.Structure.Zerg.Zerg_Infested_Command_Center ||
          key == Unit.Structure.Zerg.Zerg_Hatchery ||
          key == Unit.Structure.Zerg.Zerg_Lair ||
          key == Unit.Structure.Zerg.Zerg_Hive ||
          key == Unit.Structure.Zerg.Zerg_Nydus_Canal ||
          key == Unit.Structure.Zerg.Zerg_Hydralisk_Den ||
          key == Unit.Structure.Zerg.Zerg_Defiler_Mound ||
          key == Unit.Structure.Zerg.Zerg_Greater_Spire ||
          key == Unit.Structure.Zerg.Zerg_Queen_s_Nest ||
          key == Unit.Structure.Zerg.Zerg_Evolution_Chamber ||
          key == Unit.Structure.Zerg.Zerg_Ultralisk_Cavern ||
          key == Unit.Structure.Zerg.Zerg_Spire ||
          key == Unit.Structure.Zerg.Zerg_Spawning_Pool ||
          key == Unit.Structure.Zerg.Zerg_Creep_Colony ||
          key == Unit.Structure.Zerg.Zerg_Spore_Colony ||
          key == Unit.Structure.Zerg.Zerg_Sunken_Colony ||
          key == Unit.Structure.Zerg.Zerg_Extractor ||
          
          key == Unit.Structure.Protoss.Protoss_Nexus ||
          key == Unit.Structure.Protoss.Protoss_Robotics_Facility ||
          key == Unit.Structure.Protoss.Protoss_Pylon ||
          key == Unit.Structure.Protoss.Protoss_Assimilator ||
          key == Unit.Structure.Protoss.Protoss_Observatory ||
          key == Unit.Structure.Protoss.Protoss_Gateway ||
          key == Unit.Structure.Protoss.Protoss_Photon_Cannon ||
          key == Unit.Structure.Protoss.Protoss_Citadel_of_Adun ||
          key == Unit.Structure.Protoss.Protoss_Cybernetics_Core ||
          key == Unit.Structure.Protoss.Protoss_Templar_Archives ||
          key == Unit.Structure.Protoss.Protoss_Forge ||
          key == Unit.Structure.Protoss.Protoss_Stargate ||
          key == Unit.Structure.Protoss.Protoss_Fleet_Beacon ||
          key == Unit.Structure.Protoss.Protoss_Arbiter_Tribunal ||
          key == Unit.Structure.Protoss.Protoss_Robotics_Support_Bay ||
          key == Unit.Structure.Protoss.Protoss_Shield_Battery )
        
        structures.put(Integer.parseInt(key.toString()), myPlayersUnits.get(Integer.parseInt(key.toString())));
      }

    return structures;
  }

  /* get only non-structures from the unit list */
  public HashMap<Integer,ArrayList<UnitObject>> getMyPlayersNonStructureUnits(){

    /**
     *  Integer: enum of Unit | ArrayList<UnitObject> of # of Units of that type
     */
    HashMap<Integer,ArrayList<UnitObject>> nonstructures = new HashMap<Integer, ArrayList<UnitObject>>();

    //get only the keys in the hashmap
    for(Iterator itr = myPlayersUnits.keySet().iterator(); itr.hasNext(); ){
      Enum key = (Enum)itr.next();

      if( key == Unit.NonStructure.Terran.Terran_Marine || 
          key == Unit.NonStructure.Terran.Terran_Ghost || 
          key == Unit.NonStructure.Terran.Terran_Vulture || 
          key == Unit.NonStructure.Terran.Terran_Goliath || 
          key == Unit.NonStructure.Terran.Terran_Siege_Tank_Tank_Mode || 
          key == Unit.NonStructure.Terran.Terran_SCV || 
          key == Unit.NonStructure.Terran.Terran_Wraith || 
          key == Unit.NonStructure.Terran.Terran_Science_Vessel || 
          key == Unit.NonStructure.Terran.Terran_Dropship || 
          key == Unit.NonStructure.Terran.Terran_Battlecruiser || 
          key == Unit.NonStructure.Terran.Terran_Vulture_Spider_Mine || 
          key == Unit.NonStructure.Terran.Terran_Nuclear_Missile || 
          key == Unit.NonStructure.Terran.Terran_Siege_Tank_Siege_Mode || 
          key == Unit.NonStructure.Terran.Terran_Firebat || 
          key == Unit.NonStructure.Terran.Spell_Scanner_Sweep || 
          key == Unit.NonStructure.Terran.Terran_Medic || 
          key == Unit.NonStructure.Terran.Terran_Valkyrie || 
        
          key == Unit.NonStructure.Zerg.Zerg_Larva || 
          key == Unit.NonStructure.Zerg.Zerg_Egg || 
          key == Unit.NonStructure.Zerg.Zerg_Zergling || 
          key == Unit.NonStructure.Zerg.Zerg_Hydralisk || 
          key == Unit.NonStructure.Zerg.Zerg_Ultralisk || 
          key == Unit.NonStructure.Zerg.Zerg_Broodling || 
          key == Unit.NonStructure.Zerg.Zerg_Drone || 
          key == Unit.NonStructure.Zerg.Zerg_Overlord || 
          key == Unit.NonStructure.Zerg.Zerg_Mutalisk || 
          key == Unit.NonStructure.Zerg.Zerg_Guardian || 
          key == Unit.NonStructure.Zerg.Zerg_Queen || 
          key == Unit.NonStructure.Zerg.Zerg_Defiler || 
          key == Unit.NonStructure.Zerg.Zerg_Scourge || 
          key == Unit.NonStructure.Zerg.Zerg_Infested_Terran || 
          key == Unit.NonStructure.Zerg.Zerg_Cocoon || 
          key == Unit.NonStructure.Zerg.Zerg_Devourer || 
         
          key == Unit.NonStructure.Protoss.Protoss_Corsair || 
          key == Unit.NonStructure.Protoss.Protoss_Dark_Templar || 
          key == Unit.NonStructure.Protoss.Protoss_Dark_Archon || 
          key == Unit.NonStructure.Protoss.Protoss_Probe || 
          key == Unit.NonStructure.Protoss.Protoss_Zealot || 
          key == Unit.NonStructure.Protoss.Protoss_Dragoon || 
          key == Unit.NonStructure.Protoss.Protoss_High_Templar || 
          key == Unit.NonStructure.Protoss.Protoss_Archon || 
          key == Unit.NonStructure.Protoss.Protoss_Shuttle || 
          key == Unit.NonStructure.Protoss.Protoss_Scout || 
          key == Unit.NonStructure.Protoss.Protoss_Arbiter || 
          key == Unit.NonStructure.Protoss.Protoss_Carrier || 
          key == Unit.NonStructure.Protoss.Protoss_Interceptor || 
          key == Unit.NonStructure.Protoss.Protoss_Reaver || 
          key == Unit.NonStructure.Protoss.Protoss_Observer || 
          key == Unit.NonStructure.Protoss.Protoss_Scarab || 
       
          key == Unit.NonStructure.Neutral.Critter_Rhynadon || 
          key == Unit.NonStructure.Neutral.Critter_Bengalaas || 
          key == Unit.NonStructure.Neutral.Critter_Scantid || 
          key == Unit.NonStructure.Neutral.Critter_Kakaru || 
          key == Unit.NonStructure.Neutral.Critter_Ragnasaur || 
          key == Unit.NonStructure.Neutral.Critter_Ursadon || 
          key == Unit.NonStructure.Neutral.Special_Crashed_Norad_II || 
          key == Unit.NonStructure.Neutral.Special_Ion_Cannon || 
          key == Unit.NonStructure.Neutral.Special_Overmind_With_Shell || 
          key == Unit.NonStructure.Neutral.Special_Overmind || 
          key == Unit.NonStructure.Neutral.Special_Mature_Chrysalis || 
          key == Unit.NonStructure.Neutral.Special_Cerebrate || 
          key == Unit.NonStructure.Neutral.Special_Cerebrate_Daggoth || 
          key == Unit.NonStructure.Neutral.Special_Stasis_Cell_Prison || 
          key == Unit.NonStructure.Neutral.Special_Khaydarin_Crystal_Form || 
          key == Unit.NonStructure.Neutral.Special_Protoss_Temple || 
          key == Unit.NonStructure.Neutral.Special_XelNaga_Temple || 
          key == Unit.NonStructure.Neutral.Resource_Mineral_Field || 
          key == Unit.NonStructure.Neutral.Resource_Vespene_Geyser || 
          key == Unit.NonStructure.Neutral.Special_Warp_Gate || 
          key == Unit.NonStructure.Neutral.Special_Psi_Disrupter || 
          key == Unit.NonStructure.Neutral.Special_Power_Generator || 
          key == Unit.NonStructure.Neutral.Special_Overmind_Cocoon || 
          key == Unit.NonStructure.Neutral.Spell_Dark_Swarm || 
          key == Unit.NonStructure.Neutral.None || 
          key == Unit.NonStructure.Neutral.Unknown )
        nonstructures.put(Integer.parseInt(key.toString()), myPlayersUnits.get(Integer.parseInt(key.toString())));
     }
    
    return nonstructures;
  }

  
  //
  // Enemy units
  //
  /* neutral units */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.NonStructure.Neutral u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* terran units */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.NonStructure.Terran u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* terran structures */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.Structure.Terran u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* zerg units */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.NonStructure.Zerg u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* zerg structures */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.Structure.Zerg u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* protoss units */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.NonStructure.Protoss u){
    return enemyPlayersUnits.get(u.getNumValue());
  }

  /* protoss structures */
  public ArrayList<UnitObject> getEnemyPlayersUnit(Unit.Structure.Protoss u){
    return enemyPlayersUnits.get(u.getNumValue());
  }


  //
  // Neutral units
  //
  /* neutral units */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.NonStructure.Neutral u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* terran units */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.NonStructure.Terran u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* terran structures */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.Structure.Terran u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* zerg units */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.NonStructure.Zerg u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* zerg structures */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.Structure.Zerg u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* protoss units */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.NonStructure.Protoss u){
    return neutralPlayersUnits.get(u.getNumValue());
  }

  /* protoss structures */
  public ArrayList<UnitObject> getNeutralPlayersUnit(Unit.Structure.Protoss u){
    return neutralPlayersUnits.get(u.getNumValue());
  }
}
