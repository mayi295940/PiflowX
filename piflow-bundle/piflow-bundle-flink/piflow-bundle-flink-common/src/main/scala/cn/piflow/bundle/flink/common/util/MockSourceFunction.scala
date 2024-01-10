package cn.piflow.bundle.flink.common.util

import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.types.Row

import java.util.Date
import scala.util.Random


class MockSourceFunction(schema: RowTypeInfo, count: Int = 10) extends RichSourceFunction[Row] {

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
      case _ => throw new RuntimeException("Unsupported type: " + dataType)
    }
  }
}


