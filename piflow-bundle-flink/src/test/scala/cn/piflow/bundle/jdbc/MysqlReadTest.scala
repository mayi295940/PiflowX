package cn.piflow.bundle.jdbc

import cn.piflow.bundle.util.RowTypeUtil
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.jdbc.JdbcInputFormat
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object MysqlReadTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val url = "jdbc:mysql://127.0.0.1:3306/ft_pt_bdp?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true"
    val user: String = "root"
    val password: String = "123456"
    val sql: String = "select id, name from bdp_common_type limit 10"
    val schema: String = "id:String,name:String"

    val jdbcInputFormat = JdbcInputFormat.buildJdbcInputFormat
      .setDrivername("com.mysql.cj.jdbc.Driver")
      .setDBUrl(url)
      .setQuery(sql)
      .setUsername(user)
      .setPassword(password)
      .setRowTypeInfo(RowTypeUtil.getRowTypeInfo(schema))
      .finish()

    val df = env.createInput(jdbcInputFormat)

    df.print()

    env.execute()

  }


}
