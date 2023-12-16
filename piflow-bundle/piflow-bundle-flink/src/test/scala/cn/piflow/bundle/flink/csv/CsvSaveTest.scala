package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class CsvSaveTest {

  @Test
  def testFlow(): Unit = {
    val file = "src/test/resources/csv/CsvSave.json"
    BaseTest.testFlow(file)
  }

}
