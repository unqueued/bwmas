
package starcraftbot.proxybot.khasbot.mapma;

import java.io.*;
import java.util.*;
import java.util.logging.*;

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

//    try {
//      BufferedWriter heightOut = new BufferedWriter(new FileWriter(new File("map-parse-height" + System.currentTimeMillis() + ".txt")));
//      BufferedWriter buildOut = new BufferedWriter(new FileWriter(new File("map-parse-build" + System.currentTimeMillis() + ".txt")));
//      BufferedWriter walkOut = new BufferedWriter(new FileWriter(new File("map-parse-walk" + System.currentTimeMillis() + ".txt")));

      for (int i = 0; i < total; i++) {
        int w = i % mapWidth;
        int h = i / mapWidth;

        String tile = map_data.substring(3 * i, 3 * i + 3);

        mapHeight[h][w] = Integer.parseInt(tile.substring(0, 1));
//        if( mapHeight[h][w] == 0 )
//          heightOut.write(" ");
//        else if( mapHeight[h][w] == 1 )
//          heightOut.write("1");
//        else if( mapHeight[h][w] == 2 )
//          heightOut.write("2");
//        else if( mapHeight[h][w] == 3 )
//          heightOut.write("3");
//        else if( mapHeight[h][w] == 4 )
//          heightOut.write("X");
//        if( i%mapWidth == mapWidth -1) heightOut.write("\n");
        buildable[h][w] = (1 == Integer.parseInt(tile.substring(1, 2)));
//        buildOut.write((buildable[h][w] ? " " : "X"));
//        if( i%mapWidth == mapWidth -1) buildOut.write("\n");
        walkable[h][w] = (1 == Integer.parseInt(tile.substring(2, 3)));
//        walkOut.write((walkable[h][w] ? " " : "X"));
//        if( i%mapWidth == mapWidth -1) walkOut.write("\n");
      }
//      heightOut.close();
//      buildOut.close();
//      walkOut.close();
//    }catch(FileNotFoundException ex){
//      Logger.getLogger(MapManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
//    }catch(IOException ex){
//      Logger.getLogger(MapManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
//    }

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

  public MapLocation getStartLocation(int playerId){
    return startingLocations.get(playerId);
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
				out += walkable[y][x] ? " " : "X";
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
				case 4:
          out += "X";
					break;
				case 3:
					out += "3";
					break;
				case 2:
					out += "2";
					break;
				case 1:
          out += "1";
					break;
				case 0:
					out += " ";
					break;
				}
			}
      out += "\n";
		}
    return out;
	}

  	/**
  	 * @return String
  	 */
  	public String toString()
  	{
  		return "MapObject: \nBuildable:\n" + displayBuildable() + "\nWalkable:\n" + displayWalkable() +
              "\nHeight:\n" + displayHeight();
  	}


}
