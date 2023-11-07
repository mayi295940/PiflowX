package cn.piflow.bundle.common

import cn.piflow.Constants
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.types.Row

import java.util.Date
import scala.util.Random

object MockDataTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val count: Int = 10

    val schema: String = "id:String,name:string,age:int,salary:double,time:date"

    val rowTypeInfo = getRowTypeInfo(schema)

    env.addSource(new GenerateSourceFunction(rowTypeInfo, count))(rowTypeInfo)
      .print()

    env.execute()

  }


  /**
   * 生成Row类型的TypeInformation.
   */
  private def getRowTypeInfo(schema: String): RowTypeInfo = {
    val field = schema.split(Constants.COMMA)

    val columnTypes = new Array[TypeInformation[_]](field.size)

    val fieldNames = new Array[String](field.size)

    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      fieldNames(i) = columnName

      columnType.toLowerCase() match {
        case "string" => columnTypes(i) = BasicTypeInfo.STRING_TYPE_INFO
        case "int" => columnTypes(i) = BasicTypeInfo.INT_TYPE_INFO
        case "double" => columnTypes(i) = BasicTypeInfo.DOUBLE_TYPE_INFO
        case "float" => columnTypes(i) = BasicTypeInfo.FLOAT_TYPE_INFO
        case "long" => columnTypes(i) = BasicTypeInfo.LONG_TYPE_INFO
        case "boolean" => columnTypes(i) = BasicTypeInfo.BOOLEAN_TYPE_INFO
        case "date" => columnTypes(i) = BasicTypeInfo.DATE_TYPE_INFO
        case "timestamp" => columnTypes(i) = BasicTypeInfo.DATE_TYPE_INFO
        case _ =>
          throw new RuntimeException("Unsupported type: " + columnType)
      }
    }

    val info = new RowTypeInfo(columnTypes, fieldNames)

    info
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
