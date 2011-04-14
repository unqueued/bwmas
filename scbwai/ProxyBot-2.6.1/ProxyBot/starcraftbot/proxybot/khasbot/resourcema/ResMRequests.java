
package starcraftbot.proxybot.khasbot.resourcema;

/* conversation id's that are focused around the map manager */
public enum ResMRequests{
  RequestWorker("Request-Worker"), 
  doCommands("Do-Commands");

  String conversation_id;

  ResMRequests(String in){
    conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
  }
  public String getConId(){
    return conversation_id;
  }
}
