package cn.piflow.bundle.flink.cdc.mysql

import org.junit.Test

class MysqlCdcTest {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/cdc/mysql/MysqlCdc.json"
    BaseTest.testFlow(file)
  }

}
