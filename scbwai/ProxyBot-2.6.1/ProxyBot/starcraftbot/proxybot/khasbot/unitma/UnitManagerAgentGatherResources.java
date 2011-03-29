package starcraftbot.proxybot.khasbot.unitma;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.*;

import starcraftbot.proxybot.khasbot.ParseACLMessage;

@SuppressWarnings("serial")
public class UnitManagerAgentGatherResources extends Behaviour{
	Agent agent=null;	

  public UnitManagerAgentGatherResources(Agent a) {
    super(a);
    agent=a;
  }
  
  public void action() {
  }


  public boolean done() {
    return true;
  }

}
