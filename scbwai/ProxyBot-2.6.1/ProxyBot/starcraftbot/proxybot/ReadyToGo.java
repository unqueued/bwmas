
package starcraftbot.proxybot;

//Class used to wait for the Agent to be created so communication can start
public class ReadyToGo {
    private boolean ready = false;

    synchronized void waitOn() throws InterruptedException{ 
      while(!ready) {
        wait();
      }
    }
    
    synchronized void signal() {
      ready = true;
      notifyAll();
    }
} //end ReadyToGo


