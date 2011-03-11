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
	
	boolean powered[][] = null;
	boolean gatewayBuild = false;
	boolean gatewayBuilding = false;
	boolean pylonBuilding = false;
	boolean probecount = false;
	boolean goUp = false;
	boolean goRight = false;
	boolean canGas = false;
	boolean buildingGas = false;
	boolean cybercoreBuild = false;
	boolean cybercoreBuilding = false;
	
	
	private int GasMaintain = 0;
	private Game g;

	public JPanel getPanel() {
		return null;
	}

	/**
	 * Starts the bot.
	 * 
	 * The bot is now the owner of the current thread.
	 */
	public void start(Game game) {

		g = game;
		
		// run until told to exit
		while (running) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			// start mining
			//System.out.println("NEW TURN:::::::::");
			
			powered = new boolean[game.getMap().getMapHeight()][game.getMap().getMapWidth()];
			
			game.getCommandQueue().setColor(1);
			
			for(int i = 0; i < game.getMap().getMapHeight();i++)
			{
				//powered[i] = new [];
				for(int j = 0; j < game.getMap().getMapWidth();j++)
				{
					powered[i][j] = false;
				}
			}
			
			
			
			UnitWME Nexus = null;
			for(UnitWME unit : game.getPlayerUnits()){
				if(unit.getIsCenter())
				{
					Nexus = new UnitWME(unit);
					if(Nexus.getRealX() < (game.getMap().getMapWidth() / 2))
						goRight = true;
					if(Nexus.getRealY() < (game.getMap().getMapHeight() / 2))
						goUp = true;	
				}
				else if(unit.getTypeID() == UnitTypeWME.Protoss_Assimilator)
				{
					if(unit.getIsBuilt())
					{
						canGas = true;
						buildingGas = false;
					}
				}
				else if(unit.getTypeID() == UnitTypeWME.Protoss_Cybernetics_Core)
				{
					if(unit.getIsBuilding())
					{
						cybercoreBuilding = true;
					}
					else if(unit.getIsBuilt())
					{
						cybercoreBuild = true;
						cybercoreBuilding = false;
					}
				}
				else if(unit.getTypeID() == UnitTypeWME.Protoss_Gateway)
				{
					if(unit.getIsBuilding())
					{
						gatewayBuilding = true;
					}
					if(unit.getIsBuilt())
					{
						gatewayBuild = true;
						gatewayBuilding = false;
					}
				}
				else if(unit.getIsFarm())
				{
					if(unit.getIsBuilding())
					{
						pylonBuilding = true;
					}
					else
					{
						powered[unit.getRealY()][unit.getRealX()] = true;
						//mark all areas w/ circle realX,realY radius... 10? as powered....
						
						int radius = 10;
						int X = unit.getRealX();
						int Y = unit.getRealY();
						//fit circular peg (psy matrix) into square hole (map)
						//
						//   (x-a)^2 + (y-b)^2 = r^2  circle @ (a,b) w/ r radius
						//
						
						int currentX = X - radius - 1;
						if(currentX < 0) currentX = 0;
						int currentY, maxY;
						while(currentX < X+radius && currentX < game.getMap().getMapWidth())
						{
							currentY = (int)Math.sqrt((double) Math.pow(radius, 2.0) - Math.pow(X-currentX, 2.0));
							maxY = Y + Math.abs(Y-currentY);
							if(currentY > maxY)
							{
								int t = maxY;
								maxY = currentY;
								currentY = t;
							}
							System.out.println("Filling out power added by pylon:"+unit.getID()+"--at curX["+currentX+"], lowY:"+currentY+" & maxY:"+maxY);
							while(currentY < maxY  && currentY < game.getMap().getMapHeight())
							{
								powered[currentY][currentX] = true;
								currentY++;
							}
							currentX++;
							
						}
					}
				}
			}
			
			for (UnitWME unit : game.getPlayerUnits()) {
				if (unit.getTypeID() == UnitTypeWME.Protoss_Zealot || unit.getTypeID() == UnitTypeWME.Protoss_Dragoon)
				{
					if(!(unit.getOrder() == Order.Patrol.ordinal()))
					{
						int x = (int) (Math.random() * game.getMap().getMapWidth());
						int y = (int) (Math.random() * game.getMap().getMapHeight());
						game.getCommandQueue().patrol(unit.getID(), x, y);
						System.out.println("Unit["+unit.getID()+"] ordered to patrol to ("+x+","+y+")");
					}
				}
				else if (unit.getIsWorker()) //mine if doing nothing better
				{
					int numAssim = 0;
					UnitWME assim = null;
					for(UnitWME u: game.getPlayerUnits())
					{
						if(u.getTypeID() == UnitTypeWME.Protoss_Assimilator)
						{
							assim = new UnitWME(u);
							numAssim++;
						}
					}
					if(canGas)
					{
					  	if(GasMaintain < 3*numAssim)
					  	{
					  		game.getCommandQueue().rightClick(unit.getID(), assim.getID());
					  		GasMaintain++;
					  	}
					}
					//System.out.println(unit.getOrder()+" which is :"+Order.values()[unit.getOrder()].toString());
					if(!((unit.getOrder() == Order.MiningMinerals.ordinal())
						|| (unit.getOrder() == Order.ReturnMinerals.ordinal())
						|| (unit.getOrder() == Order.MoveToMinerals.ordinal())
						|| (unit.getOrder() == Order.WaitForMinerals.ordinal())
						|| (unit.getOrder() == Order.Harvest1.ordinal())
						|| (unit.getOrder() == Order.Harvest2.ordinal())
						|| (unit.getOrder() == Order.Harvest3.ordinal())
						|| (unit.getOrder() == Order.Harvest4.ordinal())
						|| (unit.getOrder() == Order.HarvestGas.ordinal())
						|| (unit.getOrder() == Order.WaitForGas.ordinal())
						|| (unit.getOrder() == Order.ReturnGas.ordinal())
						|| (unit.getOrder() == Order.Harvest5.ordinal()))) 
					{

						// System.out.println("Assigning worker of type:"+
						// unit.getType().getName()+": to mine...");
						//System.out.println("Worker unit["+unit.getID()+"] was doing:"+Order.values()[unit.getOrder()].toString()+" -- setting it to Mine now.");
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
							game.getCommandQueue().rightClick(unit.getID(), patchID);
						} 
					}
				}
			}
			// build more workers
			//UnitWME center = new UnitWME();
			if (game.getPlayer().getMinerals() >= 50  && !probecount) {
				int workerType = UnitTypeWME.getWorkerType(game.getPlayerRace());
					//int centerType = UnitTypeWME.getCenterType(game.getPlayerRace());

				game.getCommandQueue().train(Nexus.getID(),workerType);
							//center = new UnitWME(unit);
			}
			//make zealots
			if(game.getPlayer().getMinerals() >= 100 && gatewayBuild)
			{
				for(UnitWME u : game.getPlayerUnits())
				{
					if(u.getTypeID() == UnitTypeWME.Protoss_Gateway)
					{
						game.getCommandQueue().train(u.getID(), UnitTypeWME.Protoss_Zealot);
						break;
					}
				}
			}
			if(cybercoreBuild && game.getPlayer().getMinerals() >= 125 && game.getPlayer().getGas() >= 50)
			{
				for(UnitWME u : game.getPlayerUnits())
				{
					if(u.getTypeID() == UnitTypeWME.Protoss_Gateway)
					{
						game.getCommandQueue().train(u.getID(), UnitTypeWME.Protoss_Dragoon);
						break;
					}
				}
			}
			//if we have a gateway, build a cybernetics core
			if(gatewayBuild && !cybercoreBuilding && !cybercoreBuild)
			{
				UnitWME pylon = null;
				for(UnitWME u : game.getPlayerUnits())
				{
					if(u.getIsFarm())
					{
						pylon = u;
						break;
					}
				}
				int RandomX,RandomY;
				if(pylon != null)
				{
					if(goRight)
					{
						RandomX = (int) (pylon.getRealX() + (Math.random()*9));
					}
					else
					{
						RandomX = (int) (pylon.getRealX() - (Math.random()*9));
					}
					if(goUp)
					{
						RandomY = (int) (pylon.getRealY() - (Math.random()*9));
					}
					else
					{
						RandomY = (int) (pylon.getRealY() + (Math.random()*9));
					}
					if(RandomX < 0)
						RandomX =0;
					else if(RandomX > game.getMap().getMapWidth() - 4)
						RandomX = game.getMap().getMapWidth() - 4;
					if(RandomY < 0)
						RandomY = 0;
					else if(RandomY > game.getMap().getMapHeight() - 3)
						RandomY = game.getMap().getMapHeight() - 3;
					boolean flip = false;
					while(!game.getMap().isBuildable(RandomX, RandomY, 3, 2) && hasPower(RandomX, RandomY, 3, 2))
					{
						if(flip)
						{
							if(goRight)
								RandomX++;
							else
								RandomX--;
							flip = false;
						}
						else
						{
							if(goUp)
								RandomY--;
							else
								RandomY++;
							flip = true;
						}
						if(RandomX > pylon.getRealX()+9 || RandomX > game.getMap().getMapWidth()-5) //bounds case, building is width of 4...
						{
							RandomX = pylon.getRealX()-10;
							if(RandomX < 0)
								RandomX = 0;
						}
						else if(RandomX < pylon.getRealX()-9 || RandomX < 0)
						{
							RandomX = pylon.getRealX()+10;
							if(RandomX > game.getMap().getMapWidth()-5)
								RandomX = game.getMap().getMapWidth()-5;
						}
						if(RandomY > pylon.getRealY()+9 || RandomY > game.getMap().getMapHeight()-4) //bounds case, building is height of 3...
						{
							RandomY = pylon.getRealY()-10;
							if(RandomY < 0)
								RandomY = 0;
						}
						else if(RandomY < pylon.getRealY()-9 || RandomY < 0)
						{
							RandomY = pylon.getRealY()+10;
							if(RandomY >game.getMap().getMapHeight()-4)
								RandomY = game.getMap().getMapHeight()-4;
						}
					}
					for(UnitWME u : game.getPlayerUnits())
					{
						if(u.getIsWorker())
						{
							System.out.println("BUILDING A CYBERNETICS CORE AT:: ("+RandomX+","+RandomY+") near pylon @("+pylon.getRealX()+","+pylon.getRealY()+")!!");
							game.getCommandQueue().build(u.getID(), RandomX, RandomY, UnitTypeWME.Protoss_Cybernetics_Core);
							break;
						}
					}
				}
			}
			//if we have a gateway, build an assimilator
			if(gatewayBuild && !buildingGas && !canGas)
			{
				double closest = Double.MAX_VALUE;
				int patchID = -1;
				UnitWME geyser = null;
				for(UnitWME gas : game.getGeysers())
				{
					double dx = Nexus.getX() - gas.getX();
					double dy = Nexus.getY() - gas.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);

					if (dist < closest) {
						//patchID = gas.getID();
						geyser = new UnitWME(gas);
						closest = dist;
					}
				}

				if(geyser != null)
				{
					for(UnitWME u : game.getPlayerUnits())
					{
						if (u.getIsWorker())
						{
							game.getCommandQueue().build(u.getID(), geyser.getRealX(), geyser.getRealY(), UnitTypeWME.Protoss_Assimilator);
							buildingGas = true;
						}
					}
				}
			}
			//if we have pylons, build a gateway
			if(game.getPlayer().getMinerals() >= 150 && !gatewayBuild && !gatewayBuilding)
			{
				UnitWME pylon = null;
				for(UnitWME u : game.getPlayerUnits())
				{
					if(u.getIsFarm())
					{
						pylon = u;
						break;
					}
				}
				int RandomX,RandomY;
				if(pylon != null)
				{
					if(goRight)
					{
						RandomX = (int) (pylon.getRealX() + (Math.random()*9));
					}
					else
					{
						RandomX = (int) (pylon.getRealX() - (Math.random()*9));
					}
					if(goUp)
					{
						RandomY = (int) (pylon.getRealY() - (Math.random()*9));
					}
					else
					{
						RandomY = (int) (pylon.getRealY() + (Math.random()*9));
					}
					if(RandomX < 0)
						RandomX = 0;
					else if(RandomX > game.getMap().getMapWidth() - 5)
						RandomX = game.getMap().getMapWidth() - 5;
					if(RandomY < 0)
						RandomY = 0;
					else if(RandomY > game.getMap().getMapHeight() - 4)
						RandomY = game.getMap().getMapHeight() - 4;
					boolean flip = false;
					while(!game.getMap().isBuildable(RandomX, RandomY, 4, 3) && hasPower(RandomX, RandomY, 4, 3))
					{
						if(flip)
						{
							if(goRight)
								RandomX++;
							else
								RandomX--;
							flip = false;
						}
						else
						{
							if(goUp)
								RandomY--;
							else
								RandomY++;
							flip = true;
						}
						if(RandomX > pylon.getRealX()+9 || RandomX > game.getMap().getMapWidth()-5) //bounds case, building is width of 4...
						{
							RandomX = pylon.getRealX()-10;
							if(RandomX < 0)
								RandomX = 0;
						}
						else if(RandomX < pylon.getRealX()-9 || RandomX < 0)
						{
							RandomX = pylon.getRealX()+10;
							if(RandomX > game.getMap().getMapWidth()-5)
								RandomX = game.getMap().getMapWidth()-5;
						}
						if(RandomY > pylon.getRealY()+9 || RandomY > game.getMap().getMapHeight()-4) //bounds case, building is height of 3...
						{
							RandomY = pylon.getRealY()-10;
							if(RandomY < 0)
								RandomY = 0;
						}
						else if(RandomY < pylon.getRealY()-9 || RandomY < 0)
						{
							RandomY = pylon.getRealY()+10;
							if(RandomY >game.getMap().getMapHeight()-4)
								RandomY = game.getMap().getMapHeight()-4;
						}
					}
					for(UnitWME u : game.getPlayerUnits())
					{
						if(u.getIsWorker())
						{
							System.out.println("BUILDING A GATEWAY AT:: ("+RandomX+","+RandomY+") near pylon @("+pylon.getRealX()+","+pylon.getRealY()+")!!");
							game.getCommandQueue().build(u.getID(), RandomX, RandomY, UnitTypeWME.Protoss_Gateway);
							break;
						}
					}
				}
				
			}
			
			// build more supply

			int NonBuildingUnits = 0;
			int PrevSupplyUnits = 0;
			int supplyType = UnitTypeWME.getSupplyType(game.getPlayerRace());

			int probes = 0;
			
			for (UnitWME unit : game.getPlayerUnits()) 
			{
				if (unit.getTypeID() == UnitTypeWME.Protoss_Probe)
				{
					NonBuildingUnits++;
					probes++;
				}
				else if(unit.getTypeID() == UnitTypeWME.Protoss_Zealot || unit.getTypeID() == UnitTypeWME.Protoss_Gateway)
					NonBuildingUnits++;
				else if (unit.getTypeID() == UnitTypeWME.Protoss_Pylon)
				{
					NonBuildingUnits++;
					PrevSupplyUnits++;
				}
			}
			
			if(probes > 14)
				probecount = true;
			else if(probecount)
				probecount = false;
			
			
			System.out.println("CURRENT # of Supply Units:"+NonBuildingUnits+": SUPPLY CAP IS: "+ (9 +(PrevSupplyUnits*8)));
			
			if (game.getPlayer().getMinerals() >= 100 && NonBuildingUnits >= (9 + (PrevSupplyUnits * 8) - 2))
			{
				// build a farm
					int workerType = UnitTypeWME.getWorkerType(game.getPlayerRace());
					for (UnitWME unit : game.getPlayerUnits()) {
						if (unit.getTypeID() == workerType) {
							
							//pick spot near Nexus
							int buildX=0,buildY=0;
							if(goRight)
								buildX = Nexus.getRealX() + (int) (Math.random() * 15.0);
							else
								buildX = Nexus.getRealX() - (int) (Math.random() * 15.0);
							if(goUp)
								buildY = Nexus.getRealY() - (int) (Math.random() * 15.0);
							else
								buildY = Nexus.getRealY() + (int) (Math.random() * 15.0);
							if(buildX > game.getMap().getMapWidth()-1)
							{
								buildX = Nexus.getRealX();
							}
							if(buildY > game.getMap().getMapHeight()-1)
							{
								buildY = Nexus.getRealY();
							}
							boolean flip = true;
							while(!(game.getMap().isBuildable(buildX, buildY, 2, 2))) //pylon is 2x2 tiles
							{
								if(flip)
								{
									if(goRight)
										buildX++;
									else
										buildX--;
									flip = false;
								}
								else
								{
									if(goUp)
										buildY--;
									else
										buildY++;
									flip = true;
								}
								if(buildX > game.getMap().getMapWidth() - 3)
								{
									buildX = Nexus.getRealX() - (buildX-Nexus.getRealX());
								}
								if(buildY > game.getMap().getMapHeight() - 3)
								{
									buildY = Nexus.getRealY() - (buildY - Nexus.getRealY());
								}
								if(buildX < 0)
									buildX = Nexus.getRealX() - buildX;
								if(buildY < 0)
									buildY = Nexus.getRealY() - buildY;
							}
								System.out.println("BUILDABLE, ORDERING BUILD: Unit["+unit.getID()+"] will build pylon @("+buildX+","+buildY+"), the unit was previously: "+ Order.values()[unit.getOrder()].toString());
								game.getCommandQueue().build(unit.getID(), buildX, buildY, supplyType);
							break;
						}
					}
				}

		}
	}

	private boolean hasPower(int randomX, int randomY, int i, int j) {
		// TODO Auto-generated method stub
		for(int a = 0; a < j; a++)
		{
			if(randomY+a > g.getMap().getMapHeight()-1)
				return false;
			for(int b = 0; b < i; b++)
			{
				if(randomX+b > g.getMap().getMapWidth()-1)
					return false;
				else if(!powered[randomY+a][randomX+b])
					return false;
			}
		}
		
		return true;
	}

	/**
	 * Tell the main thread to quit.
	 */
	public void stop() {
		running = false;
	}
}
