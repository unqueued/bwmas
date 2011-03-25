
package starcraftbot.proxybot.khasbot;

import jade.lang.acl.*;


/**
 * For this class the suffix Agent is dropped from all of the method names to 
 * save typing.
 *
 */
public class ParseACLMessage {

  public static boolean isSenderProxyBot(ACLMessage msg) {
    //String sender = msg.getSender().getLocalName();
    //return sender.matches(".*[Pp]roxy[Bb]ot.*");
    return msg.getSender().getLocalName().matches(".*[Pp]roxy[Bb]ot.*");
  }
  
  public static String getProxyBotName(ACLMessage msg) {
    String sender = null;
    if( isSenderProxyBot(msg) )
      sender = msg.getSender().getLocalName();
    return sender; 
  }
  public static boolean isSenderCommander(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Cc]ommander.*");
  }

  public static boolean isSenderUnitManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Uu]nit[Mm]anager.*");
  }

  public static boolean  isSenderBuildingManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Bb]uilding[Mm]anager.*");
  }

  public static boolean isSenderStructureManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Ss]tructure[Mm]anager.*");
  }

  public static boolean isSenderBattleManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Bb]attle[Mm]anager.*");
  }

  public static boolean isSenderResourceManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Rr]esource[Mm]anager.*");
  }

  public static boolean isSenderMapManager(ACLMessage msg) {
    return msg.getSender().getLocalName().matches(".*[Mm]ap[Mm]anager.*");
  }


}//end ParseACLMessage

