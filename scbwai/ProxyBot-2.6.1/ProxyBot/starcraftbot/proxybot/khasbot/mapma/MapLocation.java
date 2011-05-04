
package starcraftbot.proxybot.khasbot.mapma;

import java.io.Serializable;


public class MapLocation implements Serializable{

  private int x; 
  private int y; 

  public MapLocation(int in_x, int in_y){
    x = in_x;
    y = in_y;
  }

  public void setX(int in ){
    x = in;
  }

  public void setY(int in){
    y = in;
  }

  public int getX(){
    return x;
  }

  public int getY(){
    return y;
  }

  /**
   * This method will return a MapLocation from a this current one in the following
   * directions:
   *                  8  1  2
   *                   \ | /
   *                 7 - + - 3
   *                   / | \
   *                  6  5  4
   * 
   * @param distance
   * @param direction
   * @return
   */
  public MapLocation distToNewLoc(int distance, int direction){
    if(direction == 1){
      return new MapLocation(x, y-distance);
    }else if(direction == 2){
      return new MapLocation(x+distance, y-distance);
    }else if(direction == 3){
      return new MapLocation(x+distance, y);
    }else if(direction == 4){
      return new MapLocation(x+distance, y+distance);
    }else if(direction == 5){
      return new MapLocation(x, y+distance);
    }else if(direction == 6){
      return new MapLocation(x-distance, y+distance);
    }else if(direction == 7){
      return new MapLocation(x-distance, y);
    }else if(direction == 8){
      return new MapLocation(x-distance, y-distance);
    }else{
      return new MapLocation(x, y);
    }
  }

  @Override
  public String toString(){
    return "(" + x + "," + y + ")";
  }

}

