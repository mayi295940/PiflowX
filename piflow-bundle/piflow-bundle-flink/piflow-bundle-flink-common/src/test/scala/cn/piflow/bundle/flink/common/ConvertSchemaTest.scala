package cn.piflow.bundle.flink.common

import org.junit.Test

class ConvertSchemaTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/ConvertSchema.json"
    BaseTest.testFlow(file)
  }

}
