package cn.piflow.bundle.flink.jdbc

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.enums.DataBaseType
import cn.piflow.util.IdGenerator
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class JDBCRead extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Read data from database with jdbc"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var driver: String = _
  private var username: String = _
  private var password: String = _
  private var tableName: String = _
  private var schema: String = _

  // todo jdbc connector other parameter
  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val columns = RowTypeUtil.getTableSchema(schema)

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    val conf = getWithConf(driver, username, password)

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'jdbc',
         |'url' = '$url',
         |$conf
         |'table-name' = '$tableName'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

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
    driver = MapUtil.get(map, "driver", "").asInstanceOf[String]
    username = MapUtil.get(map, "username").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("JDBC 数据库 url")
      .defaultValue("")
      .required(true)
      .example("jdbc:mysql://127.0.0.1:3306/dbname")
    descriptor = url :: descriptor

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("用于连接到此URL的JDBC驱动类名，如果不设置，将自动从URL中推导")
      .defaultValue(DataBaseType.MySQL8.getDriverClassName)
      .required(true)
      .example(DataBaseType.MySQL8.getDriverClassName)
    descriptor = driver :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("username")
      .description("JDBC用户名。如果指定了username和password中的任一参数，则两者必须都被指定。")
      .defaultValue("")
      .required(true)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("JDBC密码")
      .defaultValue("")
      .required(false)
      .example("12345")
      .sensitive(true)
    descriptor = password :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of mock data, format is column:columnType:isNullable. " +
        "Separate multiple fields with commas. " +
        "columnType can be String/Int/Long/Float/Double/Boolean. " +
        "isNullable can be left blank, the default value is false. ")
      .defaultValue("")
      .required(true)
      .example("id:String,name:String,age:Int")
    descriptor = schema :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("TableName")
      .description("连接到 JDBC 表的名称")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .example("test.user1")
    descriptor = tableName :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/MysqlRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
