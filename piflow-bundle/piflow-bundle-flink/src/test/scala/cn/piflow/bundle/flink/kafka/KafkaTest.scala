package cn.piflow.bundle.flink.kafka

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class KafkaTest {

  @Test
  def testFlow(): Unit = {

    //parse flow json
    val file = "src/test/resources/kafka/kafka.json"
    BaseTest.testFlow(file)
  }

}
