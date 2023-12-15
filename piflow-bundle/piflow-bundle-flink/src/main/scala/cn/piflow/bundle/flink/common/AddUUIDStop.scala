package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.util.IdGenerator
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class AddUUIDStop extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = ""
  override val description: String = "Add UUID column"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var column: String = _

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val df = in.read()

    val inputTable = tableEnv.fromDataStream(df)

    val tmpTable = "AddUUIDTmp_" + IdGenerator.uuidWithoutSplit

    tableEnv.createTemporaryView(tmpTable, inputTable)

    val resultTable = tableEnv.sqlQuery(s"SELECT UUID() AS ${column}, * FROM $tmpTable")

    val resultStream = tableEnv.toDataStream(resultTable)

    out.write(resultStream)

  }


  override def setProperties(map: Map[String, Any]): Unit = {
    column = MapUtil.get(map, "column").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()


    val column = new PropertyDescriptor()
      .name("column")
      .displayName("Column")
      .description("The column is the name of the uuid you want to add")
      .defaultValue("uuid")
      .required(true)
      .example("uuid")
    descriptor = column :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/addUUID.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

}
