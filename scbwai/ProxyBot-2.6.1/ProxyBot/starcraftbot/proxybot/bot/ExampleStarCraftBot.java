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
			for (UnitWME unit : game.getPlayerUnits()) {
				if (unit.getIsWorker() && !(unit.getOrder() == Order.MiningMinerals.ordinal()) || unit.getOrder() == Order.PlayerGuard.ordinal()) {

					//System.out.println("Assigning worker of type:"+ unit.getType().getName()+": to mine...");
					
					int patchID = -1;
					double closest = Double.MAX_VALUE;

					for (UnitWME minerals : game.getMinerals()) {
						double dx = unit.getX() - minerals.getX();
						double dy = unit.getY() - minerals.getY();
						double dist = Math.sqrt(dx * dx + dy * dy);

						if (dist < closest) {
							patchID = minerals.getID();
							closest = dist;
						}
					}

					if (patchID != -1) {
						game.getCommandQueue()
								.rightClick(unit.getID(), patchID);
					}
				}
				}
				else
				{ 
					/*if(unit.getIsWorker())
					  System.out.println("Worker Unit NOT assigned to mine:  its current order is: getOrder() == "+ unit.getOrder());
					else
					  System.out.println("Non-worker Unit. unit type() is:"+unit.getType().getName()+"-- No need to mine");*/
				}
			}

			/*try {
				//System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			// build more workers
			if (game.getPlayer().getMinerals() >= 50) {
				int workerType = UnitTypeWME
						.getWorkerType(game.getPlayerRace());

				// morph a larva into a worker
				if (game.getPlayerRace() == Race.Zerg.ordinal()) {
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == UnitType.Zerg_Larva.ordinal()) {
							game.getCommandQueue().morph(unit.getID(),
									workerType);
						}
					}
				}
				// train a worker
				else {
					int centerType = UnitTypeWME.getCenterType(game
							.getPlayerRace());

					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == centerType) {
							game.getCommandQueue().train(unit.getID(),
									workerType);
						}
					}
				}
			}

			// build more supply
			
			int NonBuildingUnits = 0;
			int PrevSupplyUnits = 0;
			
			for(UnitWME unit : game.getPlayerUnits())
			{
				if(!unit.getIsBuilding())
					NonBuildingUnits++;
				else if(unit.getIsFarm())
					PrevSupplyUnits++;
			}
			
			
			if (game.getPlayer().getMinerals() >= 100 && NonBuildingUnits > (9 + (PrevSupplyUnits*10) - 2)
					/*game.getPlayer().getSupplyUsed() >= (game.getPlayer().getSupplyTotal() - 2)*/ ) {


				System.out.println("Hit the need to build another farm...");
				
				// morph a larva into a supply
				if (game.getPlayerRace() == Race.Zerg.ordinal()) {
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == UnitType.Zerg_Larva.ordinal()) {
							game.getCommandQueue().morph(unit.getID(),
									supplyType);
						}
					}
				}
				// build a farm
				else {
					int workerType = UnitTypeWME.getWorkerType(game
							.getPlayerRace());
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == workerType) {

							System.out.println("Assigning a unit to build supplyType:" + UnitTypeWME.getSupplyType(game.getPlayerRace())+"| should be...["+ UnitTypeWME.Protoss_Pylon+"]");
							
							
							
							// pick a random spot near the worker
							game.getCommandQueue()
									.build(unit.getID(),
											unit.getX()
													+ (int) (-10.0 + Math
															.random() * 20.0),
											unit.getY()
													+ (int) (-10.0 + Math
															.random() * 20.0),
											supplyType);
							break;
						}
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
