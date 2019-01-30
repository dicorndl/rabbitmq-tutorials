package me.dicorndl.rabbitmqtutorials.tut6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class Tut6Server {

  private static final Logger LOG = LoggerFactory.getLogger(Tut6Server.class);

  @RabbitListener(queues = "tut.rpc.requests")
  public int fibonacci(int n) {
    LOG.info(" [x] Received request for " + n);
    int result = fib(n);
    LOG.info(" [.] Returned " + result);
    return result;
  }

  public int fib(int n) {
    return n == 0 ? 0 : n == 1 ? 1 : (fib(n - 1) + fib(n - 2));
  }
}
