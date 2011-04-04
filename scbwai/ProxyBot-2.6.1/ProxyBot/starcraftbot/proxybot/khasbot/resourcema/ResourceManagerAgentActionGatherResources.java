/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.ConverId;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentActionGatherResources extends TickerBehaviour {
	ResourceManagerAgent agent=null;
  long timeout=0;
  

  public ResourceManagerAgentActionGatherResources(ResourceManagerAgent a, long tick_time) {
    super(a,tick_time);
    agent=a;
    timeout = tick_time;
    
    /* make the initial request to gather minerals */
    agent.gatherMinerals();


  }

  @Override
  protected void onTick() {

  }

}
