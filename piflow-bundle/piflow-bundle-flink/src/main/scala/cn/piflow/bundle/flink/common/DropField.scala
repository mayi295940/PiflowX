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

class DropField extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = "ygang@cnic.cn"
  val description: String = "Delete one or more columns"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  def perform(in: JobInputStream[DataStream[Row]],
              out: JobOutputStream[DataStream[Row]],
              pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inDf: DataStream[Row] = in.read()

    val inputTable = tableEnv.fromDataStream(inDf)

    val field = columnNames.split(",").map(x => x.trim)

    val array = new Array[ApiExpression](field.length)

    for (x <- field.indices) {
      array(x) = $(field(x))
    }

    val resultTable = inputTable.dropColumns(array: _*)

    val resultStream = tableEnv.toDataStream(resultTable)

    out.write(resultStream)

  }

  def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

  def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val inPorts = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Fill in the columns you want to delete," +
        "multiple columns names separated by commas")
      .defaultValue("")
      .required(true)
      .example("id")
    descriptor = inPorts :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/DropColumnNames.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

}



