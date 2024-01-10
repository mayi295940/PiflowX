package cn.piflow.bundle.flink.common

import org.junit.Test

class FilterTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/filter.json"
    BaseTest.testFlow(file)
  }

}
