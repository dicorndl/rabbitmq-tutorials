package me.dicorndl.rabbitmqtutorials.tut2;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import me.dicorndl.rabbitmqtutorials.common.MessageCreator;

public class Tut2Sender {

  private static final Logger LOG = LoggerFactory.getLogger(Tut2Sender.class);

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private Queue queue;

  private AtomicInteger dots = new AtomicInteger(0);

  private AtomicInteger count = new AtomicInteger(0);

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void send() {
    String message = MessageCreator.helloDotMessage(dots, count);
    template.convertAndSend(queue.getName(), message);
    LOG.info(" [x] Sent '" + message + "'");
  }
}
