package cn.piflow.bundle.flink.common

import org.junit.Test

class SQLQueryTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/common/JDBCRead.json"
    BaseTest.testFlow(file)
  }

}
