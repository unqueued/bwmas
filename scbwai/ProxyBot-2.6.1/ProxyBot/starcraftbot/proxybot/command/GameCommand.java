package starcraftbot.proxybot.command;

import java.io.Serializable;
import java.awt.Color;

import starcraftbot.proxybot.CommandId;

/**
 * Representation of a command (Order) in StarCraft. The list of commands is
 * enumerated here: http://code.google.com/p/bwapi/wiki/Orders
 * 
 * The actual function definitions are provided in Unit.h on the AIModule side:
 * virtual bool attackMove(Position position) = 0; 
 * virtual bool attackUnit(Unit* target) = 0; 
 * virtual bool rightClick(Position position) = 0; 
 * virtual bool rightClick(Unit* target) = 0;
 * virtual bool train(UnitType type) = 0; 
 * virtual bool build(TilePosition position, UnitType type) = 0; 
 * virtual bool buildAddon(UnitType type) = 0; 
 * virtual bool research(TechType tech) = 0; 
 * virtual bool upgrade(UpgradeType upgrade) = 0; 
 * virtual bool stop() = 0; virtual bool holdPosition() = 0; 
 * virtual bool patrol(Position position) = 0; 
 * virtual bool follow(Unit* target) = 0; 
 * virtual bool setRallyPosition(Position target) = 0; 
 * virtual bool setRallyUnit(Unit* target) = 0; 
 * virtual bool repair(Unit* target) = 0; 
 * virtual bool morph(UnitType type) = 0; 
 * virtual bool burrow() = 0; 
 * virtual bool unburrow() = 0; 
 * virtual bool siege() = 0; 
 * virtual bool unsiege() = 0; 
 * virtual bool cloak() = 0; 
 * virtual bool decloak() = 0; 
 * virtual bool lift() = 0; 
 * virtual bool land(TilePosition position) = 0; 
 * virtual bool load(Unit* target) = 0; 
 * virtual bool unload(Unit* target) = 0; 
 * virtual bool unloadAll() = 0; 
 * virtual bool unloadAll(Position position) = 0; 
 * virtual bool cancelConstruction() = 0; 
 * virtual bool haltConstruction() = 0; 
 * virtual bool cancelMorph() = 0; 
 * virtual bool cancelTrain() = 0; 
 * virtual bool cancelTrain(int slot) = 0; 
 * virtual bool cancelAddon() = 0; 
 * virtual bool cancelResearch() = 0; 
 * virtual bool cancelUpgrade() = 0; 
 * virtual bool useTech(TechType tech) = 0; 
 * virtual bool useTech(TechType tech, Position position) = 0;
 * virtual bool useTech(TechType tech, Unit* target) = 0;
 * 
 * Utilities: setLocalSpeed
 * 
 * On the java side, the command function definitions are provided in ProxyBot.
 * 
 * In StarCraft, commands take up to 3 arguments.
 */


public class GameCommand implements Serializable {

	/** the command to execute, as defined by StarCraftCommand */
	private CommandId.StarCraftCommand command;

	/** the unit to execute the command */
	private int unitID;

	/** the first argument */
	private int arg0;

	/** the second argument */
	private int arg1;

	/** the third argument */
	private int arg2;

	/**
	 * Creates a command
	 * 
	 * @param command
	 * @param unit_id
	 * @param arg_0
	 * @param arg_1
	 * @param arg_2
	 */
	public GameCommand(CommandId.StarCraftCommand cmdId, int unit_id, int arg_0, int arg_1, int arg_2) {
		command = cmdId;
		unitID = unit_id;
		arg0 = arg_0;
		arg1 = arg_1;
		arg2 = arg_2;
	}

	public boolean equals(GameCommand o) {
		return command == o.command && unitID == o.unitID && arg0 == o.arg0 && arg1 == o.arg1 && arg2 == o.arg2;
	}
  
  @Override
  public String toString() {
    return "GameCommand> GameCommand: " + command.name() + " UnitID: " + unitID + " Arg0: " + arg0 + " Arg1: " + arg1 + " arg2 " + arg2 + "<";
  }

  public String formatCmd() {
    return ":" + command.getNumValue() + ";" + unitID + ";" + arg0 + ";" + arg1 + ";" + arg2;
  }

  /**********************************************************
	 * GameCommands
	 *********************************************************/

	/**
	 * Tells the unit to attack move the specific location (in tile
	 * coordinates).
	 * 
	 * // virtual bool attackMove(Position position) = 0;
	 */
	public static GameCommand attackMove(int unitID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.attackMove, unitID, x, y, 0);
	}

	/**
	 * Tells the unit to attack another unit.
	 * 
	 * // virtual bool attackUnit(Unit* target) = 0;
	 */
	public static GameCommand attackUnit(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.attackUnit, unitID, targetID, 0, 0);
	}

	/**
	 * Tells the unit to right click (move) to the specified location (in tile
	 * coordinates).
	 * 
	 * // virtual bool rightClick(Position position) = 0;
	 */
	public static GameCommand rightClick(int unitID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.rightClick, unitID, x, y, 0);
	}

	/**
	 * Tells the unit to right click (move) on the specified target unit
	 * (Includes resources).
	 * 
	 * // virtual bool rightClick(Unit* target) = 0;
	 */
	public static GameCommand rightClick(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.rightClickUnit, unitID, targetID, 0, 0);
	}

	/**
	 * Tells the building to train the specified unit type.
	 * 
	 * // virtual bool train(UnitType type) = 0;
	 */
	public static GameCommand train(int unitID, int typeID) {
    return new GameCommand(CommandId.StarCraftCommand.train, unitID, typeID, 0, 0);
	}

	/**
	 * Tells a worker unit to construct a building at the specified location.
	 * 
	 * // virtual bool build(TilePosition position, UnitType type) = 0;
	 */
	public static GameCommand build(int unitID, int tx, int ty, int typeID) {
    return new GameCommand(CommandId.StarCraftCommand.build, unitID, tx, ty, typeID);
	}

	/**
	 * Tells the building to build the specified add on.
	 * 
	 * // virtual bool buildAddon(UnitType type) = 0;
	 */
	public static GameCommand buildAddon(int unitID, int typeID) {
    return new GameCommand(CommandId.StarCraftCommand.buildAddon, unitID, typeID, 0, 0);
	}

	/**
	 * Tells the building to research the specified tech type.
	 * 
	 * // virtual bool research(TechType tech) = 0;
	 */
	public static GameCommand research(int unitID, int techTypeID) {
    return new GameCommand(CommandId.StarCraftCommand.research, unitID, techTypeID, 0, 0);
	}

	/**
	 * Tells the building to upgrade the specified upgrade type.
	 * 
	 * // virtual bool upgrade(UpgradeType upgrade) = 0;
	 */
	public static GameCommand upgrade(int unitID, int upgradeTypeID) {
    return new GameCommand(CommandId.StarCraftCommand.upgrade, unitID, upgradeTypeID, 0, 0);
	}

	/**
	 * Orders the unit to stop moving. The unit will chase enemies that enter
	 * its vision.
	 * 
	 * // virtual bool stop() = 0;
	 */
	public static GameCommand stop(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.stop, unitID, 0, 0, 0);
	}

	/**
	 * Orders the unit to hold position. The unit will not chase enemies that
	 * enter its vision.
	 * 
	 * // virtual bool holdPosition() = 0;
	 */
	public static GameCommand holdPosition(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.holdPosition, unitID, 0, 0, 0);
	}

	/**
	 * Orders the unit to patrol between its current location and the specified
	 * location.
	 * 
	 * // virtual bool patrol(Position position) = 0;
	 */
	public static GameCommand patrol(int unitID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.patrol, unitID, x, y, 0);
	}

	/**
	 * Orders a unit to follow a target unit.
	 * 
	 * // virtual bool follow(Unit* target) = 0;
	 */
	public static GameCommand follow(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.follow, unitID, targetID, 0, 0);
	}

	/**
	 * Sets the rally location for a building.
	 * 
	 * // virtual bool setRallyPosition(Position target) = 0;
	 */
	public static GameCommand setRallyPosition(int unitID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.setRallyPosition, unitID, x, y, 0);
	}

	/**
	 * Sets the rally location for a building based on the target unit's current
	 * position.
	 * 
	 * // virtual bool setRallyUnit(Unit* target) = 0;
	 */
	public static GameCommand setRallyUnit(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.setRallyUnit, unitID, targetID, 0, 0);
	}

	/**
	 * Instructs an SCV to repair a target unit.
	 * 
	 * // virtual bool repair(Unit* target) = 0;
	 */
	public static GameCommand repair(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.repair, unitID, targetID, 0, 0);
	}

	/**
	 * Orders a zerg unit to morph to a different unit type.
	 * 
	 * // virtual bool morph(UnitType type) = 0;
	 */
	public static GameCommand morph(int unitID, int typeID) {
    return new GameCommand(CommandId.StarCraftCommand.morph, unitID, typeID, 0, 0);
	}

	/**
	 * Tells a zerg unit to burrow. Burrow must be upgraded for non-lurker
	 * units.
	 * 
	 * // virtual bool burrow() = 0;
	 */
	public static GameCommand burrow(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.burrow, unitID, 0, 0, 0);
	}

	/**
	 * Tells a burrowed unit to unburrow.
	 * 
	 * // virtual bool unburrow() = 0;
	 */
	public static GameCommand unburrow(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.unburrow, unitID, 0, 0, 0);
	}

	/**
	 * Orders a siege tank to siege.
	 * 
	 * // virtual bool siege() = 0;
	 */
	public static GameCommand siege(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.siege, unitID, 0, 0, 0);
	}

	/**
	 * Orders a siege tank to un-siege.
	 * 
	 * // virtual bool unsiege() = 0;
	 */
	public static GameCommand unsiege(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.unsiege, unitID, 0, 0, 0);
	}

	/**
	 * Tells a unit to cloak. Works for ghost and wraiths.
	 * 
	 * // virtual bool cloak() = 0;
	 */
	public static GameCommand cloak(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cloak, unitID, 0, 0, 0);
	}

	/**
	 * Tells a unit to decloak, works for ghosts and wraiths.
	 * 
	 * // virtual bool decloak() = 0;
	 */
	public static GameCommand decloak(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.decloak, unitID, 0, 0, 0);
	}

	/**
	 * GameCommands a Terran building to lift off.
	 * 
	 * // virtual bool lift() = 0;
	 */
	public static GameCommand lift(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.lift, unitID, 0, 0, 0);
	}

	/**
	 * GameCommands a terran building to land at the specified location.
	 * 
	 * // virtual bool land(TilePosition position) = 0;
	 */
	public static GameCommand land(int unitID, int tx, int ty) {
    return new GameCommand(CommandId.StarCraftCommand.land, unitID, tx, ty, 0);
	}

	/**
	 * Orders the transport unit to load the target unit.
	 * 
	 * // virtual bool load(Unit* target) = 0;
	 */
	public static GameCommand load(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.load, unitID, targetID, 0, 0);
	}

	/**
	 * Orders a transport unit to unload the target unit at the current
	 * transport location.
	 * 
	 * // virtual bool unload(Unit* target) = 0;
	 */
	public static GameCommand unload(int unitID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.unload, unitID, targetID, 0, 0);
	}

	/**
	 * Orders a transport to unload all units at the current location.
	 * 
	 * // virtual bool unloadAll() = 0;
	 */
	public static GameCommand unloadAll(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.unloadAll, unitID, 0, 0, 0);
	}

	/**
	 * Orders a unit to unload all units at the target location.
	 * 
	 * // virtual bool unloadAll(Position position) = 0;
	 */
	public static GameCommand unloadAll(int unitID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.unloadAllPosition, unitID, x, y, 0);
	}

	/**
	 * Orders a being to stop being constructed.
	 * 
	 * // virtual bool cancelConstruction() = 0;
	 */
	public static GameCommand cancelConstruction(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelConstruction, unitID, 0, 0, 0);
	}

	/**
	 * Tells an scv to pause construction on a building.
	 * 
	 * // virtual bool haltConstruction() = 0;
	 */
	public static GameCommand haltConstruction(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.haltConstruction, unitID, 0, 0, 0);
	}

	/**
	 * Orders a zerg unit to stop morphing.
	 * 
	 * // virtual bool cancelMorph() = 0;
	 */
	public static GameCommand cancelMorph(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelMorph, unitID, 0, 0, 0);
	}

	/**
	 * Tells a building to remove the last unit from its training queue.
	 * 
	 * // virtual bool cancelTrain() = 0;
	 */
	public static GameCommand cancelTrain(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelTrain, unitID, 0, 0, 0);
	}

	/**
	 * Tells a building to remove a specific unit from its queue.
	 * 
	 * // virtual bool cancelTrain(int slot) = 0;
	 */
	public static GameCommand cancelTrain(int unitID, int slot) {
    return new GameCommand(CommandId.StarCraftCommand.cancelTrainSlot, unitID, slot, 0, 0);
	}

	/**
	 * Orders a Terran building to stop constructing an add on.
	 * 
	 * // virtual bool cancelAddon() = 0;
	 */
	public static GameCommand cancelAddon(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelAddon, unitID, 0, 0, 0);
	}

	/***
	 * Tells a building cancel a research in progress.
	 * 
	 * // virtual bool cancelResearch() = 0;
	 */
	public static GameCommand cancelResearch(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelResearch, unitID, 0, 0, 0);
	}

	/***
	 * Tells a building cancel an upgrade in progress.
	 * 
	 * // virtual bool cancelUpgrade() = 0;
	 */
	public static GameCommand cancelUpgrade(int unitID) {
    return new GameCommand(CommandId.StarCraftCommand.cancelUpgrade, unitID, 0, 0, 0);
	}

	/**
	 * Tells the unit to use the specified tech, (i.e. STEM PACKS)
	 * 
	 * // virtual bool useTech(TechType tech) = 0;
	 */
	public static GameCommand useTech(int unitID, int techTypeID) {
    return new GameCommand(CommandId.StarCraftCommand.useTech, unitID, techTypeID, 0, 0);
	}

	/**
	 * Tells the unit to use tech at the target location.
	 * 
	 * Note: for AOE spells such as plague.
	 * 
	 * // virtual bool useTech(TechType tech, Position position) = 0;
	 */
	public static GameCommand useTech(int unitID, int techTypeID, int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.useTechPosition, unitID, techTypeID, x, y);
	}

	/**
	 * Tells the unit to use tech on the target unit.
	 * 
	 * Note: for targeted spells such as irradiate.
	 * 
	 * // virtual bool useTech(TechType tech, Unit* target) = 0;
	 */
	public static GameCommand useTech(int unitID, int techTypeID, int targetID) {
    return new GameCommand(CommandId.StarCraftCommand.useTechTarget, unitID, techTypeID, targetID, 0);
	}

	/**********************************************************
	 * Utilities
	 *********************************************************/

	/**
	 * Sets the game speed
	 * 
	 * 0 = 16xfastest
	 */
	public static GameCommand setGameSpeed(int speed) {
		return new GameCommand(CommandId.StarCraftCommand.gameSpeed, speed, 0, 0, 0);
	}

	/**
	 * Sets the camera postiion in StarCraft.
	 * 
	 * Note: x, y is the center of the position, the 320, 240 offset is handled
	 * on the c++ side
	 */
	public static GameCommand setScreenPosition(int x, int y) {
    return new GameCommand(CommandId.StarCraftCommand.screenPosition, x, y, 0, 0);
	}

	/**
	 * Draws a line on the map. 0,0 is the top left of the map
	 */
	public static GameCommand drawLineMap(int x1, int y1, int x2, int y2) {
    return new GameCommand(CommandId.StarCraftCommand.lineMap, x1, y1, x2, y2);
	}

	/**
	 * Draws a line on the screen. 0,0 is the top left of the screen
	 */
	public static GameCommand drawLineScreen(int x1, int y1, int x2, int y2) {
    return new GameCommand(CommandId.StarCraftCommand.lineScreen, x1, y1, x2, y2);
	}

	/**
	 * Draws a circle on the map. 0,0 is the top left of the map
	 */
	public static GameCommand drawCircleMap(int x, int y, int radius, boolean filled) {
    return new GameCommand(CommandId.StarCraftCommand.circleMap, x, y, radius, filled ? 1 : 0);
	}

	/**
	 * Draws a circle on the screen. 0,0 is the top left of the screen
	 */
	public static GameCommand drawCircleScreen(int x, int y, int radius, boolean filled) {
    return new GameCommand(CommandId.StarCraftCommand.circleScreen, x, y, radius, filled ? 1 : 0);
	}

	/**
	 * Draws a rectangle on the map. 0,0 is the top left of the map
	 * 
	 * Rectangles are not filled.
	 */
	public static GameCommand drawRectMap(int left, int top, int right, int bottom) {
    return new GameCommand(CommandId.StarCraftCommand.rectMap, left, top, right, bottom);
	}

	/**
	 * Draws a reactangle on the screen. 0,0 is the top left of the screen
	 * 
	 * Rectangles are not filled.
	 */
	public static GameCommand drawRectScreen(int left, int top, int right, int bottom) {
    return new GameCommand(CommandId.StarCraftCommand.rectScreen, left, top, right, bottom);
	}

	/**
	 * Draws a box on the map. 0,0 is the top left of the map
	 * 
	 * Boxes are filled.
	 */
	public static GameCommand drawBoxMap(int left, int top, int right, int bottom) {
    return new GameCommand(CommandId.StarCraftCommand.boxMap, left, top, right, bottom);
	}

	/**
	 * Draws a box on the screen. 0,0 is the top left of the screen
	 * 
	 * Boxes are filled.
	 */
	public static GameCommand drawBoxScreen(int left, int top, int right, int bottom) {
    return new GameCommand(CommandId.StarCraftCommand.boxScreen, left, top, right, bottom);
	}

	/**
	 * Sets the color context.
	 * 
	 * Only a subset of colors is supported: - red, green, blue, yellow, cyan,
	 * magenta, orange, black, white, gray
	 */
	public static GameCommand setColor(Color color) {
		int c = 0;
		if (Color.red.equals(color))
			c = 0;
		else if (Color.green.equals(color))
			c = 1;
		else if (Color.blue.equals(color))
			c = 2;
		else if (Color.yellow.equals(color))
			c = 3;
		else if (Color.cyan.equals(color))
			c = 4;
		else if (Color.magenta.equals(color))
			c = 5;
		else if (Color.orange.equals(color))
			c = 6;
		else if (Color.black.equals(color))
			c = 7;
		else if (Color.white.equals(color))
			c = 8;
		else if (Color.gray.equals(color))
			c = 9;

    return new GameCommand(CommandId.StarCraftCommand.color, c, 0, 0, 0);
	}

	/**
	 * Sets the color context.
	 * 
	 * See the values defined in setColor(Color color)
	 */
	public static GameCommand setColor(int color) {
    return new GameCommand(CommandId.StarCraftCommand.color, color, 0, 0, 0);
	}

	public static GameCommand leaveGame() {
    return new GameCommand(CommandId.StarCraftCommand.leaveGame, 0, 0, 0, 0);
	}

	public static GameCommand sayHello() {
    return new GameCommand(CommandId.StarCraftCommand.sayHello, 0, 0, 0, 0);
	}

	public static GameCommand sayGG() {
    return new GameCommand(CommandId.StarCraftCommand.sayGG, 0, 0, 0, 0);
	}

}//end GameCommand

