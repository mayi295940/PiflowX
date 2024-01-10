package cn.piflow.bundle.flink.common

import org.junit.Test

class InsertTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/Insert.json"
    BaseTest.testFlow(file)
  }

}
