package me.dicorndl.rabbitmqtutorials.test;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class Task1 {

  private static final Logger LOG = LoggerFactory.getLogger(Task1.class);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private DirectExchange direct;

  @Autowired
  private MessageConverter messageConverter;

  @RabbitListener(queues = "#{task1Queue.name}")
    public void receive(Message message) {
      LOG.info(" [x] Received message : {}", message.toString());

    Object o = messageConverter.fromMessage(message);
    if (o instanceof Map) {
      Map<String, List<String>> payload = (Map<String, List<String>>) o;
      List<String> seq = payload.get("seq");
      LOG.info("{}", seq);

      if (seq.size() > 1) {
        seq.remove(0);
        String routeKey = seq.get(0);

        message = messageConverter.toMessage(payload, new MessageProperties());
        rabbitTemplate.send(direct.getName(), routeKey, message);
      }
    }
  }
}
