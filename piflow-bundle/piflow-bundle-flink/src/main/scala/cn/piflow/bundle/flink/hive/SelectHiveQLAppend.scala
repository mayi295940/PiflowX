package cn.piflow.bundle.flink.hive

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog
import org.apache.flink.types.Row

class SelectHiveQLAppend extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = "qinghua.liao@outlook.com"
  override val description: String = "Execute select clause of hiveQL with AppendStream"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var hiveQL: String = _
  var database: String = _

  val name = "myhive"
  val defaultDatabase = "mydatabase"
  val hiveConfDir = "/piflow-configure/hive-conf"

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val env = pec.get[StreamExecutionEnvironment]()
    val tableEnv = pec.get[StreamTableEnvironment]()

    val hive = new HiveCatalog(name, defaultDatabase, hiveConfDir)

    tableEnv.registerCatalog("myhive", hive)
    tableEnv.useCatalog("myhive")

    import org.apache.flink.table.api.SqlDialect

    tableEnv.getConfig().setSqlDialect(SqlDialect.HIVE)

    tableEnv.useDatabase(database)

    val resultTable = tableEnv.sqlQuery(hiveQL)

    val resultStream: DataStream[Row] = tableEnv.toDataStream(resultTable)

    out.write(resultStream)

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    hiveQL = MapUtil.get(map, "hiveQL").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val hiveQL = new PropertyDescriptor()
      .name("hiveQL")
      .displayName("HiveQL")
      .defaultValue("")
      .allowableValues(Set(""))
      .description("Execute select clause of hiveQL")
      .required(true)
      .language(Language.Text)
      .example("select * from test.user1")
    descriptor = hiveQL :: descriptor
    val database = new PropertyDescriptor()
      .name("database")
      .displayName("DataBase")
      .description("The database name which the hiveQL will execute on")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = database :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/SelectHiveQL.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

}
