package cn.piflow.bundle.flink.es7

import org.junit.Test

class Elasticsearch7WriteTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/es7/Elasticsearch7Write.json"
    BaseTest.testFlow(file)
  }

}
