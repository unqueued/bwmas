/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package starcraftbot.proxybot.khasbot.unitma;

/**
 *
 * @author Antonio Arredondo
 */
public class UnitObjectTasks {
  public enum Worker {
    GatherMinerals("Gather-Minerals");

    String conversation_id;

    Worker(String in){
      conversation_id = in;// + " " + this.getClass().getName() + this.hashCode() + System.currentTimeMillis()%10000;
    }
    public String getConId(){
      return conversation_id;
    }
  }
}
