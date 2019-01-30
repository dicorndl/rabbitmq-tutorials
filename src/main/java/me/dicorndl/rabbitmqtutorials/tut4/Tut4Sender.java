package me.dicorndl.rabbitmqtutorials.tut4;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import me.dicorndl.rabbitmqtutorials.common.MessageCreator;

public class Tut4Sender {

  private static final Logger LOG = LoggerFactory.getLogger(Tut4Sender.class);

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private DirectExchange direct;

  private AtomicInteger index = new AtomicInteger(0);

  private AtomicInteger count = new AtomicInteger(0);

  private final String[] keys = {"orange", "black", "green"};

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void send() {
    if (this.index.incrementAndGet() == 3) {
      this.index.set(0);
    }

    String key = keys[this.index.get()];
    String message = MessageCreator.helloToKeyMessage(key, count);
    template.convertAndSend(direct.getName(), key, message);
    LOG.info(" [x] Sent '" + message + "'");
  }
}
