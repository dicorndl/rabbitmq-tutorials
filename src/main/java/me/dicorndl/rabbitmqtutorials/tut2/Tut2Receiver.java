package me.dicorndl.rabbitmqtutorials.tut2;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import me.dicorndl.rabbitmqtutorials.common.StopWatchReceiver;

@RabbitListener(queues = "tut.hello")
public class Tut2Receiver extends StopWatchReceiver {

  private final int instance;

  Tut2Receiver(int i) {
    this.instance = i;
  }

  @RabbitHandler
  public void receiveByQueue(String in) throws InterruptedException {
    receive(in, this.instance);
  }
}
