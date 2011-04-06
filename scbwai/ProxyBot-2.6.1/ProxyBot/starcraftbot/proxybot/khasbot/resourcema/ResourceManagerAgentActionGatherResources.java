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
public class ResourceManagerAgentActionGatherResources extends CyclicBehaviour {
	ResourceManagerAgent agent=null;
  
  public ResourceManagerAgentActionGatherResources(ResourceManagerAgent a) {
    super(a);
    agent=a;
 }

  @Override
  public void action() {
    //System.out.println("ResourceManagerAgentActionGatherResources: " + agent.tbf.getThread(this).getId());
    /* request more workers to gather minerals*/
    //System.out.println("Num of workers: " + agent.numOfWorkers());
    if((agent.getGameObject() != null)){
      agent.requestWorker();
    }
      //getDataStore().put("hello", "there it is");
    //if((agent.getGameObject() != null) && (agent.numOfWorkers() <= 4) )
       //agent.requestWorker();
//    else
//    {
      /*if(agent.getGameObject() == null)
        System.out.println(agent.getLocalName() + ":: waiting for game object to NOT be null");
      else
        System.out.println(agent.getLocalName() + ":: at 5 workers");*/
//    }
  }

//  @Override
//  public boolean done() {
//    if( agent.numOfWorkers() > 0 ){
//      /* request more workers to gather minerals*/
//      return false;
//    }else{
//      return true;
//    }
//  }


}
