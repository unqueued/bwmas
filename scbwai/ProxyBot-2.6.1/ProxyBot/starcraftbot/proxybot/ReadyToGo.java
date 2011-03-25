
package starcraftbot.proxybot;

import java.io.*;

//Class used to wait for the Agent to be created so communication can start
public class ReadyToGo implements Serializable {
    private boolean ready = false;

    public synchronized void waitOn() throws InterruptedException{ 
      while(!ready) {
        wait();
      }
    }
    
    public synchronized void signal() {
      ready = true;
      notifyAll();
    }
} //end ReadyToGo


