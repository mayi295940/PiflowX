package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class ExecuteSQLStop extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "Create temporary view table to execute sql"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var sql: String = _
  private var viewName: String = _

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inDf: DataStream[Row] = in.read()

    val inputTable = tableEnv.fromDataStream(inDf)

    tableEnv.createTemporaryView(viewName, inputTable)

    val resultTable = tableEnv.sqlQuery(sql)

    val resultStream = tableEnv.toDataStream(resultTable)

    out.write(resultStream)
  }


  override def setProperties(map: Map[String, Any]): Unit = {
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
    viewName = MapUtil.get(map, "viewName").asInstanceOf[String]
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val sql = new PropertyDescriptor().name("sql")
      .displayName("Sql")
      .description("Sql string")
      .defaultValue("")
      .required(true)
      .example("select * from temp")
    descriptor = sql :: descriptor

    val ViewName = new PropertyDescriptor()
      .name("viewName")
      .displayName("ViewName")
      .description("Name of the temporary view table")
      .defaultValue("temp")
      .required(true)
      .example("temp")

    descriptor = ViewName :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }


}



