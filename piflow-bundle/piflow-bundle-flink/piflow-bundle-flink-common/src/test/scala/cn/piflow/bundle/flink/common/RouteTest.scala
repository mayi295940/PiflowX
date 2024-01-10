package cn.piflow.bundle.flink.common

import org.junit.Test

class RouteTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/Route.json"
    BaseTest.testFlow(file)
  }

}
