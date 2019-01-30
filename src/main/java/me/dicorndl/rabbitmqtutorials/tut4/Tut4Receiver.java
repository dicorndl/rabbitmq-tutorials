package me.dicorndl.rabbitmqtutorials.tut4;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import me.dicorndl.rabbitmqtutorials.common.StopWatchReceiver;

public class Tut4Receiver extends StopWatchReceiver {

  @RabbitListener(queues = "#{autoDeleteQueue1.name}")
  public void receive1(String in) throws InterruptedException {
    receive(in, 1);
  }

  @RabbitListener(queues = "#{autoDeleteQueue2.name}")
  public void receive2(String in) throws InterruptedException {
    receive(in, 2);
  }
}
