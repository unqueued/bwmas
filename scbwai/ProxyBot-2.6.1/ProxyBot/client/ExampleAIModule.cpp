/**
 * AIModule implementation for communicating with a remote java process (ProxyBot).
 *
 * Uses the winsock library for sockets, include "wsock32.lib" in the linker inputs
 *
 * Note: this implementation uses a blocking socket. On each frame, an update message is
 *       sent to the ProxyBot, then the socket waits for a command message from the 
 *		 ProxyBot. The process blocks while waiting for a response, so the ProxyBot
 *		 should immediately respond to updates.
 *
 * TODO: modify core BWAPI to assign IDs to units and provide a way of retrieving
 *       units by their ID 
 */
#include "ExampleAIModule.h"
using namespace BWAPI;

#include <winsock.h>
#include <stdio.h>
#include <sstream>
#include <string>

/** port to connect to on the java side */
#define PORTNUM 12345

/** size of recv buffer for commands from the Proxy bot */
#define recvBufferSize 100000
char recieveBuffer[recvBufferSize];

/** buffer data buffer */
#define sendBufferSize 100000
char sendBuffer[sendBufferSize];

/** pixels per tile in starcraft */
#define pixelsPerTile 32

/** max indexes of static type data */
#define maxUnitTypes 230
#define maxTechTypes 47
#define maxUpgradeTypes 63

/** mapping of IDs to types */
UnitType unitTypeMap[maxUnitTypes];
TechType techTypeMap[maxTechTypes];
UpgradeType upgradeTypeMap[maxUpgradeTypes];

/** export information about static type data? */
bool exportTypeData = false;

/** display commands made by the bot?, recieved from ProxyBot */
bool logCommands = false;
int lastZoomFrame = 0;

/** mapping of unit objects to a unique ID, sent is sent to the java process */
std::map<Unit*, int> unitMap;
std::map<int, Unit*> unitIDMap;
std::map<BWTA::Region*, int> regionMap;


/** used to assign unit object IDs */
int unitIDCounter = 1;

/** used by the append method */
int digits[9];

/** socket identifier */
int proxyBotSocket = -1;

/** functions */
int initSocket();
void exportStaticData();
std::string toString(int value);
std::string toString(bool value);
void append(FILE *log, std::string data);
void handleCommand(int command, int unitID, int arg0, int arg1, int arg2);
BWAPI::UnitType getUnitType(int type); 
BWAPI::TechType getTechType(int type);
BWAPI::UpgradeType getUpgradeType(int type);
void loadTypeMaps();
BWAPI::TilePosition getTilePosition(int x, int y);
BWAPI::Position getPosition(int x, int y);
BWAPI::Unit* getUnit(int unitID);
int append(int val, char* buf, int currentIndex);
void drawHealth();
void drawTerrain();
void drawTargets();

Color color = BWAPI::Colors::Green;
bool showHealth = false;
bool showTerrain = false;
bool showTargets = false;

/**
 * Called at the start of a match. 
 */
void ExampleAIModule::onStart()
{
	loadTypeMaps();

	// export type data?
	if (exportTypeData) {
		exportTypeData = false;
		exportStaticData();
	}

	// First, check if we are already connected to the Java proxy
	if (proxyBotSocket == -1) {
		Broodwar->sendText("Connecting to ProxyBot");		
		proxyBotSocket = initSocket();

		// connected failed
		if (proxyBotSocket == -1) {
			Broodwar->sendText("ProxyBot connected failed");
			return;
		}
		else {
			Broodwar->sendText("Sucessfully connected to ProxyBot");
		}
	}
	else {
		Broodwar->sendText("Already connected to ProxyBot");
	}

	// 1. initiate communication with the proxy bot
	std::string ack("NewGame");
	ack += ";" + toString(Broodwar->self()->getID());

	std::set<Player*> players = Broodwar->getPlayers();
	for(std::set<Player*>::iterator i=players.begin();i!=players.end();i++) {
		int id = (*i)->getID();
		std::string race = (*i)->getRace().getName();
		std::string name = (*i)->getName();
		int type = (*i)->playerType().getID();
		bool ally = Broodwar->self()->isAlly(*i);

		ack += ":" + toString(id)
			 + ";" + race
			 + ";" + name 
			 + ";" + toString(type)
			 + ";" + (ally ? "1" : "0");
	}

	ack += "\n";
	send(proxyBotSocket,(char*)ack.c_str(), ack.size(), 0);

	// 2. Wait for bot options
	char buf[1024];
	int numBytes = recv(proxyBotSocket , buf , 1024 , 0);	
	if (buf[0] == '1') Broodwar->enableFlag(Flag::UserInput);
	if (buf[1] == '1') Broodwar->enableFlag(Flag::CompleteMapInformation); // Note: Fog of War remains
	logCommands = (buf[2] == '1');
	bool terrainAnalysis = (buf[3] == '1');

	// 3. send starting locations
	std::string locations("Locations");
	std::set<TilePosition> startSpots = Broodwar->getStartLocations();
	for(std::set<TilePosition>::iterator i=startSpots.begin();i!=startSpots.end();i++)
	{
		locations += ":" + toString(i->x())
				   + ";" + toString(i->y());
	}

	locations += "\n";
    char *slBuf = (char*)locations.c_str();
    send(proxyBotSocket, slBuf, locations.size(), 0);

	// 4. send the map data
	std::string mapName = Broodwar->mapName();
	int mapWidth = Broodwar->mapWidth();
	int mapHeight = Broodwar->mapHeight();

	std::string mapData(mapName);
	mapData += ":" + toString(mapWidth)
  	 		 + ":" + toString(mapHeight)
			 + ":";

	for (int y=0; y<mapHeight; y++) {	
		for (int x=0; x<mapWidth; x++) {
			mapData += toString(Broodwar->getGroundHeight(4*x, 4*y));
			mapData += (Broodwar->isBuildable(x, y)) ? "1" : "0";
			mapData += (Broodwar->isWalkable(4*x, 4*y)) ? "1" : "0";
		}
	}

	mapData += "\n";
	char *sbuf = (char*)mapData.c_str();
	send(proxyBotSocket, sbuf, mapData.size(), 0);

	// 5. send region data
	if (terrainAnalysis) {
		Broodwar->printf("Running TA");
		BWTA::readMap();
		BWTA::analyze();
		Broodwar->printf("Analyze Done");

		int regionCounter = 1;
		std::set<BWTA::Region*> theRegions = BWTA::getRegions();
		for (std::set<BWTA::Region*>::iterator i=theRegions.begin();i!=theRegions.end();i++)
		{
			regionMap[(*i)] = regionCounter++;
		}

		std::string regions("Regions");
		theRegions = BWTA::getRegions();
		for (std::set<BWTA::Region*>::iterator i=theRegions.begin();i!=theRegions.end();i++)
		{
			int id = regionMap[(*i)];
			int cx = (*i)->getCenter().x();
			int cy = (*i)->getCenter().y();

			regions+= ":" + toString(id)
					+ ";" + toString(cx)
					+ ";" + toString(cy)
					+ ";" + toString((int)(*i)->getPolygon().size());
/*
			regions += ";connected";
			std::set<BWTA::Region*> connected = (*i)->getReachableRegions();
			for (std::set<BWTA::Region*>::iterator j=connected.begin();j!=connected.end();j++) 
			{
				regions += "," + toString(regionMap[(*j)]);
			}
*/

			regions += ";outline";
			for (int j=0; j<(*i)->getPolygon().size(); j++) {
				Position position = (*i)->getPolygon()[j];
				regions += ";" + toString(position.x()) + ";" + toString(position.y());
			}
		}
		regions += "\n";
		char *rbuf = (char*)regions.c_str();
		send(proxyBotSocket, rbuf, regions.size(), 0);

		// 6. Send chokepoint data
		std::string chokes("Chokes");
		std::set<BWTA::Chokepoint*> chokepoints = BWTA::getChokepoints();
		for (std::set<BWTA::Chokepoint*>::iterator i=chokepoints.begin();i!=chokepoints.end();i++)
		{
			int region1 = regionMap[(*i)->getRegions().first];
			int region2 = regionMap[(*i)->getRegions().second];
 
			chokes += ":" + toString((*i)->getCenter().x())
					+ ";" + toString((*i)->getCenter().y())
					+ ";" + toString((int)(*i)->getWidth())
					+ ";" + toString(region1)
					+ ";" + toString(region2)
					+ ";" + toString((*i)->getSides().first.x())
					+ ";" + toString((*i)->getSides().first.y())
					+ ";" + toString((*i)->getSides().second.x())
					+ ";" + toString((*i)->getSides().second.y());
		}

		chokes += "\n";
		char *scbuf = (char*)chokes.c_str();
		send(proxyBotSocket, scbuf, chokes.size(), 0);

		// 7. send base location data
		std::string bases("Bases");
		std::set<BWTA::BaseLocation*> baseLocation =  BWTA::getBaseLocations();
		for (std::set<BWTA::BaseLocation*>::iterator i=baseLocation.begin();i!=baseLocation.end();i++)
		{
			bases += ":" + toString((*i)->getTilePosition().x())
				   + ";" + toString((*i)->getTilePosition().y());
		}

		bases += "\n";
		char *sbbuf = (char*)bases.c_str();
		send(proxyBotSocket, sbbuf, bases.size(), 0);
	}
}

/**
 * Runs every frame 
 *
 * Sends the unit status to the ProxyBot, then waits for a list of command messages.
 */
void ExampleAIModule::onFrame()
{
	// draw health bars
	if (Broodwar->getKeyState(BWAPI::Key::K_TAB)) {
		showHealth = !showHealth;
	}
	
	if (showHealth) {
		drawHealth();
	}

	// draw terrain
	if (Broodwar->getKeyState(BWAPI::Key::K_CONTROL)) {
		showTerrain = !showTerrain;
	}

	if (showTerrain) {
		drawTerrain();
	}

	// draw targets
	if (Broodwar->getKeyState(BWAPI::Key::K_T)) {
		showTargets = !showTargets;
	}

	if (showTargets) {
		drawTargets();
	}

	// check if the Proxy Bot is connected
	if (proxyBotSocket == -1) {
		return;
	}	

	// assign IDs to the units
	std::set<Unit*> units = Broodwar->getAllUnits();	
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		int unitID = unitMap[*i];

		if (unitID == 0) {
			unitID = unitIDCounter++; 
			unitMap[*i] = unitID;
			unitIDMap[unitID] = *i;
		}
	}

    // 1. send the unit status's to the Proxy Bot
	sendBuffer[0] = 's';
	int index = 1;
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->minerals(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->gas(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->supplyUsed(), sendBuffer, index);
	sendBuffer[index++] = ';';
	index = append(Broodwar->self()->supplyTotal(), sendBuffer, index);

	// get the research status
	// 0 - 46, player research
	// 47 - (2*47-1) player is researching
	// 2*47 - (3*47-1) enemy research
	int research[3*47];
	for (int i=0; i<(3*47); i++) research[i] = 0;

	std::set<TechType> tektypes = TechTypes::allTechTypes();
	for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++) {

		if (Broodwar->self()->hasResearched((*i))) {
			research[(*i).getID()] = 1;
		}

		if (Broodwar->self()->isResearching((*i))) {
			research[47 + (*i).getID()] = 1;
		}
	} 

	tektypes = TechTypes::allTechTypes();
	for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++) {
		if (Broodwar->enemy()->hasResearched((*i))) {
			research[(2*47) + (*i).getID()] = 1;
		}
	} 

	sendBuffer[index++] = ';';
	for (int i=0; i<(3*47); i++) {
		index = append(research[i], sendBuffer, index);
	}

	// get the upgrade status
	// 0 - 62, player upgrade level
	// 63 - (2*63-1) player is upgrading
	// 2*63 - (3*61-1) enemy upgrade level
	int ups[3*63];
	for (int i=0; i<(3*63); i++) ups[i] = 0;

	std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
	for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++) {
		ups[(*i).getID()] = Broodwar->self()->getUpgradeLevel((*i));
		
		if (Broodwar->self()->isUpgrading((*i))) {
			ups[63 + (*i).getID()] = 1;
		}
	}

	upTypes = UpgradeTypes::allUpgradeTypes();
	for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++) {
		ups[(2*63) + (*i).getID()] = Broodwar->enemy()->getUpgradeLevel((*i));
	}

	sendBuffer[index++] = ';';
	for (int i=0; i<(3*63); i++) {
		index = append(ups[i], sendBuffer, index);
	}

	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++)
	{
		int unitID = unitMap[*i];

		sendBuffer[index++] = ':';
		index = append(unitID, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getPlayer()->getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getType().getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getPosition().x(), sendBuffer, index);
//		index = append((*i)->getTilePosition().x(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getPosition().y(), sendBuffer, index);
//		index = append((*i)->getTilePosition().y(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getHitPoints(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getShields(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getEnergy(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingBuildTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingTrainTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingResearchTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getRemainingUpgradeTime(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getOrderTimer(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getOrder().getID(), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->isLifted() ? 1 : 0, sendBuffer, index);
//		index = append((char)(int)((*i)->isLifted()), sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getResources(), sendBuffer, index);
		sendBuffer[index++] = ';';
		Unit *addon = (*i)->getAddon();	// add on ID
		int addonID = 0;
		if (addon != NULL) addonID = unitMap[addon];
		index = append(addonID, sendBuffer, index);
		sendBuffer[index++] = ';';
		index = append((*i)->getSpiderMineCount(), sendBuffer, index);
	} 

	sendBuffer[index++] = '\n';
	send(proxyBotSocket, sendBuffer, index, 0);

	// 2. process commands
	int numBytes = recv(proxyBotSocket , recieveBuffer, recvBufferSize, 0);

	char *message = new char[numBytes + 1];
	message[numBytes] = 0;
	for (int i=0; i<numBytes; i++) 
	{ 
		message[i] = recieveBuffer[i];
	}

	// tokenize the commands
	char* token = strtok(message, ":");
	token = strtok(NULL, ":");			// eat the command part of the message
    int commandCount = 0;
    char* commands[1000];

	while (token != NULL) 
	{
		commands[commandCount] = token;
		commandCount++;
		token = strtok(NULL, ":");
	}

	// tokenize the arguments
	for (int i=0; i<commandCount; i++) 
	{
		char* command = strtok(commands[i], ";");
		char* unitID = strtok(NULL, ";");
		char* arg0 = strtok(NULL, ";");
		char* arg1 = strtok(NULL, ";");
		char* arg2 = strtok(NULL, ";");

		handleCommand(atoi(command), atoi(unitID), atoi(arg0), atoi(arg1), atoi(arg2));
	}
}

/** 
 * Append a number to the char array.
 */
int append(int val, char* buf, int currentIndex) 
{

	if (val <= 0) {
		buf[currentIndex++] = '0';
		return currentIndex;
	}

	for (int i=0; i<9; i++) {
		digits[i] = val%10;

		if (val >= 10) {
			val /= 10;
		}
		else {
			for (int j=i; j>=0; j--) {
				buf[currentIndex++] = ('0' + digits[j]);
			}

			break;
		}
	}

	return currentIndex;
}

/**
 * Executes the specified command with the given arguments. Does limited sanity checking.
 *
 * The command value is specified by the StarCraftCommand enumeration in Command.java.
 */
void handleCommand(int command, int unitID, int arg0, int arg1, int arg2)
{
	// extra commands

	// set game speed
	if (command == 41) {
		Broodwar->sendText("Set game speed: %d", unitID);
		Broodwar->setLocalSpeed(unitID);
		return;
	}
	// set the screen position
	else if (command == 42) {
		Broodwar->setScreenPosition(unitID - 320, arg0 - 240);
		return;
	}
	// draw a line in map coordinates
	else if (command == 43) {
		// x1, y1, x2, y2
		Broodwar->drawLineMap(unitID, arg0, arg1, arg2, color);
		return;
	}
	// draw a line in screen coordinates
	else if (command == 44) {
		// x1, y1, x2, y2
		Broodwar->drawLineScreen(unitID, arg0, arg1, arg2, color);
		return;
	}
	// draw a circle in map coordinates
	else if (command == 45) {
		// x, y, radius, color, solid
		Broodwar->drawCircleMap(unitID, arg0, arg1, color, arg2 == 1);
		return;
	}
	// draw a circle in screen coordinates
	else if (command == 46) {
		// x, y, radius, color, solid
		Broodwar->drawCircleScreen(unitID, arg0, arg1, color, arg2 == 1);
		return;
	}
	// draw a non-filled box in map coordinates
	else if (command == 47) {
		// left, top, right, bottom
		Broodwar->drawBoxMap(unitID, arg0, arg1, arg2, color, false);
		return;
	}
	// draw a non-filled box in screen coordinates
	else if (command == 48) {
		Broodwar->drawBoxScreen(unitID, arg0, arg1, arg2, color, false);
		return;
	}
	// draw a filled box in map coordinates
	else if (command == 49) {
		// left, top, right, bottom
		Broodwar->drawBoxMap(unitID, arg0, arg1, arg2, color, true);
		return;
	}
	// draw a filled box in screen coordinates
	else if (command == 50) {
		Broodwar->drawBoxScreen(unitID, arg0, arg1, arg2, color, true);
		return;
	}
	// set the color
	else if (command == 51) {
		switch (unitID) {
		case 0:
			color = BWAPI::Colors::Red;
			break;
		case 1:
			color = BWAPI::Colors::Green;
			break;
		case 2:
			color = BWAPI::Colors::Blue;
			break;
		case 3:
			color = BWAPI::Colors::Yellow;
			break;
		case 4:
			color = BWAPI::Colors::Cyan;
			break;
		case 5:
			color = BWAPI::Colors::Purple;
			break;
		case 6:
			color = BWAPI::Colors::Orange;
			break;
		case 7:
			color = BWAPI::Colors::Black;
			break;
		case 8:
			color = BWAPI::Colors::White;
			break;
		case 9:
			color = BWAPI::Colors::Grey;
			break;
		}	

		return;
	}
	// leave the game (surrender)
	else if (command == 52) {
		Broodwar->leaveGame();
		return;
	}
	// say hello
	else if (command == 53) {
		Broodwar->sendText("This is the EIS Bot!");
		Broodwar->sendText("I come from UC Santa Cruz!");
		return;
	}
	// say gg
	else if (command == 54) {
		Broodwar->sendText("gg");
		return;
	}

	// check that the unit ID is valid
	Unit* unit = unitIDMap[unitID];
	if (unit == NULL) {
		Broodwar->sendText("Issued command to invalid unit ID: %d", unitID);
		return;
	}

	// execute the command
	switch (command) {

	    // virtual bool attackMove(Position position) = 0;
		case 1:
			if (logCommands) Broodwar->sendText("Unit:%d attackMove(%d, %d)",unitID, arg0, arg1);
			unit->attackMove(getPosition(arg0, arg1));
			break;
		// virtual bool attackUnit(Unit* target) = 0;
		case 2:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d attackUnit(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d attackUnit(%d)", unitID, arg0);
				unit->attackUnit(getUnit(arg0));
			}
			break;
		// virtual bool rightClick(Position position) = 0;
		case 3:
			if (logCommands) Broodwar->sendText("Unit:%d rightClick(%d, %d)",unitID, arg0, arg1);
			unit->rightClick(getPosition(arg0, arg1));
			break;
		// virtual bool rightClick(Unit* target) = 0;
		case 4:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d rightClick(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d rightClick(%d)", unitID, arg0);
				unit->rightClick(getUnit(arg0));
			}
			break;
		// virtual bool train(UnitType type) = 0;
		case 5:
			if (getUnitType(arg0) < 0) { // NULL doesnt work here, NULL = 0, Terran_Marine=0
				Broodwar->sendText("Invalid Command, Unit:%d train(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d train(%d)", unitID, arg0);
				unit->train(getUnitType(arg0));
			}
			break;
		// virtual bool build(TilePosition position, UnitType type) = 0;
		case 6:
//			Broodwar->drawBox(CoordinateType::Map, 32*arg0, 32*arg1, 32*arg0 + 96, 32*arg1 + 64, Colors::Yellow, true);
			if (getUnitType(arg2) == NULL) {
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d build(%d, %d, %d)", unitID, arg0, arg1, arg2);
				unit->build(getTilePosition(arg0, arg1), getUnitType(arg2));
			}
			break;
		// virtual bool buildAddon(UnitType type) = 0;
		case 7:
			if (getUnitType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d buildAddon(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d buildAddon(%d)", unitID, arg0);
				unit->buildAddon(getUnitType(arg0));
			}
			break;
		// virtual bool research(TechType tech) = 0;
		case 8:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d research(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d research(%d)", unitID, arg0);
				unit->research(getTechType(arg0));
			}
			break;
		// virtual bool upgrade(UpgradeType upgrade) = 0;
		case 9:
			if (getUpgradeType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d upgrade(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d upgrade(%d)", unitID, arg0);
				unit->upgrade(getUpgradeType(arg0));
			}
			break;
		// virtual bool stop() = 0;
		case 10:
			if (logCommands) Broodwar->sendText("Unit:%d stop()", unitID);
			unit->stop();
			break;
		// virtual bool holdPosition() = 0;
		case 11:
			if (logCommands) Broodwar->sendText("Unit:%d holdPosition()", unitID);
			unit->holdPosition();
			break;
		// virtual bool patrol(Position position) = 0;
		case 12:
			if (logCommands) Broodwar->sendText("Unit:%d patrol(%d, %d)", unitID, arg0, arg1);
			unit->patrol(getPosition(arg0, arg1));
			break;
		// virtual bool follow(Unit* target) = 0;
		case 13:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d follow(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d follow(%d)", unitID, arg0);
				unit->follow(getUnit(arg0));
			}
			break;
		// virtual bool setRallyPosition(Position target) = 0;
		case 14:
			if (logCommands) Broodwar->sendText("Unit:%d setRallyPosition(%d, %d)", unitID, arg0, arg1);
			unit->setRallyPosition(getPosition(arg0, arg1));
			break;
		// virtual bool setRallyUnit(Unit* target) = 0;
		case 15:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d setRallyUnit(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d setRallyUnit(%d)", unitID, arg0);
				unit->setRallyUnit(getUnit(arg0));
			}
			break;
		// virtual bool repair(Unit* target) = 0;
		case 16:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d repair(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d repair(%d)", unitID, arg0);
				unit->repair(getUnit(arg0));
			}
			break;
		// virtual bool morph(UnitType type) = 0;
		case 17:
			if (getUnitType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d morph(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d morph(%d)", unitID, arg0);
				unit->morph(getUnitType(arg0));
			}
			break;
		// virtual bool burrow() = 0;
		case 18:
			if (logCommands) Broodwar->sendText("Unit:%d burrow()", unitID);
			unit->burrow();
			break;
		// virtual bool unburrow() = 0;
		case 19:
			if (logCommands) Broodwar->sendText("Unit:%d unburrow()", unitID);
			unit->unburrow();
			break;
		// virtual bool siege() = 0;
		case 20:
			if (logCommands) Broodwar->sendText("Unit:%d siege()", unitID);
			unit->siege();
			break;
		// virtual bool unsiege() = 0;
		case 21:
			if (logCommands) Broodwar->sendText("Unit:%d unsiege()", unitID);
			unit->unsiege();
			break;
		// virtual bool cloak() = 0;
		case 22:
			if (logCommands) Broodwar->sendText("Unit:%d cloak()", unitID);
			unit->cloak();
			break;
		// virtual bool decloak() = 0;
		case 23:
			if (logCommands) Broodwar->sendText("Unit:%d decloak()", unitID);
			unit->decloak();
			break;
		// virtual bool lift() = 0;
		case 24:
			if (logCommands) Broodwar->sendText("Unit:%d lift()", unitID);
			unit->lift();
			break;
		// virtual bool land(TilePosition position) = 0;
		case 25:
			if (logCommands) Broodwar->sendText("Unit:%d land(%d, %d)", unitID, arg0, arg1);
			unit->land(getTilePosition(arg0, arg1));
			break;
		// virtual bool load(Unit* target) = 0;
		case 26:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d load(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d load(%d)", unitID, arg0);
				unit->load(getUnit(arg0));
			}
			break;
		// virtual bool unload(Unit* target) = 0;
		case 27:
			if (getUnit(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d unload(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d unload(%d)", unitID, arg0);
				unit->unload(getUnit(arg0));
			}
			break;
		// virtual bool unloadAll() = 0;
		case 28:
			if (logCommands) Broodwar->sendText("Unit:%d unloadAll()", unitID);
			unit->unloadAll();
			break;
		// virtual bool unloadAll(Position position) = 0;
		case 29:
			if (logCommands) Broodwar->sendText("Unit:%d unloadAll(%d, %d)", unitID, arg0, arg1);
			unit->unloadAll(getPosition(arg0, arg1));
			break;
		// virtual bool cancelConstruction() = 0;
		case 30:
			if (logCommands) Broodwar->sendText("Unit:%d cancelConstruction()", unitID);
			unit->cancelConstruction();
			break;
		// virtual bool haltConstruction() = 0;
		case 31:
			if (logCommands) Broodwar->sendText("Unit:%d haltConstruction()", unitID);
			unit->haltConstruction();
			break;
		// virtual bool cancelMorph() = 0;
		case 32:
			if (logCommands) Broodwar->sendText("Unit:%d cancelMorph()", unitID);
			unit->cancelMorph();
			break;
		// virtual bool cancelTrain() = 0;
		case 33:
			if (logCommands) Broodwar->sendText("Unit:%d cancelTrain()", unitID);
			unit->cancelTrain();
			break;
		// virtual bool cancelTrain(int slot) = 0;
		case 34:
			if (logCommands) Broodwar->sendText("Unit:%d cancelTrain(%d)", unitID, arg0);
			unit->cancelTrain(arg0);
			break;
		// virtual bool cancelAddon() = 0;
		case 35:
			if (logCommands) Broodwar->sendText("Unit:%d cancelAddon()", unitID);
			unit->cancelAddon();
			break;
		// virtual bool cancelResearch() = 0;
		case 36:
			if (logCommands) Broodwar->sendText("Unit:%d cancelResearch()", unitID);
			unit->cancelResearch();
			break;
		// virtual bool cancelUpgrade() = 0;
		case 37:
			if (logCommands) Broodwar->sendText("Unit:%d cancelUpgrade()", unitID);
			unit->cancelUpgrade();
			break;
		// virtual bool useTech(TechType tech) = 0;
		case 38:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d)", unitID, arg0);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d)", unitID, arg0);
				unit->useTech(getTechType(arg0));
			}
			break;
		// virtual bool useTech(TechType tech, Position position) = 0;
		case 39:
			if (getTechType(arg0) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d, %d, %d)", unitID, arg0, arg1, arg2);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d, %d, %d)", unitID, arg0, arg1, arg2);
				unit->useTech(getTechType(arg0), getPosition(arg1, arg2));
			}
			break;
		// virtual bool useTech(TechType tech, Unit* target) = 0;
		case 40:
			if (getTechType(arg0) == NULL || getUnit(arg1) == NULL) {
				Broodwar->sendText("Invalid Command, Unit:%d useTech(%d, %d)", unitID, arg0, arg1);
			}
			else {
				if (logCommands) Broodwar->sendText("Unit:%d useTech(%d, %d)", unitID, arg0, arg1);
				unit->useTech(getTechType(arg0), getUnit(arg1));
			}
			break;
		default:
			break;
	}
}

/**
 * Called at the end of a game. This is where we shut down sockets and clean
 * up any data structures we have created.
 */
void ExampleAIModule::onEnd(bool isWinner) 
{
	if (proxyBotSocket == -1) {
		return;
	}

	// say if the bot won or lost
	std::string endData("ended:");
	endData += toString(isWinner);
	endData += "\n";
	char *sbuf = (char*)endData.c_str();
	send(proxyBotSocket, sbuf, endData.size(), 0);

	closesocket(proxyBotSocket);
}

void ExampleAIModule::onAddUnit(Unit* unit)
{
	
}

/**
 * Removes the unit from the ID->unit mapping
 */
void ExampleAIModule::onRemove(BWAPI::Unit* unit)
{
	int key = unitMap.erase(unit);
	unitIDMap.erase(key);
}

bool ExampleAIModule::onSendText(std::string text)
{
	return true;
}

/**
 * Utility function for constructing a Position.
 *
 * Note: positions are in pixel coordinates, while the inputs are given in tile coordinates
 */
Position getPosition(int x, int y)
{
	return BWAPI::Position(x, y);
}

/**
 * Utility function for constructing a TilePosition.
 *
 * Note: not sure if this is correct, is there a way to get a tile position
 *       object from the api rather than create a new one?
 */
TilePosition getTilePosition(int x, int y)
{
	return BWAPI::TilePosition(x, y);
}

/**
 * Utiliity function for int to string conversion.
 */
std::string toString(int value) 
{
	std::stringstream ss;
	ss << value;
	return ss.str();
}

/**
 * Utiliity function for bool to string conversion.
 */
std::string toString(bool value) 
{
	if (value) return std::string("1");
	else return std::string("0");
}

/**
 * Returns the unit based on the unit ID
 */
Unit* getUnit(int unitID)
{
	return unitIDMap[unitID];
}

/** 
 * Returns the unit type from its identifier
 */
UnitType getUnitType(int type) 
{
	return unitTypeMap[type];
}

/** 
 * Returns the tech type from its identifier
 */
TechType getTechType(int type) 
{
	return techTypeMap[type];
}

/** 
 * Returns the upgrade type from its identifier
 */
UpgradeType getUpgradeType(int type)
{
	return upgradeTypeMap[type];
}

/**
 * Utility function for appending data to a file.
 */
void append(FILE *log, std::string data) {
	data += "\n";
	fprintf(log, (char*)data.c_str());
	fflush(log);
}

/**
 * Builds the mapping of Indices to actual BW objects.
 */
void loadTypeMaps() 
{
  std::set<UnitType> types = UnitTypes::allUnitTypes();
  for(std::set<UnitType>::iterator i=types.begin();i!=types.end();i++)
  {
	  unitTypeMap[i->getID()] = (*i);
  }

  std::set<TechType> tektypes = TechTypes::allTechTypes();
  for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++)
  {
	  techTypeMap[i->getID()] = (*i);
  }

  std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
  for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++)
  {
	  upgradeTypeMap[i->getID()] = (*i);
  }
}

/**
 * Exports static data about UnitTypes, TypeTypes, and UpgradeTypes to a text 
 * file name "TypeData.txt"
 */
void exportStaticData() {

  FILE *typeData = 0;
  typeData = fopen("TypeData.txt", "w");

  // Export unit type data
  append(typeData, "UnitTypes");
  append(typeData, "-id,race,name,mins,gas,hitPoints,shields,energy,buildTime,canAttack,canMove,width,height,supply,supplyProvided,sight,groundMaxRange,groundMinRange,groundDamage,airRange,airDamage,isBuilding,isFlyer,isSpellCaster,isWorker,whatBuilds");

  std::set<UnitType> types = UnitTypes::allUnitTypes();
  for(std::set<UnitType>::iterator i=types.begin();i!=types.end();i++)
  {
	  int id = i->getID();
	  std::string race = i->getRace().getName();
	  std::string name = i->getName();
	  int minerals = i->mineralPrice();
	  int gas = i->gasPrice();
	  int hitPoints = i->maxHitPoints()/256;
	  int shields = i->maxShields();
	  int energy = i->maxEnergy();
	  int buildTime = i->buildTime();
	  bool canAttack = i->canAttack();
	  bool canMove = i->canMove();
	  int width = i->tileWidth();
	  int height = i->tileHeight();
	  int supplyRequired = i->supplyRequired();
	  int supplyProvided = i->supplyProvided();
	  int sightRange = i->sightRange();
	  int groundMaxRange = i->groundWeapon()->maxRange();
	  int groundMinRange = i->groundWeapon()->minRange();
	  int groundDamage = i->groundWeapon()->damageAmount();
	  int airRange = i->airWeapon()->maxRange();
	  int airDamage = i->airWeapon()->damageAmount();
	  bool isBuilding = i->isBuilding();
	  bool isFlyer = i->isFlyer();
	  bool isSpellCaster = i->isSpellcaster();
	  bool isWorker = i->isWorker();
	  int whatBuilds = i->whatBuilds().first->getID();

	  std::string unitType(" UnitType");
	  unitType += ":" + toString(id)
			  + ":" + race
			  + ":" + name
			  + ":" + toString(minerals)
			  + ":" + toString(gas)
			  + ":" + toString(hitPoints)
			  + ":" + toString(shields)
			  + ":" + toString(energy)
			  + ":" + toString(buildTime)
			  + ":" + toString(canAttack)
			  + ":" + toString(canMove)
			  + ":" + toString(width)
			  + ":" + toString(height)
			  + ":" + toString(supplyRequired)
			  + ":" + toString(supplyProvided)
			  + ":" + toString(sightRange)
			  + ":" + toString(groundMaxRange)
			  + ":" + toString(groundMinRange)
			  + ":" + toString(groundDamage)
			  + ":" + toString(airRange)
			  + ":" + toString(airDamage)
			  + ":" + toString(isBuilding)
			  + ":" + toString(isFlyer)
			  + ":" + toString(isSpellCaster)
			  + ":" + toString(isWorker)
			  + ":" + toString(whatBuilds);

	  append(typeData, unitType);
  }

  // Export tech types
  append(typeData, "TechTypes");
  append(typeData, "-id,name,whatResearches,minerals,gas");

  std::set<TechType> tektypes = TechTypes::allTechTypes();
  for(std::set<TechType>::iterator i=tektypes.begin();i!=tektypes.end();i++)
  {
	  int id = i->getID();
	  std::string name = i->getName();
	  int whatResearchesID = i->whatResearches()->getID(); 
	  int mins = i->mineralPrice();
	  int gas = i->gasPrice();

	  std::string techType(" TechType"); 
	  techType += ":" + toString(id)
				+ ":" + name
				+ ":" + toString(whatResearchesID)
				+ ":" + toString(mins)
				+ ":" + toString(gas);

	  append(typeData, techType);
  }

  // Export upgrade types
  append(typeData, "UpgradeTypes");
  append(typeData, "-id,name,whatUpgrades,repeats,minBase,minFactor,gasBase,gasFactor");

  std::set<UpgradeType> upTypes = UpgradeTypes::allUpgradeTypes();
  for(std::set<UpgradeType>::iterator i=upTypes.begin();i!=upTypes.end();i++)
  {
	  int id = i->getID();
	  std::string name = i->getName();
	  int whatUpgradesID = i->whatUpgrades()->getID(); // unit type id of what researches it
	  int repeats = i->maxRepeats();
	  int minBase = i->mineralPriceBase();
	  int minFactor = i->mineralPriceFactor();
	  int gasBase = i->gasPriceBase();
	  int gasFactor = i->gasPriceFactor();
	  
	  std::string upgradeType(" UpgradeType"); 
	  upgradeType += ":" + toString(id)
				  + ":" + name
				  + ":" + toString(whatUpgradesID)
				  + ":" + toString(repeats)
				  + ":" + toString(minBase)
				  + ":" + toString(minFactor)
				  + ":" + toString(gasBase)
				  + ":" + toString(gasFactor);

	  append(typeData, upgradeType);
  }
}

/**
 * Establishes a connection with the ProxyBot.
 *
 * Returns -1 if the connection fails
 */
int initSocket() 
{
      int sockfd;
      int size;
      struct hostent *h;
      struct sockaddr_in client_addr;
      char myname[256];
      WORD wVersionRequested;
      WSADATA wsaData;

      wVersionRequested = MAKEWORD( 1, 1 );
      WSAStartup( wVersionRequested, &wsaData );
      gethostname(myname, 256);      
      h=gethostbyname(myname);

      size = sizeof(client_addr);
      memset(&client_addr , 0 , sizeof(struct sockaddr_in));
      memcpy((char *)&client_addr.sin_addr , h -> h_addr ,h -> h_length);
     
	  client_addr.sin_family = AF_INET;
      client_addr.sin_port = htons(PORTNUM);
      client_addr.sin_addr =  *((struct in_addr*) h->h_addr) ;
      if ((sockfd = socket(AF_INET , SOCK_STREAM , 0)) == -1){
		  return -1;
      }

      if ((connect(sockfd , (struct sockaddr *)&client_addr , sizeof(client_addr))) == -1){
		  return -1;
	  }

	  return sockfd;
}

/**
 * Draws health boxes for units
 */
void drawHealth() 
{
	std::set<Unit*> units = Broodwar->self()->getUnits();
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		int health = (*i)->getHitPoints();

		if (health > 0) {
			int x = (*i)->getPosition().x();
			int y = (*i)->getPosition().y();
			int l = (*i)->getType().dimensionLeft();
			int t = (*i)->getType().dimensionUp();
			int r = (*i)->getType().dimensionRight();
			int b = (*i)->getType().dimensionDown();
			int max = (*i)->getType().maxHitPoints();
			int width = ((r + l)*health)/max;

			if (health*3<max) {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Red, true);
			}
			else if (health*3<2*max) {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Yellow, true);
			}
			else {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Green, true);
			}

			Broodwar->drawBoxMap(x - l, y - t - 5, x + r, y - t, BWAPI::Colors::White, false);
			Broodwar->drawBoxMap(x - l, y - t, x + r, y + b, BWAPI::Colors::White, false);
		}
	}

	units = Broodwar->enemy()->getUnits();
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		int health = (*i)->getHitPoints();

		if (health > 0) {
			int x = (*i)->getPosition().x();
			int y = (*i)->getPosition().y();
			int l = (*i)->getType().dimensionLeft();
			int t = (*i)->getType().dimensionUp();
			int r = (*i)->getType().dimensionRight();
			int b = (*i)->getType().dimensionDown();
			int max = (*i)->getType().maxHitPoints();
			int width = ((r + l)*health)/max;

			if (health*3<max) {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Red, true);
			}
			else if (health*3<2*max) {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Yellow, true);
			}
			else {
				Broodwar->drawBoxMap(x - l, y - t - 5, x - l + width, y - t, BWAPI::Colors::Green, true);
			}

			Broodwar->drawBoxMap(x - l, y - t - 5, x + r, y - t, BWAPI::Colors::Red, false);
			Broodwar->drawBoxMap(x - l, y - t, x + r, y + b, BWAPI::Colors::Red, false);
			Broodwar->drawTextMap(x - l, y - t, (*i)->getType().getName().c_str());
		}
	}
}
 
/**
 * Draws the targets of each unit.
 */
void drawTargets() {

	std::set<Unit*> units = Broodwar->self()->getUnits();
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		Unit* target = (*i)->getTarget(); 
		if (target != NULL) {
			Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
				target->getPosition().x(), target->getPosition().y(), BWAPI::Colors::Yellow);
		}

		target = (*i)->getOrderTarget(); 
		if (target != NULL) {
			Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
				target->getPosition().x(), target->getPosition().y(), BWAPI::Colors::Yellow);
		}

		Position position = (*i)->getTargetPosition(); 
		Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
			position.x(), position.y(), BWAPI::Colors::Yellow);
	}	

	units = Broodwar->enemy()->getUnits();
	for(std::set<Unit*>::iterator i=units.begin();i!=units.end();i++) {
		Unit* target = (*i)->getTarget(); 
		if (target != NULL) {
			Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
				target->getPosition().x(), target->getPosition().y(), BWAPI::Colors::Purple);
		}

		target = (*i)->getOrderTarget(); 
		if (target != NULL) {
			Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
				target->getPosition().x(), target->getPosition().y(), BWAPI::Colors::Purple);
		}

		Position position = (*i)->getTargetPosition(); 
		Broodwar->drawLineMap((*i)->getPosition().x(), (*i)->getPosition().y(), 
			position.x(), position.y(), BWAPI::Colors::Purple);
	}	
}

/**
 * Draws the BWAPI regions and chokepoints.
 */
void drawTerrain() 
{
	std::set<BWTA::Region*> theRegions = BWTA::getRegions();
	for (std::set<BWTA::Region*>::iterator i=theRegions.begin();i!=theRegions.end();i++)
	{
		int lx = 0;
		int ly = 0;
		bool first = true;

		for (int j=0; j<(*i)->getPolygon().size(); j++) {
			Position position = (*i)->getPolygon()[j];

			if (!first) {
				Broodwar->drawLineMap(position.x(), position.y(), lx, ly, BWAPI::Colors::Grey);
			}

			first = false;
			lx = position.x(); 
			ly = position.y(); 
		}

		Position position = (*i)->getPolygon()[0];
		Broodwar->drawLineMap(position.x(), position.y(), lx, ly, BWAPI::Colors::Grey);
	}

	std::set<BWTA::Chokepoint*> chokepoints = BWTA::getChokepoints();
	for (std::set<BWTA::Chokepoint*>::iterator i=chokepoints.begin();i!=chokepoints.end();i++)
	{
		Broodwar->drawCircleMap((*i)->getCenter().x(), (*i)->getCenter().y(), (int)(*i)->getWidth()/2, BWAPI::Colors::Orange, false);
	}
}
