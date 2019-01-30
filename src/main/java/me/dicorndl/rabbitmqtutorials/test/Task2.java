package me.dicorndl.rabbitmqtutorials.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class Task2 {

  private static final Logger LOG = LoggerFactory.getLogger(Task2.class);

  @RabbitListener(queues = "#{task2Queue.name}")
  public void receive(Message message) {
    LOG.info(" [x] Received message : {}", message.toString());
  }
}
