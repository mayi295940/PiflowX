package cn.piflow.bundle.flink.common

import org.junit.Test

class JoinTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/join.json"
    BaseTest.testFlow(file)
  }

}
