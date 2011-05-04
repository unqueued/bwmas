package starcraftbot.proxybot.buildorders;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.*;

import starcraftbot.proxybot.game.*;
import starcraftbot.proxybot.khasbot.unitma.*;
import starcraftbot.proxybot.khasbot.resourcema.*;
import starcraftbot.proxybot.khasbot.unitma.*;
import starcraftbot.proxybot.khasbot.mapma.*;
import java.util.*;


public class BuildLoader {
	private DocumentBuilderFactory factory = null;
	private DocumentBuilder loader = null;
	private Document document = null;
	private Hashtable<String,LinkedList<LinkedList<BuildList>>> raceOrders = null;
	private LinkedList<BuildList> gameRaceOrders = null;
	private LinkedList<BuildList> gamePreCheck = null;
	private String currentRace = null;
	private int lastSupply = 0;
	private int lastUnitCount = 0;
	private int lastStructureCount = 0;
	private MapLocation playerMain = null;
	
	
	
	
	
	
	
	public BuildLoader(String xmlFile){
		try{
			String testOut = "";
			String race = null;
			LinkedList<BuildList> stepList = new LinkedList<BuildList>();
			LinkedList<LinkedList<BuildList>> raceSteps = new LinkedList<LinkedList<BuildList>>();
			NodeList steps = null;
			
			gamePreCheck = new LinkedList<BuildList>();
			raceOrders = new Hashtable< String,  LinkedList<LinkedList<BuildList>> >();
			
			
			factory = DocumentBuilderFactory.newInstance();
			loader = factory.newDocumentBuilder();
			//Load in xml file
			document = loader.parse(xmlFile);
			// Look for step element to load up orders
			// step hold supply and will hold orders for such
			
			NodeList raceLookup = document.getElementsByTagName("race");
			for(int r = 0; r<raceLookup.getLength(); r++){ //Races
				race = ((Element)raceLookup.item(r)).getAttribute("name").toString();
				testOut += race+" ";
				steps = ((Element)raceLookup.item(r)).getElementsByTagName("versus");
				for (int s = 0; s < steps.getLength(); s++){ //Steps
				    Element headNode = (Element)steps.item(s);
				    String versus = headNode.getAttribute("race");
				    if(versus=="")
				    	versus = "default";
				    if (headNode.getNodeType() == Node.ELEMENT_NODE) { 
				    	Element headElement = (Element) headNode;
				    	NodeList stepTags = headElement.getElementsByTagName("step");
				    	
				    	for(int st = 0;st<stepTags.getLength();st++){ //Step
				    		 Element stepElement = (Element) stepTags.item(st);
				    		 NodeList tags = stepElement.getChildNodes(); //grab all unit/structures/actions
				    		 BuildList tmp = new BuildList(Integer.parseInt(stepElement.getAttribute("supply")), versus);
				    		 for(int i=0;i<tags.getLength();i++){ // unit - structure - action
				    			 String Type = tags.item(i).getNodeName();
				    			 if(Type.equals("unit")){
				    				String name = race+"_"+tags.item(i).getAttributes().getNamedItem("name").getNodeValue();
				    				BuildUnit bu = new BuildUnit(Unit.valueOf(name).getNumValue());
				    				tmp.addOrder((BuildOrder)bu);
				    			 }else
				    			 if(Type.equals("structure")){
				    				String name = race+"_"+tags.item(i).getAttributes().getNamedItem("name").getNodeValue();
				    				String location = race+"_"+tags.item(i).getAttributes().getNamedItem("location");
				    				BuildStructure bs = new BuildStructure(Unit.valueOf(name).getNumValue(), location);
				    				tmp.addOrder((BuildOrder)bs);
				    			 }else
				    		     if(Type.equals("action")){
				    		    	String type = tags.item(i).getAttributes().getNamedItem("type").getNodeValue(); 
				    		    	String name = race+"_"+tags.item(i).getAttributes().getNamedItem("name").getNodeValue();
				    		    	String location = race+"_"+tags.item(i).getAttributes().getNamedItem("location");
				    		    	if(location=="")
				    		    		location = "enemy_main";
				    		    	String expendableString = race+"_"+tags.item(i).getAttributes().getNamedItem("location");
				    		    	boolean expendable = expendableString == "true" ? true: false;
				    		    	BuildAction ba = new BuildAction(type, Unit.valueOf(name).getNumValue(), location, expendable);
				    		    	tmp.addOrder((BuildOrder)ba);
				    		     }
				    		 }
				    		 /*System.out.println("Supply: "+tmp.getSupply()+" Len:"+tmp.getLength());
				    		 System.out.print("Things to do: ");
				    		 LinkedList we = tmp.getList();
				    		 for( int i=0; i< we.size(); i++){
				    			 System.out.print(we.get(i).toString());
				    		 }
				    		 System.out.println( );
				    		 */stepList.add(tmp);
				    	}//Step
				    	
				    }//Steps
				    
			    	raceSteps.add(stepList);
					stepList = null;
				}
		    	raceOrders.put(race, raceSteps);
		    	raceSteps = null;
			}
			System.out.println("Build Order for: "+testOut);
			
			
		} catch (ParserConfigurationException ex) {
			// document-loader cannot be created which,
			// satisfies the configuration requested
			handleError(ex);
		} catch (IOException ex) {
			// any IO errors occur:
			handleError(ex);
		} catch (SAXException ex) {
			// parse errors occur:
			handleError(ex);
		}  catch (FactoryConfigurationError ex) {
			// DOM-implementation is not available 
			// or cannot be instantiated:
			handleError(ex);
		}
	}
	private BuildList precheck(GameObjectUpdate gameObj){
		if(gamePreCheck.size()==0)
			return null;
	    Units playerUnits = gameObj.getUnitsInGame();
	    if(playerUnits==null)
	    	return null;
		HashMap<Integer, ArrayList<UnitObject>> gameUnits = (HashMap<Integer, ArrayList<UnitObject>>)playerUnits.getMyPlayersNonStructureUnits().clone();
		HashMap<Integer, ArrayDeque<UnitObject>> gameStructures = (HashMap<Integer, ArrayDeque<UnitObject>>)playerUnits.getMyPlayersStructureUnits().clone();
		BuildList todoList = null;
		
		if(gameUnits != null && gameStructures != null && gameUnits.isEmpty()!=true && gameStructures.isEmpty()!=true){
			
			
			for(int i=0; i<gamePreCheck.size(); i++){
				LinkedList<BuildOrder> preOrders = gamePreCheck.get(i).getList();
				for(int l=0; l<preOrders.size(); l++){
					if( preOrders.get(l) instanceof BuildStructure){
						BuildStructure bs = (BuildStructure)preOrders.get(l);
						if(gameStructures.get(bs.getID())!=null && gameStructures.get(bs.getID()).isEmpty()!=true){
							ArrayDeque<UnitObject> rm = gameStructures.get(bs.getID());
							rm.pop();
							gameStructures.put(bs.getID(), rm);
						}else{
							if(todoList==null)
								todoList = new BuildList();
							todoList.addOrder(preOrders.get(l));
						}
					}else
					if( preOrders.get(l) instanceof BuildUnit){
						BuildUnit bu = (BuildUnit)preOrders.get(l);
						if( gameUnits.get(bu.getID())!=null && gameUnits.get(bu.getID()).isEmpty()!=true){
							ArrayList<UnitObject> rm = gameUnits.get(bu.getID());
							rm.remove(0);
							gameUnits.put(bu.getID(), rm);
						}else{
							if(todoList==null)
								todoList = new BuildList();
							todoList.addOrder(preOrders.get(l));
						}
					}
				}
			}
		}
		gameStructures.clear();
		gameUnits.clear();
		return todoList;
	}
	public BuildList getUpdatedOrders(GameObjectUpdate gameObj){
		if(gameObj==null) //Check and make sure gameObj isn't null and on the outside call that way buildorder isn't dropped half way.
			return null;
		
		//PRECHECK
		
		BuildList preOrders = precheck(gameObj);
		if(preOrders!=null)
			return preOrders;
		//Now Check if there is anything even left to do before we continue.
		if(gameRaceOrders.size()==0)
			return null; //returning null will end buildOrder.
		
		int currentSupply = gameObj.getMyPlayer().getResources().getSupply()/2;
		//Precheck cleared onto the next step.
		//Move first Step from gameRaceOrders -> gamePreCheck
		//But first we make sure our first step supply is larger or equal to in list.
		
		
		if(gameRaceOrders.get(0).getSupply() > currentSupply){
			BuildList upSupply = new BuildList(1);
			if(currentRace=="Protoss")
				upSupply.addOrder((BuildOrder)new BuildUnit(Unit.valueOf("Protoss_Probe").getNumValue()));
			
			return upSupply;
		}else{
			BuildList tmp = gameRaceOrders.pollFirst();
			gamePreCheck.push(tmp);
			return tmp;
		}
	}
	public BuildList getOrders(GameObject gameObj){
		if(gameObj==null)
			return null;
		
		if(lastSupply==0){
			HashMap<Integer, ArrayList<UnitObject>> gameUnits = gameObj.getUnitsInGame().getMyPlayersNonStructureUnits();
			HashMap<Integer, ArrayList<UnitObject>> gameStructures = gameObj.getUnitsInGame().getMyPlayersNonStructureUnits();
			PlayerObject player = gameObj.getMyPlayer();
			int currentSupply = player.getResources().getSupply()/2;
			
			//Lets get the race and set it to gameRaceOrders so we don't have to keep looking for it.
			String enemyrace = gameObj.getEnemyPlayer().getPlayerRace().toString();
			
			playerMain = gameObj.getMapObj().getStartLocation(player.getPlayerID());
			currentRace = player.getPlayerRace().toString();
			lastSupply = currentSupply;
			lastUnitCount = gameUnits.size();
			lastStructureCount = gameStructures.size();
			
			LinkedList<BuildList> defaultList = null;
			LinkedList<LinkedList<BuildList>> allRaceOrders = raceOrders.get(currentRace);
			
			//while(rO.hasNext()){
			for(int i=0; i < allRaceOrders.size(); i++){
				LinkedList<BuildList> ordersList = allRaceOrders.get(i);
				if(ordersList.size()>0){
					String versusList = ordersList.get(0).getVersus();
					
					if(versusList.equals("default")){
						defaultList = ordersList;
					}else
					if(versusList.equals(enemyrace)){
						System.out.println("Build Plan: "+enemyrace);
						gameRaceOrders = ordersList;
						break;
					}
				}
			}
			if(gameRaceOrders==null){
				System.out.println("Build Plan: Default");
				gameRaceOrders = defaultList;
			}
		}
		//Send all buildings out here for BuildingManager to position and build.
		return gameRaceOrders.get(0);
	} 
	private static final void handleError(Throwable ex) {
		// ... handle error here...
		System.out.println(ex.toString());
	}
	
}
