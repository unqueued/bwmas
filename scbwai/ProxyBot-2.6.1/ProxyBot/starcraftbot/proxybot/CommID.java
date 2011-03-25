
package starcraftbot.proxybot;

import jade.core.*;

public class CommID{
   
   public static String genID(Agent a) { 
      return a.getLocalName() + a.hashCode() + System.currentTimeMillis()%10000 + "_" + System.currentTimeMillis()%100; 
   }
}

