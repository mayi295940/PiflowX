package cn.piflow.bundle.flink.jdbc

import org.junit.Test

class JDBCReadTest2 {

  @Test
  def testFlow(): Unit = {
    //parse flow json
    val file = "src/test/resources/jdbc/JDBCRead.json"
    BaseTest.testFlow(file)
  }


}
