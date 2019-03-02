package de.dennisbuerger.learn.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.dennisbuerger.learn.kafka.Application;
import de.dennisbuerger.learn.kafka.Receiver;
import de.dennisbuerger.learn.kafka.Sender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1,
    topics = {"dummy"}, brokerProperties={"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class SpringKafkaApplicationTest {

  static final String HELLOWORLD_TOPIC = "dummy";

  @Autowired
  private Receiver receiver;

  @Autowired
  private Sender sender;

  @Test
  public void testReceive() throws Exception {
    sender.send("Hello Spring Kafka!");

    receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    assertThat(receiver.getLatch().getCount()).isEqualTo(0);
  }
}