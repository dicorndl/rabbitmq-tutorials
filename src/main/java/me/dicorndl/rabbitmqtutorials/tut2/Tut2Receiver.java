package me.dicorndl.rabbitmqtutorials.tut2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StopWatch;

@RabbitListener(queues = "tut.hello")
public class Tut2Receiver {

  private static final Logger LOG = LoggerFactory.getLogger(Tut2Receiver.class);

  private final int instance;

  public Tut2Receiver(int i) {
    this.instance = i;
  }

  @RabbitHandler
  public void receive(String in) throws InterruptedException {
    StopWatch watch = new StopWatch();
    watch.start();
    LOG.info("instance " + this.instance + " [x] Received '" + in + "'");
    doWork(in);
    watch.stop();
    LOG.info("instance " + this.instance + " [x] Done in " + watch.getTotalTimeSeconds() + "s");
  }

  private void doWork(String in) throws InterruptedException {
    for (char ch : in.toCharArray()) {
      if (ch == '.') {
        Thread.sleep(1000);
      }
    }
  }
}
