package me.dicorndl.rabbitmqtutorials.tut1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Tut1Sender {

  private static final Logger LOG = LoggerFactory.getLogger(Tut1Sender.class);

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private Queue queue;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void send() {
    String message = "Hello World!";
    this.template.convertAndSend(queue.getName(), message);
    LOG.info(" [x] Sent '" + message + "'");
  }
}
