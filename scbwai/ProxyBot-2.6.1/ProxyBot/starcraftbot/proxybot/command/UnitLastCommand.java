package starcraftbot.proxybot.command;

import java.io.Serializable;

public class UnitLastCommand implements Serializable{
  private int unitId;
  private GameCommand last_cmd;

  public UnitLastCommand(int in_unitId, GameCommand in_last_cmd){
    unitId = in_unitId;
    last_cmd = in_last_cmd;
  }

  public int getUnitId(){
    return unitId;
  }

  public GameCommand getCommand(){
    return last_cmd;
  }

}
