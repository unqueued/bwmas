
package starcraftbot.proxybot.khasbot.unitma;

import java.io.*;
import java.util.*;


public enum Unit implements Serializable {
  /* Terran Units */
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
  Terran_Valkyrie(58),

  /* Zerg Units */
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
  Zerg_Devourer(62),

  /* Protoss Units */
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
  Protoss_Scarab(85),

  /* Neutral Structures */
  Critter_Rhynadon(89),
  Critter_Bengalaas(90),
  Critter_Scantid(93),
  Critter_Kakaru(94),
  Critter_Ragnasaur(95),
  Critter_Ursadon(96),
  Special_Crashed_Norad_II(126),
  Special_Ion_Cannon(127),
  Special_Overmind_With_Shell(147),
  Special_Overmind(148),
  Special_Mature_Chrysalis(150),
  Special_Cerebrate(151),
  Special_Cerebrate_Daggoth(152),
  Special_Stasis_Cell_Prison(168),
  Special_Khaydarin_Crystal_Form(173),
  Special_Protoss_Temple(174),
  Special_XelNaga_Temple(175),
  Resource_Mineral_Field(176),
  Resource_Vespene_Geyser(188),
  Special_Warp_Gate(189),
  Special_Psi_Disrupter(190),
  Special_Power_Generator(200),
  Special_Overmind_Cocoon(201),
  Spell_Dark_Swarm(202),
  None(228),
  Unknown(229),

  /* Terran Structures */
  Terran_Command_Center(106),
  Terran_Comsat_Station(107),
  Terran_Nuclear_Silo(108),
  Terran_Supply_Depot(109),
  Terran_Refinery(110),
  Terran_Barracks(111),
  Terran_Academy(112),
  Terran_Factory(113),
  Terran_Starport(114),
  Terran_Control_Tower(115),
  Terran_Science_Facility(116),
  Terran_Covert_Ops(117),
  Terran_Physics_Lab(118),
  Terran_Machine_Shop(120),
  Terran_Engineering_Bay(122),
  Terran_Armory(123),
  Terran_Missile_Turret(124),
  Terran_Bunker(125),


  /* Zerg Structures */
  Zerg_Infested_Command_Center(130),
  Zerg_Hatchery(131),
  Zerg_Lair(132),
  Zerg_Hive(133),
  Zerg_Nydus_Canal(134),
  Zerg_Hydralisk_Den(135),
  Zerg_Defiler_Mound(136),
  Zerg_Greater_Spire(137),
  Zerg_Queen_s_Nest(138),
  Zerg_Evolution_Chamber(139),
  Zerg_Ultralisk_Cavern(140),
  Zerg_Spire(141),
  Zerg_Spawning_Pool(142),
  Zerg_Creep_Colony(143),
  Zerg_Spore_Colony(144),
  Zerg_Sunken_Colony(146),
  Zerg_Extractor(149),

  /* Protoss Structures */
  Protoss_Nexus(154),
  Protoss_Robotics_Facility(155),
  Protoss_Pylon(156),
  Protoss_Assimilator(157),
  Protoss_Observatory(159),
  Protoss_Gateway(160),
  Protoss_Photon_Cannon(162),
  Protoss_Citadel_of_Adun(163),
  Protoss_Cybernetics_Core(164),
  Protoss_Templar_Archives(165),
  Protoss_Forge(166),
  Protoss_Stargate(167),
  Protoss_Fleet_Beacon(169),
  Protoss_Arbiter_Tribunal(170),
  Protoss_Robotics_Support_Bay(171),
  Protoss_Shield_Battery(172);

  int unitId;

  private Unit(int num){
    unitId = num;
  }

 public int getNumValue(){
    return unitId;
  }

  private static final HashMap<Integer,Unit> lookup = new HashMap<Integer,Unit>();

  static {
    for( Unit p : EnumSet.allOf(Unit.class))
      lookup.put(p.getNumValue(),p);
  }

  public static Unit getUnit(int id){
    return lookup.get(id);
  }
  
}

