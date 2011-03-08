package starcraftbot.proxybot.bot;

import java.io.IOException;

import javax.swing.JPanel;

import starcraftbot.proxybot.Game;
import starcraftbot.proxybot.Constants.Order;
import starcraftbot.proxybot.Constants.Race;
import starcraftbot.proxybot.wmes.UnitTypeWME;
import starcraftbot.proxybot.wmes.UnitTypeWME.UnitType;
import starcraftbot.proxybot.wmes.unit.UnitWME;

/**
 * Example implementation of the StarCraftBot.
 * 
 * This build will tell workers to mine, build additional workers, and build
 * additional supply units.
 */
public class ExampleStarCraftBot implements StarCraftBot {

	/** specifies that the agent is running */
	boolean running = true;

	public JPanel getPanel() {
		return null;
	}

	/**
	 * Starts the bot.
	 * 
	 * The bot is now the owner of the current thread.
	 */
	public void start(Game game) {

		// run until told to exit
		while (running) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			// start mining
			System.out.println("NEW TURN:::::::::");
			
			UnitWME Nexus = null;
			for(UnitWME unit : game.getPlayerUnits()){
				if(unit.getIsCenter())
				{
					Nexus = new UnitWME(unit);
					break;
				}
			}
			
			for (UnitWME unit : game.getPlayerUnits()) {
				if (unit.getIsWorker())
				{
					
					//System.out.print("At unit worker, current order is:");
					
					//String OrderName = Order.;

					
					
					//System.out.println(unit.getOrder()+" which is :"+Order.values()[unit.getOrder()].toString());
					if(!((unit.getOrder() == Order.MiningMinerals.ordinal())
						|| (unit.getOrder() == Order.ReturnMinerals.ordinal())
						|| (unit.getOrder() == Order.MoveToMinerals.ordinal())
						|| (unit.getOrder() == Order.WaitForMinerals.ordinal())
						|| (unit.getOrder() == Order.Harvest1.ordinal())
						|| (unit.getOrder() == Order.Harvest2.ordinal())
						|| (unit.getOrder() == Order.Harvest3.ordinal())
						|| (unit.getOrder() == Order.Harvest4.ordinal())
						|| (unit.getOrder() == Order.Harvest5.ordinal())
						|| (unit.getOrder() == Order.Move.ordinal()))
						/*|| !(unit.getOrder() == Order.Build5.ordinal())
						|| !(unit.getOrder() == Order.BuildingLand.ordinal())
						|| unit.getOrder() == Order.PlayerGuard.ordinal()*/) {

					// System.out.println("Assigning worker of type:"+
					// unit.getType().getName()+": to mine...");
					System.out.println("Worker unit["+unit.getID()+"] was doing:"+Order.values()[unit.getOrder()].toString()+" -- setting it to Mine now.");
					int patchID = -1;
					double closest = Double.MAX_VALUE;

					//System.out.println("Mineral: " + game.getMinerals().size());
					//System.out.println("Geyser: " + game.getGeysers().size());
					
					for (UnitWME minerals : game.getMinerals()) {
						double dx = Nexus.getX() - minerals.getX();
						double dy = Nexus.getY() - minerals.getY();
						double dist = Math.sqrt(dx * dx + dy * dy);

						if (dist < closest) {
							patchID = minerals.getID();
							closest = dist;
						}
					}

					if (patchID != -1) {
						game.getCommandQueue()
								.rightClick(unit.getID(), patchID);
					} else {System.out.println("*pop*");
						/*
						 * if(unit.getIsWorker()) System.out.println(
						 * "Worker Unit NOT assigned to mine:  its current order is: getOrder() == "
						 * + unit.getOrder()); else
						 * System.out.println("Non-worker Unit. unit type() is:"
						 * +unit.getType().getName()+"-- No need to mine");
						 */
					}
				}
					else{
						//System.out.println("Worker unit["+unit.getID()+"] was doing:"+Order.values()[unit.getOrder()].toString()+" -- already Mining or something with mining...?");
					}
				}
				/*
				 * try { //System.in.read(); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */
			}
			// build more workers
			//UnitWME center = new UnitWME();
			if (game.getPlayer().getMinerals() >= 50) {
				int workerType = UnitTypeWME.getWorkerType(game.getPlayerRace());
					//int centerType = UnitTypeWME.getCenterType(game.getPlayerRace());

				game.getCommandQueue().train(Nexus.getID(),workerType);
							//center = new UnitWME(unit);
			}

			// build more supply

			int NonBuildingUnits = 0;
			int PrevSupplyUnits = 0;
			int supplyType = UnitTypeWME.getSupplyType(game.getPlayerRace());

			for (UnitWME unit : game.getPlayerUnits()) {
				if (!unit.getIsBuilding())
					NonBuildingUnits++;
				else if (unit.getIsFarm())
					PrevSupplyUnits++;
			}

			System.out.println("CURRENT # of Supply Units:"+NonBuildingUnits+": SUPPLY CAP IS: "+ 9 +(PrevSupplyUnits*8));
			
			if (game.getPlayer().getMinerals() >= 100
					&& NonBuildingUnits > (9 + (PrevSupplyUnits * 8) - 2)
			/*
			 * game.getPlayer().getSupplyUsed() >=
			 * (game.getPlayer().getSupplyTotal() - 2)
			 */) {
				//int supplyType = UnitTypeWME.getSupplyType(game.getPlayerRace());

				//System.out.println("Hit the need to build another farm...");

				// build a farm
					int workerType = UnitTypeWME.getWorkerType(game
							.getPlayerRace());
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == workerType) {
							
							

							/*System.out.println("Assigning a unit to build supplyType:"
											+ UnitTypeWME.getSupplyType(game
													.getPlayerRace())
											+ "| should be...["
											+ UnitTypeWME.Protoss_Pylon + "]");
*/
							// pick a random spot near the worker
							//int centerX=0,centerY=0;
							int buildX=0,buildY=0;
							/*for(UnitWME u : game.getUnits())
							{
								if(u.getIsCenter())
								{
									centerX = u.getX();
									centerY = u.getY();
									break;
								}
							}*/
							
							buildX = Nexus.getRealX() + (int) (Math.random() * 5.0);
							buildY = Nexus.getRealY() + (int) (Math.random() * 5.0);
							if(buildX == Nexus.getRealX() && buildY == Nexus.getRealY())
							{
								if((int) (Math.random() * 1.0) == 1)
									buildX++;
								else
									buildY++;
							}
							if(buildX > game.getMap().getMapWidth())
							{
								buildX = Nexus.getRealX() - (buildX-Nexus.getRealX());
							}
							if(buildY > game.getMap().getMapHeight())
							{
								buildY = Nexus.getRealY() - (buildY - Nexus.getRealY());
							}
							System.out.print("checking spot ["+buildX+","+buildY+"] to see if buildable for Pylon by center["+Nexus.getRealX()+","+Nexus.getRealY()+"]...");
							System.out.println("Range of map is {0.."+game.getMap().getMapWidth()+"} wide and {0.."+game.getMap().getMapHeight()+"} height");
							//UnitWME pylon = new UnitWME();
							
							boolean flip = true;
							while(!(game.getMap().isBuildable(buildX, buildY, 2, 2))) //pylon is 2x2 tiles
							{
								if(flip)
								{
									buildX++;
									flip = false;
								}
								else
								{
									buildY++;
									flip = true;
								}
								if(buildX > game.getMap().getMapWidth())
								{
									buildX = Nexus.getRealX() - (buildX-Nexus.getRealX());
								}
								if(buildY > game.getMap().getMapHeight())
								{
									buildY = Nexus.getRealY() - (buildY - Nexus.getRealY());
								}
							}
								System.out.println("BUILDABLE, ORDERING BUILD: Unit["+unit.getID()+"] will build pylon @("+buildX+","+buildY+"), the unit was previously: "+ Order.values()[unit.getOrder()].toString());
								
								
								//game.getCommandQueue().stop(unit.getID());
								
								game.getCommandQueue().build(unit.getID(),
															 buildX, //unit.getX() + (int) (-10.0 + Math.random() * 20.0),
															 buildY, //unit.getY() + (int) (-10.0 + Math.random() * 20.0),
															 supplyType);
								game.getCommandQueue().rightClick(unit.getID(), buildX, buildY);
								//game.getCommandQueue().stop(unit.getID());
								//game.getCommandQueue().drawCircleScreen(buildX, buildY, 1, true);
								//game.getCommandQueue().drawCircleMap(buildX, buildY, 1, true);
								game.getCommandQueue().drawRectScreen(buildX, buildY, buildX+1, buildY-1);
								game.getCommandQueue().drawRectMap(buildX, buildY, buildX+1, buildY-1);
								//game.getCommandQueue().drawBoxScreen(buildX, buildY, buildX+1, buildY+1);
								
								
								//unit.setOrder(Order.Build5.ordinal());
							//}
							/*else{
								System.out.println("CANT BUILD AT RANDOM SPOT!");
							}*/
							break;
						}
					}
				}

		}
	}

	/**
	 * Tell the main thread to quit.
	 */
	public void stop() {
		running = false;
	}
}
