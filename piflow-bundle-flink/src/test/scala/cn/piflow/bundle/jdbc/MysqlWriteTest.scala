package cn.piflow.bundle.jdbc

import cn.piflow.bundle.util.RowTypeUtil
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.types.Row

import java.sql.{Date, PreparedStatement}
import java.math.BigDecimal
import scala.util.Random

object MysqlWriteTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val url = "jdbc:mysql://192.168.56.100:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true"
    val user: String = "root"
    val password: String = "rootroothdp"
    val schema: String = "id:String,name:String,age:Int"
    val count: Int = 10
    val driver = "com.mysql.cj.jdbc.Driver";
    val tableName = "test"

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new GenerateSourceFunction(rowTypeInfo, count))(rowTypeInfo)
    df.print()

    val dataType: RowTypeInfo = df.dataType.asInstanceOf[RowTypeInfo]
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
        .withDriverName(driver)
        .withConnectionCheckTimeoutSeconds(60)
        .build()
    )

    df.addSink(jdbcSink)

    env.execute()

  }


  private class GenerateSourceFunction(schema: RowTypeInfo, count: Int) extends RichSourceFunction[Row] {

    private var rnd: Random = _

    override def run(ctx: SourceFunction.SourceContext[Row]): Unit = {
      val fieldNum = schema.getTotalFields
      val types = schema.getFieldTypes

      for (_ <- 0 until count) {
        val row = new Row(fieldNum)
        for (i <- 0 until fieldNum) {
          row.setField(i, generateRandomValue(rnd, types(i).toString.toLowerCase()))
        }
        ctx.collect(row)
      }
    }

    override def open(parameters: Configuration): Unit = {
      super.open(parameters)
      rnd = new Random()
    }

    override def cancel(): Unit = {}
  }

  private def generateRandomValue(rnd: Random, dataType: String): Any = {
    dataType match {
      case "double" =>
        rnd.nextDouble()
      case "string" =>
        rnd.alphanumeric.take(10).mkString
      case "integer" =>
        rnd.nextInt(100)
      case "long" =>
        rnd.nextLong()
      case "float" =>
        rnd.nextFloat()
      case "date" =>
        new Date(rnd.nextLong())
      case "boolean" =>
        rnd.nextBoolean()
      case _ =>
        throw new RuntimeException("Unsupported type: " + dataType)
    }
  }


}
