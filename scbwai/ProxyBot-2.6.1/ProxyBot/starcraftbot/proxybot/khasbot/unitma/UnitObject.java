
package starcraftbot.proxybot.khasbot.unitma;

import java.io.Serializable;


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
public class UnitObject implements Serializable{
	
	/**
	 *  THESE ARE ATTRIBUTES TAKEN FROM UNITWME.. MAY WANT TO CHANGE!!!
	 */
	private int updateDataID;
	//private int playerID; //don't need this, thi
	//private Unit type;
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


	public UnitObject(){
    
  }

	
	/**
	 * 
	 * @param iD
	 */
	public void setID(int id) {
		updateDataID = id;
	}

	/**
	 * 
	 * @return ID of unit
	 */
	public int getID() {
		return updateDataID;
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


  /**
	 *
	 * @param x
	 */
	public void setRealX(int x) {
		this.realX = x;
	}
	/**
	 *
	 * @return x coordinate for unit
	 */
	public int getRealX() {
		return realX;
	}
	/**
	 *
	 * @param y
	 */
	public void setRealY(int y) {
		this.realY = y;
	}
	/**
	 *
	 * @return y coordinate for unit
	 */
	public int getRealY() {
		return realY;
	}

  public boolean isIdle(){

    return false;
  }
//  public boolean isAirUnit(){
//
//  }
//  public boolean isGroundUnit(){
//
//  }
  public boolean isBeingBuilt(){
    return (buildTimer > 0);
  }
  public boolean isBeingTrained(){
    return (trainTimer > 0);
  }
  public boolean isBuilt(){
    return buildTimer == 0;
  }
  public boolean isTrained(){
    return trainTimer == 0;
  }

}
