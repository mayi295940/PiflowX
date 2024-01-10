package cn.piflow.bundle.flink.common

import org.junit.Test

class SelectFieldTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/SelectFiled.json"
    BaseTest.testFlow(file)
  }

}
