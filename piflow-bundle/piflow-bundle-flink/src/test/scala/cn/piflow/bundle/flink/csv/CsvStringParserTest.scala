package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class CsvStringParserTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/csv/CsvStringParser.json"
    BaseTest.testFlow(file)
  }

}
