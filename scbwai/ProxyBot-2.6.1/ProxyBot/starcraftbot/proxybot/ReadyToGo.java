
package starcraftbot.proxybot;

import java.io.*;

/**
 * This class used for communication between ProxyBot.java and ProxyBotAgent.java in order to
 * let ProxyBot.java that the ProxyBotAgent has been created.
 *
 */
public class ReadyToGo implements Serializable {
    private boolean ready = false; //mutex used 
    
    /**
     * This method will make the caller wait until signal() is called.
     * \throws InterruptedException
     */
    public synchronized void waitOn() throws InterruptedException{ 
      while(!ready) {
        wait();
      }
    }
    
    /**
     * This method will issue a signal to any method that called waitOn().
     */
    public synchronized void signal() {
      ready = true;
      notifyAll();
    }

} //end ReadyToGo


