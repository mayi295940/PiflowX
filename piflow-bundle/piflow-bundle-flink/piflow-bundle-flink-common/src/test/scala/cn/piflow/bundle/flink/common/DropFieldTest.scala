package cn.piflow.bundle.flink.common

import org.junit.Test

class DropFieldTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/DropFiled.json"
    BaseTest.testFlow(file)
  }

}
