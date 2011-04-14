
package starcraftbot.proxybot.khasbot.structurema;

public class Tech {
  public enum Researched {
    /* Values for these were obtained from the bwapi project */
    Stim_Packs(0),
    Lockdown(1),
    EMP_Shockwave(2),
    Spider_Mines(3),
    Scanner_Sweep(4),
    Tank_Siege_Mode(5),
    Defensive_Matrix(6),
    Irradiate(7),
    Yamato_Gun(8),
    Cloaking_Field(9),
    Personnel_Cloaking(10),
    Burrowing(11),
    Infestation(12),
    Spawn_Broodlings(13),
    Dark_Swarm(14),
    Plague(15),
    Consume(16),
    Ensnare(17),
    Parasite(18),
    Psionic_Storm(19),
    Hallucination(20),
    Recall(21),
    Stasis_Field(22),
    Archon_Warp(23),
    Restoration(24),
    Disruption_Web(25),
    Mind_Control(27),
    Dark_Archon_Meld(28),
    Feedback(29),
    Optical_Flare(30),
    Maelstrom(31),
    Lurker_Aspect(32),
    Healing(34),
    None(44),
    Unknown(45),
    Nuclear_Strike(46);


    /*
     * The research values are as follows:
     * 4 - tech has been researched
     * 1 - tech is currently being researched
     * other - not defined
     */
    private int tech_researched;

    Researched(int value){
      tech_researched = value;
    }

    public int getNumValue(){
      return tech_researched;
    }
  }

  public enum Level {
    /* Values for these were obtained from the bwapi project */
    Terran_Infantry_Armor(0),
    Terran_Vehicle_Plating(1),
    Terran_Ship_Plating(2),
    Zerg_Carapace(3),
    Zerg_Flyer_Carapace(4),
    Protoss_Ground_Armor(5),
    Protoss_Air_Armor(6),
    Terran_Infantry_Weapons(7),
    Terran_Vehicle_Weapons(8),
    Terran_Ship_Weapons(9),
    Zerg_Melee_Attacks(10),
    Zerg_Missile_Attacks(11),
    Zerg_Flyer_Attacks(12),
    Protoss_Ground_Weapons(13),
    Protoss_Air_Weapons(14),
    Protoss_Plasma_Shields(15),
    U_238_Shells(16),
    Ion_Thrusters(17),
    Titan_Reactor(19),
    Ocular_Implants(20),
    Moebius_Reactor(21),
    Apollo_Reactor(22),
    Colossus_Reactor(23),
    Ventral_Sacs(24),
    Antennae(25),
    Pneumatized_Carapace(26),
    Metabolic_Boost(27),
    Adrenal_Glands(28),
    Muscular_Augments(29),
    Grooved_Spines(30),
    Gamete_Meiosis(31),
    Metasynaptic_Node(32),
    Singularity_Charge(33),
    Leg_Enhancements(34),
    Scarab_Damage(35),
    Reaver_Capacity(36),
    Gravitic_Drive(37),
    Sensor_Array(38),
    Gravitic_Boosters(39),
    Khaydarin_Amulet(40),
    Apial_Sensors(41),
    Gravitic_Thrusters(42),
    Carrier_Capacity(43),
    Khaydarin_Core(44),
    Argus_Jewel(47),
    Argus_Talisman(49),
    Caduceus_Reactor(51),
    Chitinous_Plating(52),
    Anabolic_Synthesis(53),
    Charon_Boosters(54),
    None(61),
    Unknown(62);

    /*
     * The tech levels are as follows:
     * 0 - initial tech level
     * 1 - level 1 tech for this ability/tech
     * 2 - level 2 tech for this ability/tech
     * 3 - level 3 tech for this ability/tech (MAX level)
     * 4 - tech upgrade in progress
     */
    private int tech_level;

    Level(int value){
      tech_level = value;
    }

    public int getNumValue(){
      return tech_level;
    }
  }
}
