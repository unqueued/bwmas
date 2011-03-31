
package starcraftbot.proxybot.khasbot.unitma;

import java.io.Serializable;
import java.util.ArrayList;

import starcraftbot.proxybot.game.GameObject;

/**
 * 
 * This is the object for a "unit" in the Starcraft game.
 * 
 * 		this includes anything from buildings, drones, and minerals.
 * 
 * 
 * @author Antonio Arredondo
 *
 */
public class UnitsObject implements Serializable{



    /** in game object */
	public static int Critter_Rhynadon = 89;
	   /** in game object */
	public static int Critter_Bengalaas = 90;
	   /** in game object */
	public static int Critter_Scantid = 93;
	   /** in game object */
	public static int Critter_Kakaru = 94;
	   /** in game object */
	public static int Critter_Ragnasaur = 95;
	   /** in game object */
	public static int Critter_Ursadon = 96;
	   /** in game object */
	public static int Special_Crashed_Norad_II = 126;
	   /** in game object */
	public static int Special_Ion_Cannon = 127;
	   /** in game object */
	public static int Special_Overmind_With_Shell = 147;
	   /** in game object */
	public static int Special_Overmind = 148;
	   /** in game object */
    public static int Special_Mature_Chrysalis = 150;
    /** in game object */
    public static int Special_Cerebrate = 151;
    /** in game object */
    public static int Special_Cerebrate_Daggoth = 152;
    /** in game object */
	public static int Special_Stasis_Cell_Prison = 168;
	   /** in game object */
	public static int Special_Khaydarin_Crystal_Form = 173;
	   /** in game object */
	public static int Special_Protoss_Temple = 174;
	   /** in game object */
	public static int Special_XelNaga_Temple = 175;
	   /** in game object */
    public static int Resource_Mineral_Field = 176;
    /** in game object */
    public static int Resource_Vespene_Geyser = 188;
    /** in game object */
    public static int Special_Warp_Gate = 189;
    /** in game object */
    public static int Special_Psi_Disrupter = 190;
    /** in game object */
    public static int Special_Power_Generator = 200;
    /** in game object */
    public static int Special_Overmind_Cocoon = 201;
    /** in game object */
    public static int Spell_Dark_Swarm = 202;
    /** in game object */
    public static int None = 228;
    /** in game object */
    public static int Unknown = 229;
    
	
	/**
	 *  THESE ARE ATTRIBUTES TAKEN FROM UNITWME.. MAY WANT TO CHANGE!!!
	 */
	private int ID;
	private int playerID;
	private UnitID type;
	private int realX; 			
	private int realY;			
	private int hitPoints; 		
	private int shields; 		
	private int energy; 			
	private int buildTimer; 		
	private int trainTimer;		
	private int researchTimer; 	
	private int upgradeTimer;
	private int orderTimer; 	
	private int order; 		
	//unit.lifted = (Integer.parseInt(attributes[14]) != 0);
	private int resources; 		
	private int addonID; 	
	private int mineCount;		
	private int x; 			
	private int y;      		
	
	
	
	
	/**
	 * creates a new ArrayList of the units it parses from 'update' in game.
	 * 
	 * @param update
	 * @param game
	 * @return newList
	 */
	public static ArrayList<UnitsObject> parseUpdateUnits(String update, GameObject game) 
	{
		ArrayList<UnitsObject> newList = new ArrayList<UnitsObject>();
		
		String[] unitDatas = update.split(":");
		boolean first = true;

		for (String data : unitDatas) {
			if (first) {
				first = false;
				continue;
			}

			String[] attributes = data.split(";");

			//System.out.println("UnitWME.getUnits:: attribute.length==["+attributes.length+"]");
			
			int ID = Integer.parseInt(attributes[0]);
			UnitsObject unit;
			if(ID < game.getUnitArray().size())
				unit = game.getUnitAt(ID);
			else
				unit = null;
			
			int pID = Integer.parseInt(attributes[1]);
			int utype = Integer.parseInt(attributes[2]);

			// allow units to change type
			if (unit != null && unit.getType().getID() != utype) {
				unit = null;
			}

			if (unit == null) {
				/*if (pID == game.getMyPlayer().getPlayerID()) {
					unit = new PlayerUnitWME();
				} else if (utype == UnitType.Resource_Mineral_Field.ordinal()) {
					unit = new MineralWME();
				} else if (utype == UnitType.Resource_Vespene_Geyser.ordinal()) {
					unit = new GeyserWME();
				} else if (pID != getPlayerID() && pID != 11
						&& !players[pID].isAlly()) {
					unit = new EnemyUnitWME();
				} else if (pID != getPlayerID() && pID != 11
						&& players[pID].isAlly()) {
					unit = new AllyUnitWME();
				} else {*/
					unit = new UnitsObject();
				//}
			}

			unit.setID(ID);
			unit.setPlayerID(pID);
			unit.setType(new UnitID(utype));
			unit.realX = Integer.parseInt(attributes[3]);
			unit.realY = Integer.parseInt(attributes[4]);
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
			newList.add(unit);

			unit.setX(unit.realX / 32);
			unit.setY(unit.realY / 32);
		}
		
		
		return newList;
	}

	/**
	 * 
	 * @param iD
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * 
	 * @return ID of unit
	 */
	public int getID() {
		return ID;
	}
	/**
	 * 
	 * @param playerID
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	/**
	 * 
	 * @return playerID of unit
	 */
	public int getPlayerID() {
		return playerID;
	}
	/**
	 * 
	 * @param type
	 */
	public void setType(UnitID type) {
		this.type = type;
	}
	/**
	 * 
	 * @return type of unit
	 */
	public UnitID getType() {
		return type;
	}
	/**
	 * 
	 * @param hitPoints
	 */
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	/**
	 * 
	 * @return hitPoints of unit
	 */
	public int getHitPoints() {
		return hitPoints;
	}
	/**
	 * 
	 * @param shields
	 */
	public void setShields(int shields) {
		this.shields = shields;
	}
	/**
	 * 
	 * @return shields of unit
	 */
	public int getShields() {
		return shields;
	}
	/**
	 * 
	 * @param energy
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	/**
	 * 
	 * @return energy of unit
	 */
	public int getEnergy() {
		return energy;
	}
	/**
	 * 
	 * @param buildTimer
	 */
	public void setBuildTimer(int buildTimer) {
		this.buildTimer = buildTimer;
	}
	/**
	 * 
	 * @return buildTimer for unit
	 */
	public int getBuildTimer() {
		return buildTimer;
	}
	/**
	 * 
	 * @param trainTimer
	 */
	public void setTrainTimer(int trainTimer) {
		this.trainTimer = trainTimer;
	}
	/**
	 * 
	 * @return trainTimer for unit
	 */
	public int getTrainTimer() {
		return trainTimer;
	}
	/**
	 * 
	 * @param researchTimer
	 */
	public void setResearchTimer(int researchTimer) {
		this.researchTimer = researchTimer;
	}
	/**
	 * 
	 * @return researchTimer for unit
	 */
	public int getResearchTimer() {
		return researchTimer;
	}
	/**
	 * 
	 * @param upgradeTimer
	 */
	public void setUpgradeTimer(int upgradeTimer) {
		this.upgradeTimer = upgradeTimer;
	}
	/**
	 * 
	 * @return upgradeTimer for unit
	 */
	public int getUpgradeTimer() {
		return upgradeTimer;
	}
	/**
	 * 
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * 
	 * @return order for unit
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * 
	 * @param orderTimer
	 */
	public void setOrderTimer(int orderTimer) {
		this.orderTimer = orderTimer;
	}
	/**
	 * 
	 * @return orderTimer for unit
	 */
	public int getOrderTimer() {
		return orderTimer;
	}
	/**
	 * 
	 * @param resources
	 */
	public void setResources(int resources) {
		this.resources = resources;
	}
	/**
	 * 
	 * @return resources for unit
	 */
	public int getResources() {
		return resources;
	}
	/**
	 * 
	 * @param mineCount
	 */
	public void setMineCount(int mineCount) {
		this.mineCount = mineCount;
	}
	/**
	 * 
	 * @return minecount for unit
	 */
	public int getMineCount() {
		return mineCount;
	}
	/**
	 * 
	 * @param addonID
	 */
	public void setAddonID(int addonID) {
		this.addonID = addonID;
	}
	/**
	 * 
	 * @return addonID for unit
	 */
	public int getAddonID() {
		return addonID;
	}
	/**
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * 
	 * @return x coordinate for unit
	 */
	public int getX() {
		return x;
	}
	/**
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * 
	 * @return y coordinate for unit
	 */
	public int getY() {
		return y;
	}

}
