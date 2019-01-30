package me.dicorndl.rabbitmqtutorials.common;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageCreator {

  public static String helloDotMessage(AtomicInteger dots, AtomicInteger count) {
    StringBuilder builder = new StringBuilder("Hello");
    if (dots.getAndIncrement() == 3) {
      dots.set(1);
    }
    for (int i = 0; i < dots.get(); i++) {
      builder.append('.');
    }
    builder.append(count.incrementAndGet());
    return builder.toString();
  }

  public static String helloToKeyMessage(String key, AtomicInteger count) {
    return String.format("Hello to %s %d", key, count.incrementAndGet());
  }
}
