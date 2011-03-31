
package starcraftbot.proxybot.khasbot.structurema;

public enum TechResearched {
  Stim_Packs("Stim_Packs"),
  Lockdown("Lockdown"),
  EMP_Shockwave("EMP_Shockwave"),
  Spider_Mines("Spider_Mines"),
  Scanner_Sweep("Scanner_Sweep"),
  Tank_Siege_Mode("Tank_Siege_Mode"),
  Defensive_Matrix("Defensive_Matrix"),
  Irradiate("Irradiate"),
  Yamato_Gun("Yamato_Gun"),
  Cloaking_Field("Cloaking_Field"),
  Personnel_Cloaking("Personnel_Cloaking"),
  Burrowing("Burrowing"),
  Infestation("Infestation"),
  Spawn_Broodlings("Spawn_Broodlings"),
  Dark_Swarm("Dark_Swarm"),
  Plague("Plague"),
  Consume("Consume"),
  Ensnare("Ensnare"),
  Parasite("Parasite"),
  Psionic_Storm("Psionic_Storm"),
  Hallucination("Hallucination"),
  Recall("Recall"),
  Stasis_Field("Stasis_Field"),
  Archon_Warp("Archon_Warp"),
  Restoration("Restoration"),
  Disruption_Web("Disruption_Web"),
  Mind_Control("Mind_Control"),
  Dark_Archon_Meld("Dark_Archon_Meld"),
  Feedback("Feedback"),
  Optical_Flare("Optical_Flare"),
  Maelstrom("Maelstrom"),
  Lurker_Aspect("Lurker_Aspect"),
  Healing("Healing"),
  None("None"),
  Unknown("Unknown"),
  Nuclear_Strike("Nuclear_Strike");


  private String tech_researched_name;

  TechResearched(String name){
    tech_researched_name = name; 
  }

}

