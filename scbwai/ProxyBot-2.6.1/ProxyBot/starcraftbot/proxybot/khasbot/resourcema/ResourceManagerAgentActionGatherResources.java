/**
 * 
 */
package starcraftbot.proxybot.khasbot.resourcema;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.lang.acl.*;
import jade.proto.*;
import jade.tools.introspector.gui.MyDialog;

import starcraftbot.proxybot.CommandId;
import starcraftbot.proxybot.ConverId;
import starcraftbot.proxybot.command.GameCommand;
import starcraftbot.proxybot.command.GameCommandQueue;
import starcraftbot.proxybot.khasbot.unitma.UnitObject;
import starcraftbot.proxybot.khasbot.unitma.Units;

/**
 * This class should be instantiated everytime a FIPA-Request is made. 
 * I'm hoping once the FIPA-Request is done the instance will get picked
 * up by the garbage collector until the next FIPA-Request is made.
 */
@SuppressWarnings("serial")
public class ResourceManagerAgentActionGatherResources extends CyclicBehaviour {
	ResourceManagerAgent agent=null;
	DataStore ds = null;
	
  public ResourceManagerAgentActionGatherResources(ResourceManagerAgent a) {
    super(a);
    this.agent=a;
    ds = this.agent.getDS();
    //ds.put(agent.getLocalName()+"agent", agent);
 }

  @Override
  public void action() {
    //System.out.println("ResourceManagerAgentActionGatherResources: " + agent.tbf.getThread(this).getId());
    /* request more workers to gather minerals*/
	this.agent = (ResourceManagerAgent)this.agent.getDS().get(this.agent.getLocalName()+"agent");
    //System.out.println("Num of workers: " + agent.numOfWorkers());
    if((this.agent.getGameObject() != null))
    {
      Units gameUnits = this.agent.getGameObject().getUnitsInGame();
      
      if(gameUnits != null)
      {	  
	      if(this.agent.numOfWorkers() < 5)
	      {
	    	  //System.out.println(agent.getLocalName()+":  Has less than 5 workers, requesting another.");
	    	  this.agent.requestWorker();
	      }
	      
	      for(UnitObject u : this.agent.my_units.values())
	      {
	    	  if(!u.isMining())
	    	  {
		    	  int patchID = -1;
				  double cdist = Double.MAX_VALUE;
		    	  for(UnitObject m : this.agent.minerals)
		    	  {
		    		  double dx = u.getX() - m.getX();
						double dy = u.getY() - m.getY();
						double dist = Math.sqrt(dx * dx + dy * dy);
		
						if (dist < cdist) {
							//System.out.println("found closer mineral patch to probe!");
							System.out.flush();
							patchID = m.getID();
							cdist = dist;
						}
		    	  }
		    	  if(patchID > 0)
				  {
		    		  
		    		this.agent.commandsToDo.push(new GameCommand(CommandId.StarCraftCommand.rightClickUnit, u.getID(), patchID, 0, 0 ));
		    		  
				    //agent.getGameObject().   getCommandsToDo().add(new Command(Command.StarCraftCommand.rightClickUnit, u.getID(), patchID, 0, 0));
					System.out.println("Command( rightClickUnit, unitID:"+u.getID()+", targetID:"+patchID+", 0, 0) added to ResourceManagerAgent CommandsToDo List");
					  //System.out.flush();
				  }
	    	  }
	      }
	      if(!this.agent.commandsToDo.empty())
	      {
	    	  this.agent.requestCommandsExe();
	      }
	      this.agent.getDS().put(this.agent.getLocalName()+"agent", this.agent);
      }
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
