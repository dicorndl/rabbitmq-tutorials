package me.dicorndl.rabbitmqtutorials.tut1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "hello")
public class Tut1Receiver {

  private static final Logger LOG = LoggerFactory.getLogger(Tut1Receiver.class);

  @RabbitHandler
  public void receive(String in) {
    LOG.info(" [x] Received '" + in + "'");
  }
}
