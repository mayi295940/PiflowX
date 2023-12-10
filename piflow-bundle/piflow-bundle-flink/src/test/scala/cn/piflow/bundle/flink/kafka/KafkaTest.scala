package cn.piflow.bundle.flink.kafka

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class KafkaTest {

  @Test
  def testFlow(): Unit = {

    //parse flow json
    val file = "src/main/resources/test/kafka.json"
    BaseTest.testFlow(file)
  }

}
