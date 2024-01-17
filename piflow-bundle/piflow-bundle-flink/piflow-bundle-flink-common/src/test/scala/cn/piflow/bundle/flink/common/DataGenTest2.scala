package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class DataGenTest2 {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/DataGen.json"
    BaseTest.testFlow(file)
  }

}
