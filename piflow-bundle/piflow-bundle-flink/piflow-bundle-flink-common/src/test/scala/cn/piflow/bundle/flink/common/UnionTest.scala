package cn.piflow.bundle.flink.common

import org.apache.flink.api.common.RuntimeExecutionMode
import org.junit.Test

class UnionTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/union.json"
    BaseTest.testFlow(file, RuntimeExecutionMode.BATCH)
  }

}
