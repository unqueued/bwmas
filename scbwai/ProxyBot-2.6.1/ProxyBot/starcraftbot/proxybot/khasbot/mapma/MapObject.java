
package starcraftbot.proxybot.khasbot.mapma;

import java.io.Serializable;
import java.util.*;

public class MapObject implements Serializable{

	/** a list of the starting locations */
	private ArrayList<MapLocation> startingLocations;

	//private ArrayList<BaseLocationWME> baseLocations;
	//private ArrayList<ChokePointWME> chokePoints;
	//private ArrayList<RegionWME> regions;

  private String mapName; 

  private int mapWidth; 
  private int mapLength; 

  private int[][] mapHeight; 
  private boolean[][] buildable; 
  private boolean[][] walkable; 

  public MapObject(String startingLocationsData, String mapData){
     
    parseMapData(mapData);

    parseStartingLocationsData(startingLocationsData);
  }

  private void parseMapData(String mapData){
  
    String[] map = mapData.split(":"); 

    String map_data = map[3];

    mapName = map[0];
    mapWidth = Integer.parseInt(map[1]);
    mapLength = Integer.parseInt(map[2]);

    mapHeight = new int[mapLength][mapWidth];
    buildable = new boolean[mapLength][mapWidth];
    walkable = new boolean[mapLength][mapWidth];

		int total = mapWidth * mapLength;

		for (int i = 0; i < total; i++) {
			int w = i % mapWidth;
			int h = i / mapWidth;

			String tile = map_data.substring(3 * i, 3 * i + 3);

			mapHeight[h][w] = Integer.parseInt(tile.substring(0, 1));
			buildable[h][w] = (1 == Integer.parseInt(tile.substring(1, 2)));
			walkable[h][w] = (1 == Integer.parseInt(tile.substring(2, 3)));
  
    }
  }

  private void parseStartingLocationsData(String starting_location_data){

    startingLocations = new ArrayList<MapLocation>();
		String[] locs = starting_location_data.split(":");
		
    boolean first = true;

		for (String location : locs) {
			if (first) {
				first = false;
				continue;
			}

			String[] coords = location.split(";");

			MapLocation loc = new MapLocation(Integer.parseInt(coords[0]) + 2, Integer.parseInt(coords[1]) + 1);
			startingLocations.add(loc);
		}
  }
  	/**
  	 * @return MapObject
  	 */
  	public String toString()
  	{
  		return "MapObject: \nBuildable:\n" + displayBuildable() + "\nWalkable:\n" + displayWalkable() +
              "\nHeight:\n" + displayHeight();
  	}

	/**
	 * Displays the main properties.
	 */
	public String displayBuildable() {
    String out = "Name: " + mapName + "\nSize: " + mapWidth + " x " + mapLength + "\n";

		out += "\nBuildable:\n";
		out += "---------\n";
		for (int y = 0; y < mapLength; y++) {
			for (int x = 0; x < mapWidth; x++) {
				out += buildable[y][x] ? " " : "X";
			}
			out += "\n";
		}
    return out;
  }
public String displayWalkable() {
    String out = "Name: " + mapName + "\nSize: " + mapWidth + " x " + mapLength + "\n";

		out += "\nWalkable:\n";
		out += "---------\n";
		
		for (int y = 0; y < mapLength; y++) {
			for (int x = 0; x < mapWidth; x++) {
				out += buildable[y][x] ? " " : "X";
			}
			out += "\n";
		}
    return out;
  }
public String displayHeight() {
    String out = "Name: " + mapName + "\nSize: " + mapWidth + " x " + mapLength + "\n";

		out += "\nHeight:\n";
		out += "---------\n";
    
		for (int y = 0; y < mapLength; y++) {
			for (int x = 0; x < mapWidth; x++) {
				switch (mapHeight[y][x]) {
				case 2:
					out += " ";
					break;
				case 1:
					//System.out.print("*");
          out += "O";
					break;
				case 0:
					out += "X";
					break;
				}
			}
      out += "\n";
		}
    return out;
	}

}
