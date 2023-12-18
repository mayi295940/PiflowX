package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.ApiExpression
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row


class ConvertSchema extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "Change field name"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var schema: String = _

  def perform(in: JobInputStream[DataStream[Row]],
              out: JobOutputStream[DataStream[Row]],
              pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inDf: DataStream[Row] = in.read()

    val inputTable = tableEnv.fromDataStream(inDf)

    val fields = schema.split(Constants.COMMA).map(x => x.trim)

    val array = new Array[ApiExpression](fields.length)

    for (x <- fields.indices) {
      val old_new: Array[String] = fields(x).split(Constants.ARROW_SIGN).map(x => x.trim)
      array(x) = $(old_new(0)).as(old_new(1))
    }

    val resultTable = inputTable.renameColumns(array: _*)

    val resultStream = tableEnv.toDataStream(resultTable)
    out.write(resultStream)
  }

  def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

  def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val inports = new PropertyDescriptor().name("schema")
      .displayName("Schema")
      .description("Change column names," +
        "multiple column names are separated by commas")
      .defaultValue("")
      .required(true)
      .example("id->uuid")
    descriptor = inports :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ConvertSchema.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

}



