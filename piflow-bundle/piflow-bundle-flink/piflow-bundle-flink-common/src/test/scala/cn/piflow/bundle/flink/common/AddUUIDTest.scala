package cn.piflow.bundle.flink.common

import org.junit.Test

class AddUUIDTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/uuid.json"
    BaseTest.testFlow(file)
  }

}
