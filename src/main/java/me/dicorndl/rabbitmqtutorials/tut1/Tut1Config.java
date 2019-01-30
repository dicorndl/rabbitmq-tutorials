package me.dicorndl.rabbitmqtutorials.tut1;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"tut1", "hello-world"})
@Configuration
@EnableRabbit
public class Tut1Config {

  @Bean
  public Queue hello() {
    return new Queue("hello");
  }

  @Profile("receiver")
  @Bean
  public Tut1Receiver receiver() {
    return new Tut1Receiver();
  }

  @Profile("sender")
  @Bean
  public Tut1Sender sender() {
    return new Tut1Sender();
  }

  @Bean
  public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setConcurrentConsumers(5);

    return factory;
  }
}
