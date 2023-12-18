package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class UnionAll extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = ""
  override val description: String = "Union多个输入源。输入源必须具有相同的字段类型。"
  override val inportList: List[String] = List(Port.AnyPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var inports: List[String] = _

  override def setProperties(map: Map[String, Any]): Unit = {
    val inportStr = MapUtil.get(map, "inports").asInstanceOf[String]
    inports = inportStr.split(",").map(x => x.trim).toList
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val inports = new PropertyDescriptor()
      .name("inports")
      .displayName("Inports")
      .description("Inports string are separated by commas")
      .defaultValue("")
      .required(true)
    descriptor = inports :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Merge.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val resultTable = in.ports().map(in.read).reduce((x, y) => {
      val leftTable = tableEnv.fromDataStream(x)
      val rightTable = tableEnv.fromDataStream(y)
      val table = leftTable.unionAll(rightTable)
      table
    })

    val resultStream = tableEnv.toDataStream(resultTable)
    out.write(resultStream)

  }
}
