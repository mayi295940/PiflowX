package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class CsvParserTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/csv/CsvParser.json"
    BaseTest.testFlow(file)
  }

}
