package cn.piflow.bundle.flink.doris

import org.junit.Test

class DorisWriteTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/doris/DorisWrite.json"
    BaseTest.testFlow(file)
  }


}
