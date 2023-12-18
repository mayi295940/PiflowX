package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.TableResult
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class SQLExecute extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "execute sql"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var sql: String = _

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()
    val result: TableResult = tableEnv.executeSql(sql)
    result.print()

  }


  override def setProperties(map: Map[String, Any]): Unit = {
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }


}



