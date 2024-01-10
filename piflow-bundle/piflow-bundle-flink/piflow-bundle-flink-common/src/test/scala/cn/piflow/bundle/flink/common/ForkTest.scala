package cn.piflow.bundle.flink.common

import org.junit.Test

class ForkTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/fork.json"
    BaseTest.testFlow(file)
  }

}
