package me.dicorndl.rabbitmqtutorials.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class StopWatchReceiver {

  private static final Logger LOG = LoggerFactory.getLogger(StopWatchReceiver.class);

  public void receive(String in, int receiver) throws InterruptedException {
    StopWatch watch = new StopWatch();
    watch.start();
    LOG.info("instance " + receiver + " [x] Received '" + in + "'");
    doWork(in);
    watch.stop();
    LOG.info("instance " + receiver + " [x] Done in " + watch.getTotalTimeSeconds() + "s");
  }

  private void doWork(String in) throws InterruptedException {
    for (char ch : in.toCharArray()) {
      if (ch == '.') {
        Thread.sleep(1000);
      }
    }
  }
}
