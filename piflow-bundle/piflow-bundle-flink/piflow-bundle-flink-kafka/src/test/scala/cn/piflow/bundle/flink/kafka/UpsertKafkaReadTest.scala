package cn.piflow.bundle.flink.kafka

import org.junit.Test

class UpsertKafkaReadTest {

  @Test
  def testFlow(): Unit = {

    //parse flow json
    val file = "src/test/resources/kafka/upsert_kafka_read.json"
    BaseTest.testFlow(file)
  }

}
