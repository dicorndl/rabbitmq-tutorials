package me.dicorndl.rabbitmqtutorials.test;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Profile({"test"})
@Configuration
public class TestConfig {

  private static final String EXCHANGE_KEY = "test.direct";

  @Bean
  public ConnectionFactory connectionFactory() {
    // RabbitMQ broker 의 커넥션을 관리하기 위한 중심 컴포넌트는 ConnectionFactory 인터페이스이다.
    // Spring AMQP 에서 제공하는 concrete 구현체는 CachingConnectionFactory 로,
    // 애플리케이션이 공유할 수 있는 단일 연결 프록시를 설정한다.
    // AMQP 로 메시지를 보내는 작업 단위는 실제론 channel 이기 때문에 연결에 대한 공유가 가능하다.
    // connection 객체는 createChannel 메소드를 지원한다.
    // CachingConnectionFactory 의 구현은 이러한 channel 의 캐싱을 지원하며,
    // channel 의 transactional 여부에 따라 channel 에 대해 구분된 캐시를 관리함.
    // channel 의 캐시 크기(기본값 25)를 설정하려면 setChannelCacheSize() 메소드를 사용하면 됨.
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");

    // 캐시 크기는 제한이 아니라 캐시할 수 있는 채널의 수를 의미한다.
    // 예를 들어 캐시 크기가 10개여도 어떤 갯수의 채널도 실제론 사용될 수 있다.
    connectionFactory.setChannelCacheSize(25);

    return connectionFactory;
  }

  @Bean
  public AmqpTemplate rabbitTemplate() {
    // AmqpTemplate : 메시지의 송수신 동작을 다루는 인터페이스
    RabbitTemplate template = new RabbitTemplate(connectionFactory());
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(500);
    backOffPolicy.setMultiplier(10.0);
    backOffPolicy.setMaxInterval(10000);

    retryTemplate.setBackOffPolicy(backOffPolicy);

    template.setRetryTemplate(retryTemplate);

    return template;
  }

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
