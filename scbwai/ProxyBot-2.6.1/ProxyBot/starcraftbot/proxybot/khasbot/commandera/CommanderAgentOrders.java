
package starcraftbot.proxybot.khasbot.commandera;

import jade.core.*;
import jade.core.behaviours.*;

public class CommanderAgentOrders {
	Agent agent=null;	
	ParallelBehaviour pb = null;	

  public CommanderAgentOrders(Agent a, ParallelBehaviour root_behaviour) {
    agent=a;
    pb = root_behaviour;
  }

}

