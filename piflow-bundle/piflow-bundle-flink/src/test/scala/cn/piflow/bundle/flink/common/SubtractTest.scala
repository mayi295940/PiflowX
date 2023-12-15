package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.BaseTest
import org.apache.flink.api.common.RuntimeExecutionMode
import org.junit.Test

class SubtractTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/subtract.json"
    BaseTest.testFlow(file, RuntimeExecutionMode.BATCH)
  }

}
