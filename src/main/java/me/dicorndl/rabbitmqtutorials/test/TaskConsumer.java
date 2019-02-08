package me.dicorndl.rabbitmqtutorials.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("receiver")
@Component
public class TaskConsumer {

  private static final Logger log = LoggerFactory.getLogger(TaskConsumer.class);

  // Annotation-driven Listener Endpoint
  // Bean 의 메소드를 Rabbit listener endpoint 로서 노출시킴. 비동기적으로 메시지를 받을 수 있음.
  // @RabbitListener 지원을 위해서 Configuration 클래스 중 하나에 @EnableRabbit 애노테이션이 붙어있어야 함.
  // queues 에 들어가는 큐는 존재해야하며 exchange 에 binding 된 큐여야 함.
  @RabbitListener(queues = "#{task1Queue.name}")
  public int receiveTaskQueue1(Message message) {
    log.info(" [x] Task 1 Received message : {}", message.toString());

    return 1;
  }

  // config 에서 설정하는 것이 아닌 annotation 을 활용하여 Listen 할 queue 를 설정.
  // SpEL 활용이 가능하다는 데 어떻게 하면 좋을지 잘 모르겠다.
  @RabbitListener(bindings = @QueueBinding(
      value = @Queue, // Anonymous Queue
      exchange = @Exchange(value = "test.direct"),
      key = "TASK2"
  ))
  public void receiveTaskQueue2(Message message) {
    log.info(" [x] Task 2 Received message : {}", message.toString());
  }

  @RabbitListener(queues = "#{task3Queue.name}")
  public void receiveTaskQueue3(Message message) {
    log.info(" [x] Task 3 Received message : {}", message.toString());
  }
}
