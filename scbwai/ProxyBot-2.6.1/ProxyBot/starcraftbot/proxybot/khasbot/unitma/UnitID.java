
package starcraftbot.proxybot.khasbot.unitma;

public class UnitID {

 
  /* Terran Units */
  public enum TerranID {
    Terran_Marine(0),
    Terran_Ghost(1),
    Terran_Vulture(2),
    Terran_Goliath(3),
    Terran_Siege_Tank_Tank_Mode(5),
    Terran_SCV(7),
    Terran_Wraith(8),
    Terran_Science_Vessel(9),
    Terran_Dropship(11),
    Terran_Battlecruiser(12),
    Terran_Vulture_Spider_Mine(13),
    Terran_Nuclear_Missile(14),
    Terran_Siege_Tank_Siege_Mode(30),
    Terran_Firebat(32),
    Spell_Scanner_Sweep(33),
    Terran_Medic(34),
    Terran_Valkyrie(58);
  
    int unitId;

    TerranID(int num){
      unitId = num;
    }

    public int getID(){
      return unitId;
    }

  }
	
  /* Zerg Units */
  public enum ZergID {
  Zerg_Larva(35),
	Zerg_Egg(36),
	Zerg_Zergling(37),
	Zerg_Hydralisk(38),
	Zerg_Ultralisk(39),
	Zerg_Broodling(40),
	Zerg_Drone(41),
	Zerg_Overlord(42),
	Zerg_Mutalisk(43),
	Zerg_Guardian(44),
	Zerg_Queen(45),
	Zerg_Defiler(46),
	Zerg_Scourge(47),
	Zerg_Infested_Terran(50),
	Zerg_Cocoon(59),
	Zerg_Devourer(62);
	 
    int unitId;

    ZergID(int num){
      unitId = num;
    }

    public int getID(){
      return unitId;
    }

  }

  /* Protoss Units */
  public enum ProtossID {
  Protoss_Corsair(60),
	Protoss_Dark_Templar(61),
	Protoss_Dark_Archon(63),
	Protoss_Probe(64),
	Protoss_Zealot(65),
	Protoss_Dragoon(66),
	Protoss_High_Templar(67),
	Protoss_Archon(68),
	Protoss_Shuttle(69),
	Protoss_Scout(70),
	Protoss_Arbiter(71),
	Protoss_Carrier(72),
	Protoss_Interceptor(73),
	Protoss_Reaver(83),
	Protoss_Observer(84),
	Protoss_Scarab(85);
 
    int unitId;

    ProtossID(int num){
      unitId = num;
    }

    public int getID(){
      return unitId;
    }

  }

   

}

