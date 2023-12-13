package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.BaseTest
import org.junit.Test

class DripFieldTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/DropFiled.json"
    BaseTest.testFlow(file)
  }

}
