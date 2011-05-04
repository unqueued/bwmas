
package starcraftbot.proxybot;

/**
 * This class is used to generate a conversationId that can be used between two agents.
 *
 * This helps the two agents carry on multiple converstations, while still being able to 
 * distinguish which is which.
 */
public class ConverId{

  
  public enum Game{
    GameObjUpdate("Game-Object-Update"),
    InitGameObj("Initial-Game-Object");
    
    String conversation_id;
    
    Game(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  public enum Commands{
    ExecuteCommand("Execute-Command");

    String conversation_id;

    Commands(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  /*
   * Commander will not be providing services or information like the agents
   * below. It will be issuing orders, so these conversation id's will be from
   * it's persepective.
   */
  public enum Commander{
    EndGamePhaseOne("End-Game-Phase-One"), //PhaseOne is the opening phase of the game
    StartGamePhaseTwo("Start-Game-Phase-Two"),
    EndGamePhaseTwo("End-Game-Phase-Two"),
    StartGamePhaseThree("Start-Game-Phase-Three"),
    BuildOrders("Build-Orders");

    String conversation_id;

    Commander(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  /*
   * The conversation id's below are used slightly different, since they
   * provide services, but I didn't want to break up the file, since
   * technically all conversations ask/tell an agent to do something or
   * to give something.
   *
   * So the names of the conversations will be like:
   *    what service/materials do i provide (new building, new probe ....)
   *    what kind of information can i give to other agents
   */
  
  /* conversation id's that are focused around the building manager */
  public enum BattM{
    LocationEnemySeen("Location-Enemy-Seen"),
    NeedUnits("Need-Units"); //a request for any units to be built

    String conversation_id;

    BattM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  /**
   * conversation id's that are focused around the building manager
   * The payload of the msg.setContent(String PAYLOAD) is defined next to each conversation.
   */
  public enum BuildM{

    NeedWorker("Need-Worker"), //PAYLOAD> unitId:count
    ReturningWorker("Returning-Worker"), //PAYLOAD> unitId
    BuildStructure("Build-Structure"); //PAYLOAD> unitId:count


    String conversation_id;

    BuildM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  /* conversation id's that are focused around the map manager */
  public enum MapM{
    PathToNewBuilding("Path-To-New-Building"),
    PathToChokePoint("Path-To-Choke-Point"),
    NearestMinerals("Nearest-Minerals"),
    NearestMineralsSuccess("Nearest-Minerals-Success"),
    NearestMineralsFailure("Nearest-Minerals-Failure"),
    NearestGas("Nearest-Gas"),
    NearestGasSuccess("Nearest-Gas-Success"),
    NearestGasFailure("Nearest-Gas-Failure");

    String conversation_id;

    MapM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

   /* conversation id's that are focused around the map manager */
  public enum ResM{
    NeedWorker("Need-Worker"),
    EnoughResourcesToBuild("Nearest-Gas");

    String conversation_id;

    ResM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

   /**
    * conversation id's that are focused around the map manager
    * The payload of the msg.setContent(String PAYLOAD) is defined next to each conversation.
    */
  public enum StructM{
    TrainNewUnit("Train-New-Unit"), //PAYLOAD> unitId:count
    UpgradeTechLevel("Upgrade-Tech-Level"),
    ResearchTechLevel("Research-Tech-Level");

    String conversation_id;

    StructM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

  /* conversation id's that are focused around the building manager */
  public enum UnitM{
    /* services/data that will be provided for MapM */
    NeedGameObject("Need-Game-Object"),
    NeedGameObjectUpdate("Need-Game-Object-Update"),
    NeedPlayerObject("Need-Player-Object"),
    NeedStartLocation("Need-Start-Location"),
    
    NewStructureToBuild("New-Structure-To-Build"),
    NeedWorker("Need-Worker"),
    RetaskWorker("Retask-Worker"),
    SmallUnitGroup("Small-Unit-Group"),   //we will have to define this size
    MediumUnitGroup("Medium-Unit-Group"), //we will have to define this size
    LargeUnitGroup("Large-Unit-Group"),   //we will have to define this size
    UnitsLost("Units-Lost"),
    NewUnit("New-Unit"), //a request for a new unit to be built, say from battle manager
    NewCommands("New-Commands"), //new commands to be added to gamecommand queue
    SendCommandsToCommander("Send-Commands-To-Commander"),
    UnitLastCommand("Unit-Last-Command"),

    //requests that UnitM can make to others
    MineralList("Mineral-List"),
    GasList("Gas-List"),

    //data that UnitM sends to others
    SendWorkerToMine("Send-Worker-To-Mine"),
    SendWorker("Send-Worker");
    
    String conversation_id;

    UnitM(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }

}//end CommID


