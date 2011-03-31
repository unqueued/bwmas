
package starcraftbot.proxybot.khasbot.structurema;

public enum TechLevel {
  Terran_Infantry_Armor("Terran_Infantry_Armor"),
  Terran_Vehicle_Plating("Terran_Vehicle_Plating"),
  Terran_Ship_Plating("Terran_Ship_Plating"),
  Zerg_Carapace("Zerg_Carapace"),
  Zerg_Flyer_Carapace("Zerg_Flyer_Carapace"),
  Protoss_Ground_Armor("Protoss_Ground_Armor"),
  Protoss_Air_Armor("Protoss_Air_Armor"),
  Terran_Infantry_Weapons("Terran_Infantry_Weapons"),
  Terran_Vehicle_Weapons("Terran_Vehicle_Weapons"),
  Terran_Ship_Weapons("Terran_Ship_Weapons"),
  Zerg_Melee_Attacks("Zerg_Melee_Attacks"),
  Zerg_Missile_Attacks("Zerg_Missile_Attacks"),
  Zerg_Flyer_Attacks("Zerg_Flyer_Attacks"),
  Protoss_Ground_Weapons("Protoss_Ground_Weapons"),
  Protoss_Air_Weapons("Protoss_Air_Weapons"),
  Protoss_Plasma_Shields("Protoss_Plasma_Shields"),
  U_238_Shells("U_238_Shells"),
  Ion_Thrusters("Ion_Thrusters"),
  Titan_Reactor("Titan_Reactor"),
  Ocular_Implants("Ocular_Implants"),
  Moebius_Reactor("Moebius_Reactor"),
  Apollo_Reactor("Apollo_Reactor"),
  Colossus_Reactor("Colossus_Reactor"),
  Ventral_Sacs("Ventral_Sacs"),
  Antennae("Antennae"),
  Pneumatized_Carapace("Pneumatized_Carapace"),
  Metabolic_Boost("Metabolic_Boost"),
  Adrenal_Glands("Adrenal_Glands"),
  Muscular_Augments("Muscular_Augments"),
  Grooved_Spines("Grooved_Spines"),
  Gamete_Meiosis("Gamete_Meiosis"),
  Metasynaptic_Node("Metasynaptic_Node"),
  Singularity_Charge("Singularity_Charge"),
  Leg_Enhancements("Leg_Enhancements"),
  Scarab_Damage("Scarab_Damage"),
  Reaver_Capacity("Reaver_Capacity"),
  Gravitic_Drive("Gravitic_Drive"),
  Sensor_Array("Sensor_Array"),
  Gravitic_Boosters("Gravitic_Boosters"),
  Khaydarin_Amulet("Khaydarin_Amulet"),
  Apial_Sensors("Apial_Sensors"),
  Gravitic_Thrusters("Gravitic_Thrusters"),
  Carrier_Capacity("Carrier_Capacity"),
  Khaydarin_Core("Khaydarin_Core"),
  Argus_Jewel("Argus_Jewel"),
  Argus_Talisman("Argus_Talisman"),
  Caduceus_Reactor("Caduceus_Reactor"),
  Chitinous_Plating("Chitinous_Plating"),
  Anabolic_Synthesis("Anabolic_Synthesis"),
  Charon_Boosters("Charon_Boosters"),
  None("None"),
  Unknown("Unknown");

  private String tech_level_name;

  TechLevel(String name){
    tech_level_name = name; 
  }


}
