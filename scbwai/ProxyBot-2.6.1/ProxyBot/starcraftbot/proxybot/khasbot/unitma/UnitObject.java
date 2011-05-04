
package starcraftbot.proxybot.khasbot.unitma;

import java.io.Serializable;
import java.util.ArrayList;

import starcraftbot.proxybot.CommandId.Order;
import starcraftbot.proxybot.khasbot.mapma.MapLocation;


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
@SuppressWarnings("serial")
public class UnitObject implements Serializable, Comparable {
	
	/**
	 *  THESE ARE ATTRIBUTES TAKEN FROM UNITWME.. MAY WANT TO CHANGE!!!
	 */
	private int updateDataID;
	//private int playerID; //don't need this, thi
	private Unit type;
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

  private MapLocation destLoc = null;
  
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
	 * @param iD
	 */
	public void setType(int id) {
		type = Unit.getUnit(id);
	}

	/**
	 *
	 * @return ID of unit
	 */
	public Unit getType() {
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

    return ( (this.getOrder() == Order.Neutral.ordinal()) ||
    		 (this.getOrder() == Order.None.ordinal()) ||
    		 (this.getOrder() == Order.Nothing1.ordinal()) ||
    		 (this.getOrder() == Order.Nothing2.ordinal()) ||
    		 (this.getOrder() == Order.Nothing3.ordinal()) ||
    		 (this.getOrder() == Order.Unknown.ordinal())
    	   );
  }
  
  public boolean isGaurding(){
	  return (
			  (this.getOrder() == Order.PlayerGuard.ordinal()) ||
			  (this.getOrder() == Order.Guard.ordinal()) ||
			  (this.getOrder() == Order.GuardPost.ordinal())
			  );
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


  public boolean isMining() {
    return ((this.getOrder() == Order.MiningMinerals.ordinal()) ||
    (this.getOrder() == Order.MoveToMinerals.ordinal()) ||
    (this.getOrder() == Order.ReturnMinerals.ordinal()) ||
    (this.getOrder() == Order.WaitForMinerals.ordinal()));
  }

  public String toString(){
    return "Unit[ "+ this.getID() + "] at (" + this.getRealX() + "," + this.getRealY() +") is doing: " + Order.values()[this.getOrder()];
  }

  public void setLoc(MapLocation loc){
    destLoc = loc;
  }

  public double distance(){
    double dx = getRealX() - destLoc.getX();
    double dy = getRealY() - destLoc.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }

  public boolean equalTo(UnitObject u){
    if( u.getID() == updateDataID )//&&
//        u.getType() == type &&
//        u.getRealX() == realX &&
//        u.getRealY() == realY &&
//        u.getHitPoints() == hitPoints &&
//        u.getShields() == shields &&
//        u.getEnergy() == energy &&
//        u.getBuildTimer() == buildTimer &&
//        u.getTrainTimer() == trainTimer &&
//        u.getResearchTimer() == researchTimer &&
//        u.getUpgradeTimer() == upgradeTimer &&
//        u.getOrderTimer() == orderTimer &&
//        u.getOrder() == order &&
//        u.getResources() == resources &&
//        u.getAddonID() == addonID &&
//        u.getMineCount() == mineCount &&
//        u.getX() == x &&
//        u.getY() == y )
      return true;
    else
      return false;
  }

  @Override
  public int compareTo(Object o){
    if( this.distance() == ((UnitObject)o).distance() )
      return 0;
    else if(this.distance() > ((UnitObject)o).distance())
      return 1;
    else
      return -1;
  }
  /* not the best place for this I know */
  public static void heapSort(ArrayList<UnitObject> array) {
      /* This method performs an in-place heapsort. Starting
       * from the beginning of the array, the array is swapped
       * into a binary max heap.  Then elements are removed
       * from the heap, and added to the front of the sorted
       * section of the array. */

      /* Insertion onto heap */
      for (int heapsize=0; heapsize<array.size(); heapsize++) {
          /* Step one in adding an element to the heap in the
           * place that element at the end of the heap array-
           * in this case, the element is already there. */
          int n = heapsize; // the index of the inserted int
          while (n > 0) { // until we reach the root of the heap
              int p = (n-1)/2; // the index of the parent of n
              if( array.get(n).compareTo(array.get(p)) == 1) { // child is larger than parent
                  arraySwap(array, n, p); // swap child with parent
                  n = p; // check parent
              }
              else // parent is larger than child
                  break; // all is good in the heap
          }
      }

      /* Removal from heap */
      for (int heapsize=array.size(); heapsize>0;) {
          arraySwap(array, 0, --heapsize); // swap root with the last heap element
          int n = 0; // index of the element being moved down the tree
          while (true) {
              int left = (n*2)+1;
              if (left >= heapsize) // node has no left child
                  break; // reached the bottom; heap is heapified
              int right = left+1;
              if (right >= heapsize) { // node has a left child, but no right child
                  if (array.get(left).compareTo(array.get(n)) == 1) // if left child is greater than node
                      arraySwap(array, left, n); // swap left child with node
                  break; // heap is heapified
              }
              if (array.get(left).compareTo(array.get(n)) == 1) { // (left > n)
                  if (array.get(left).compareTo(array.get(right)) == 1) { // (left > right) & (left > n)
                      arraySwap(array, left, n);
                      n = left; continue; // continue recursion on left child
                  } else { // (right > left > n)
                      arraySwap(array, right, n);
                      n = right; continue; // continue recursion on right child
                  }
              } else { // (n > left)
                  if (array.get(right).compareTo(array.get(n)) == 1) { // (right > n > left)
                      arraySwap(array, right, n);
                      n = right; continue; // continue recursion on right child
                  } else { // (n > left) & (n > right)
                      break; // node is greater than both children, so it's heapified
                  }
              }
          }
      }
  }
  public static void arraySwap(ArrayList<UnitObject> array, int a, int b) {
    UnitObject temp = array.get(a);
    array.set(a,array.get(b));
    array.set(b,temp);
  }

  
}
