
package starcraftbot.proxybot;

import jade.core.*;
/**
 * This class is used to generate a conversationId that can be used between two agents.
 *
 * This helps the two agents carry on multiple converstations, while still being able to 
 * distinguish which is which.
 */
public class CommID{
   
  /**
   * This method will generate the conversationId.
   * \arg \c Agent the name of the agent is passed in, in order to generate a unique id
   * \return \c String the generated converationId
   */
  public static String genID(Agent a) { 
    return a.getLocalName() + a.hashCode() + System.currentTimeMillis()%10000 + "_" + System.currentTimeMillis()%100; 
  }
}//end CommID


