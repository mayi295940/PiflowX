package cn.piflow.bundle.flink.json

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class JsonStringParserTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/json/JsonStringParser.json"
    BaseTest.testFlow(file)
  }

}
