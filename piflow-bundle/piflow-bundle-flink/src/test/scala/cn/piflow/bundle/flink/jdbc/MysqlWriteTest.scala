package cn.piflow.bundle.flink.jdbc

import cn.piflow.bundle.flink.source.mock.MockSourceFunction
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.enums.DataBaseType
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.types.Row

import java.math.BigDecimal
import java.sql.{Date, PreparedStatement}

object MysqlWriteTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val url = "jdbc:mysql://192.168.56.100:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true"
    val user: String = "root"
    val password: String = "rootroothdp"
    val schema: String = "id:String,name:String,age:Int"
    val count: Int = 10
    val tableName = "test"

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new MockSourceFunction(rowTypeInfo, count))
    df.print()

    val dataType: RowTypeInfo = df.getType.asInstanceOf[RowTypeInfo]
    val types = dataType.getFieldTypes
    val fieldNum = dataType.getTotalFields
    val placeholders = (0 until fieldNum).map(_ => "?").toList
    val sql = "insert into %s values(%s)".format(tableName, placeholders.mkString(","))

    val jdbcSink = JdbcSink.sink(sql,
      new JdbcStatementBuilder[Row]() {
        def accept(preparedStatement: PreparedStatement, row: Row): Unit = {
          (0 until fieldNum).foreach(i => {
            types(i) match {
              case BasicTypeInfo.INT_TYPE_INFO => preparedStatement.setInt(i + 1, row.getField(i).asInstanceOf[Int])
              case BasicTypeInfo.STRING_TYPE_INFO => preparedStatement.setString(i + 1, row.getField(i).asInstanceOf[String])
              case BasicTypeInfo.DOUBLE_TYPE_INFO => preparedStatement.setDouble(i + 1, row.getField(i).asInstanceOf[Double])
              case BasicTypeInfo.BYTE_TYPE_INFO => preparedStatement.setByte(i + 1, row.getField(i).asInstanceOf[Byte])
              case BasicTypeInfo.FLOAT_TYPE_INFO => preparedStatement.setFloat(i + 1, row.getField(i).asInstanceOf[Float])
              case BasicTypeInfo.LONG_TYPE_INFO => preparedStatement.setLong(i + 1, row.getField(i).asInstanceOf[Long])
              case BasicTypeInfo.BOOLEAN_TYPE_INFO => preparedStatement.setBoolean(i + 1, row.getField(i).asInstanceOf[Boolean])
              case BasicTypeInfo.DATE_TYPE_INFO => preparedStatement.setDate(i + 1, row.getField(i).asInstanceOf[Date])
              case BasicTypeInfo.BIG_DEC_TYPE_INFO => preparedStatement.setBigDecimal(i + 1, row.getField(i).asInstanceOf[BigDecimal])
              case _ =>
            }
          })
        }
      },
      JdbcExecutionOptions.builder()
        .withMaxRetries(3)
        .withBatchSize(100)
        .withBatchIntervalMs(3000)
        .build(),
      new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
        .withUrl(url)
        .withUsername(user)
        .withPassword(password)
        .withDriverName(DataBaseType.MySQL8.getDriverClassName)
        .withConnectionCheckTimeoutSeconds(60)
        .build()
    )

    df.addSink(jdbcSink)

    env.execute()

  }


}
