package cn.piflow.bundle.common

import cn.piflow._
import cn.piflow.bundle.util.RowTypeUtil
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.types.Row

import java.util.Date
import scala.util.Random

class MockData extends ConfigurableStop {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Mock dataframe."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: String = _
  private var count: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
    count = MapUtil.get(map, "count").asInstanceOf[String].toInt
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of mock data, format is column:columnType:isNullable. " +
        "Separate multiple fields with commas. " +
        "columnType can be String/Int/Long/Float/Double/Boolean. " +
        "isNullable can be left blank, the default value is false. ")
      .defaultValue("")
      .required(true)
      .example("id:String,name:String,age:Int")
    descriptor = schema :: descriptor

    val count = new PropertyDescriptor()
      .name("count")
      .displayName("Count")
      .description("The count of dataframe")
      .defaultValue("")
      .required(true)
      .example("10")
    descriptor = count :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext): Unit = {}

  override def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val env = pec.get[StreamExecutionEnvironment]()

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new GenerateSourceFunction(rowTypeInfo, count))(rowTypeInfo)

    out.write(df)
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
