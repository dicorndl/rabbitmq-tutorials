package me.dicorndl.rabbitmqtutorials.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class DummyExternalSystem {

  private static final Logger LOG = LoggerFactory.getLogger(DummyExternalSystem.class);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private DirectExchange direct;

  @Autowired
  private MessageConverter messageConverter;

  @Scheduled(fixedDelay = 5000, initialDelay = 500)
  public void send() {
    List<String> seq = generateSequence();
    String routeKey = seq.get(0);

    Map<String, List<String>> payload = Collections.singletonMap("seq", seq);

    Message message = messageConverter.toMessage(payload, new MessageProperties());

    rabbitTemplate.send(direct.getName(), routeKey, message);
    LOG.info(" [x] Send message : {}", message.toString());
  }

  private List<String> generateSequence() {
    List<String> seqTargets = Arrays.stream(RouteKey.values()).map(Enum::name).collect(Collectors.toList());
    Collections.shuffle(seqTargets);
    return seqTargets;
  }
}
