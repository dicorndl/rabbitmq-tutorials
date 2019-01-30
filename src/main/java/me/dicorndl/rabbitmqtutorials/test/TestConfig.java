package me.dicorndl.rabbitmqtutorials.test;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"test"})
@Configuration
public class TestConfig {

  private static final String EXCHANGE_KEY = "test.direct";

  @Bean
  public MessageConverter messageConverter() {
    return new SimpleMessageConverter();
  }

  @Bean
  public DirectExchange direct() {
    return new DirectExchange(EXCHANGE_KEY);
  }

  @Profile("sender")
  @Bean
  public DummyExternalSystem sender() {
    return new DummyExternalSystem();
  }

  @Profile("receiver")
  private static class ReceiverConfig {

    @Bean
    public Queue task1Queue() {
      return new AnonymousQueue();
    }

    @Bean
    public Queue task2Queue() {
      return new AnonymousQueue();
    }

    @Bean
    public Queue task3Queue() {
      return new AnonymousQueue();
    }

    @Bean
    public Binding task1Binding(DirectExchange direct, Queue task1Queue) {
      return BindingBuilder.bind(task1Queue).to(direct).with(RouteKey.TASK1);
    }

    @Bean
    public Binding task2Binding(DirectExchange direct, Queue task2Queue) {
      return BindingBuilder.bind(task2Queue).to(direct).with(RouteKey.TASK2);
    }

    @Bean
    public Binding task3Binding(DirectExchange direct, Queue task3Queue) {
      return BindingBuilder.bind(task3Queue).to(direct).with(RouteKey.TASK3);
    }

    @Bean
    public Task1 task1Receiver() {
      return new Task1();
    }

    @Bean
    public Task2 task2Receiver() {
      return new Task2();
    }

    @Bean
    public Task3 task3Receiver() {
      return new Task3();
    }
  }
}
