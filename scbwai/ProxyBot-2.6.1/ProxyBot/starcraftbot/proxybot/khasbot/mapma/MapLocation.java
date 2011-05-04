
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

  public String toString(){
    return "(" + x + "," + y + ")";
  }

}

