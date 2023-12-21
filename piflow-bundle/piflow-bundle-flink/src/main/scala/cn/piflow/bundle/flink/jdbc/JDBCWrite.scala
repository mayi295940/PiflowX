package cn.piflow.bundle.flink.jdbc

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class JDBCWrite extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Write data to database with jdbc"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var username: String = _
  private var password: String = _
  private var tableName: String = _
  private var driver: String = _
  private var batchSize: Int = _
  private var saveMode: String = _
  private var columnReflect: String = _
  private var partition: String = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    val columns = RowTypeUtil.getTableSchema(inputTable)

    val conf = getWithConf(driver, username, password)

    // 生成数据源 DDL 语句
    val ddl =
      s""" CREATE TABLE $tableName ($columns) WITH (
         |'connector' = 'jdbc',
         |'url' = '$url',
         |$conf
         |'table-name' = '$tableName'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(ddl)
    tableEnv.executeSql(ddl)

    inputTable.insertInto(tableName).execute().print()


  }

  private def getWithConf(driver: String, username: String, password: String): String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(driver)) {
      result = s"'driver' = '$driver'," :: result
    }

    if (StringUtils.isNotBlank(username)) {
      result = s"'username' = '$username'," :: result
    }

    if (StringUtils.isNotBlank(password)) {
      result = s"'password' = '$password'," :: result
    }

    result.mkString("")
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    username = MapUtil.get(map, "username").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    //saveMode = MapUtil.get(map, "saveMode").asInstanceOf[String]
    //driver = MapUtil.get(map, "driver").asInstanceOf[String]
    //batchSize = MapUtil.get(map, "batchSize").asInstanceOf[String].toInt
    //columnReflect = MapUtil.get(map, "columnReflect").asInstanceOf[String]
    //partition = MapUtil.get(map, "partition").asInstanceOf[String]
    //partitions = MapUtil.get(map, "numPartitions").asInstanceOf[String]
    //isolationLevel = MapUtil.get(map, "isolationLevel").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url, for example jdbc:mysql://127.0.0.1/dbname")
      .defaultValue("")
      .required(true)
      .example("jdbc:mysql://127.0.0.1/dbname")
    descriptor = url :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("username")
      .description("The user name of database")
      .defaultValue("")
      .required(true)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of database")
      .defaultValue("")
      .required(true)
      .example("123456")
      .sensitive(true)
    descriptor = password :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("DBTable")
      .description("The table you want to write")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = tableName :: descriptor

    val batchSize = new PropertyDescriptor()
      .name("batchSize")
      .displayName("BatchSize")
      .description("The size of batch")
      .defaultValue("100")
      .required(false)
      .example("100")
    descriptor = batchSize :: descriptor

    val saveModeOption = Set("Append", "Overwrite", "Ignore")
    val saveMode = new PropertyDescriptor()
      .name("saveMode")
      .displayName("SaveMode")
      .description("The save mode for table")
      .allowableValues(saveModeOption)
      .defaultValue("Append")
      .required(true)
      .example("Append")
    descriptor = saveMode :: descriptor

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("The Driver of mysql database")
      .defaultValue("com.mysql.jdbc.Driver")
      .required(true)
      .example("com.mysql.jdbc.Driver")
    descriptor = driver :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/MysqlWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK


}
