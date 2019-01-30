package me.dicorndl.rabbitmqtutorials.tut6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Tut6Client {

  private static final Logger LOG = LoggerFactory.getLogger(Tut6Client.class);

  @Autowired
  private RabbitTemplate template;

  @Autowired
  private DirectExchange exchange;

  int start = 0;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void send() {
    LOG.info(" [x] Requesting fib(" + start + ")");
    Integer response = (Integer) template.convertSendAndReceive(exchange.getName(), "rpc", start++);
    LOG.info(" [.] Got '" + response + "'");
  }
}
