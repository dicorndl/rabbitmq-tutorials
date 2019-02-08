package me.dicorndl.rabbitmqtutorials.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.client.ShutdownSignalException;

@Profile({"test"})
@Configuration
@EnableRabbit
public class TestConfig {

  private static final Logger log = LoggerFactory.getLogger(TestConfig.class);
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

    connectionFactory.addConnectionListener(new ConnectionListener() {
      @Override
      public void onCreate(Connection connection) {
        log.info(">> Connection created...");
      }

      @Override
      public void onShutDown(ShutdownSignalException signal) {
        // 존재하지 않는 exchange 로 메시지를 publish 하는 경우 channel 이 예외와 함께 닫히게 됨.
        // 위 상황시 본 이벤트 리스너를 통해 로깅할 수 있음.
        log.info(">> Connection ShutDown... Cause by {}", signal.getReason());
      }
    });

    // RabbitTemplate 의 Publisher 확인 및 반환 기능을 사용하기 위한 필요 설정.
    connectionFactory.setPublisherReturns(true);

    // Publisher 확인 기능을 사용하기 위한 필요 설정.
    connectionFactory.setPublisherConfirms(true);

    return connectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
    // AmqpTemplate : 메시지의 송수신 기본 동작을 정의한 인터페이스
    // RabbitTemplate : 유일하게 제공되는 AmqpTemplate 의 구현체
    RabbitTemplate template = new RabbitTemplate(connectionFactory());

    // 브로커 연결 관련 문제를 처리하기 위한 RetryTemplate (버전 1.3 부터 추가됨)
    // 아래는 지수 back-off 및 SimpleRetryPolicy 사용에 대한 예임.
    // 3번 시도 후 예외를 던짐.
    RetryTemplate retryTemplate = new RetryTemplate();
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(500);
    backOffPolicy.setMultiplier(10.0);
    backOffPolicy.setMaxInterval(10000);

    retryTemplate.setBackOffPolicy(backOffPolicy);

    template.setRetryTemplate(retryTemplate);

    // RabbitTemplate 는 Publisher 확인 및 반환을 지원함.
    // 반환된 메시지를 사용하려면 template 의 mandatory 속성이 반드시 true 여야 함.
    // 아니면 특정 메시지에 대한 mandatory-expression 의 결과가 true 여야 함.
    // 이 기능은 publisherReturns 속성이 true 인 CachingConnectionFactory 를 필요로 함.
    template.setMandatory(true);

    // 반환된 메시지는 등록된 ReturnCallback 으로 전달되며 RabbitTemplate 당 하나의 ReturnCallback 만 지원함.
    template.setReturnCallback(
        (message, replyCode, replyText, exchange, routingKey) -> log.info(">> Returned Message Info\n"
            + "> message : {}\n"
            + "> replyCode : {}\n"
            + "> replyText : {}\n"
            + "> exchange : {}\n"
            + "> routingKey : {}", new String(message.getBody()), replyCode, replyText, exchange, routingKey));

    // Publisher 확인(ack)는 ConfirmCallback 으로 전달됨. RabbitTemplate 당 하나의 ConfirmCallback 을 지원함.
    // correlationData : 메시지를 보낼 때 클라이언트가 제공한 객체. send 할 때 추가해줘야 null 로 안 나오는 것 같다.
    // ack : true 면 ack, false 면 nack
    // nack 인 경우 nack 의 원인이 cause 에 포함될 수 있음 (ex. 존재하지 않는 exchange 로 메시지 보냄)
    template.setConfirmCallback((correlationData, ack, cause) -> log.info(">> Confirmed Info\n"
        + "> correlationData : {}\n"
        + "> ack : {}\n"
        + "> cause : {}\n", correlationData != null ? correlationData.toString() : "null", ack, cause));

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
    public Queue task3Queue() {
      return new AnonymousQueue();
    }

    @Bean
    public Binding task1Binding(DirectExchange direct, Queue task1Queue) {
      return BindingBuilder
          .bind(task1Queue)
          .to(direct)
          .with(RouteKey.TASK1);
    }

    @Bean
    public Binding task3Binding(DirectExchange direct, Queue task3Queue) {
      return BindingBuilder
          .bind(task3Queue)
          .to(direct)
          .with(RouteKey.TASK3);
    }
  }
}
