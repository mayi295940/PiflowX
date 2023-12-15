package cn.piflow.bundle.flink.common

import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class Route extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "Route data by custom properties,key is port,value is filter"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.RoutePort)

  override val isCustomized: Boolean = true

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val df = in.read()

    val inputTable = tableEnv.fromDataStream(df)

    val tmpTable = "RouteTmp_" + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(tmpTable, inputTable)

    if (this.customizedProperties != null || this.customizedProperties.nonEmpty) {
      val keyIterator = this.customizedProperties.keySet.iterator
      while (keyIterator.hasNext) {
        val port = keyIterator.next()
        val filterCondition = MapUtil.get(this.customizedProperties, port).asInstanceOf[String]
        val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable WHERE $filterCondition")
        val resultStream = tableEnv.toDataStream(resultTable)
        out.write(port, resultStream)
      }
    }

    out.write(df)

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val customizedProperties = new PropertyDescriptor()
      .name("customizedProperties")
      .displayName("customizedProperties")
      .description("custom properties,key is port,value is filter")
      .defaultValue("")
      .required(true)
      .example("\"port1\": \"author = \\\"Carmen Heine\\\"\",\n\"port2\": \"author = \\\"Gerd Hoff\\\"\"")

    descriptor = customizedProperties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Fork.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }
}
