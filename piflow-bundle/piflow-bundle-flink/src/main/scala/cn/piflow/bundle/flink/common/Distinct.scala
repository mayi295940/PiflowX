package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class Distinct extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Duplicate based on the specified column name or all column names"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var columnNames: String = _

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inDf: DataStream[Row] = in.read()

    val inputTable = tableEnv.fromDataStream(inDf)

    val distinctTable = inputTable.distinct()

    val outDf = tableEnv.toDataStream(distinctTable)

    out.write(outDf)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val fields = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Fill in the column names you want to duplicate," +
        "multiple columns names separated by commas,if not," +
        "all the columns will be deduplicated")
      .defaultValue("")
      .required(false)
      .example("id")
    descriptor = fields :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }


  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

}
