package me.dicorndl.rabbitmqtutorials.tut2;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Tut2Sender {

  private static final Logger LOG = LoggerFactory.getLogger(Tut2Sender.class);

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private Queue queue;

  AtomicInteger dots = new AtomicInteger(0);

  AtomicInteger count = new AtomicInteger(0);

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void send() {
    StringBuilder builder = new StringBuilder("Hello");
    if (dots.getAndIncrement() == 3) {
      dots.set(1);
    }
    for (int i = 0; i < dots.get(); i++) {
      builder.append('.');
    }
    builder.append(count.incrementAndGet());
    String message = builder.toString();
    template.convertAndSend(queue.getName(), message);
    LOG.info(" [x] Sent '" + message + "'");
  }
}
