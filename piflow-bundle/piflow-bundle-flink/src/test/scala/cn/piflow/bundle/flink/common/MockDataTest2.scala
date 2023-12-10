package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class MockDataTest2 {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/MockData.json"
    BaseTest.testFlow(file)
  }

}
