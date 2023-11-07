package cn.piflow.bundle.jdbc

import cn.piflow._
import cn.piflow.bundle.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.jdbc.JdbcInputFormat
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment


class OracleRead extends ConfigurableStop {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Read data From oracle"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var url: String = _
  var user: String = _
  var password: String = _
  var sql: String = _
  var schema: String = _

  override def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val env = pec.get[StreamExecutionEnvironment]()

    val jdbcInputFormat = JdbcInputFormat.buildJdbcInputFormat
      .setDrivername("oracle.jdbc.OracleDriver")
      .setDBUrl(url)
      .setQuery(sql)
      .setUsername(user)
      .setPassword(password)
      .setRowTypeInfo(RowTypeUtil.getRowTypeInfo(schema))
      .finish()

    val df = env.createInput(jdbcInputFormat)
    out.write(df)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    user = MapUtil.get(map, "user").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url of oracle")
      .defaultValue("")
      .required(true)
      .example("jdbc:oracle:thin:@10.0.86.237:1521/newdb")
    descriptor = url :: descriptor

    val user = new PropertyDescriptor()
      .name("user")
      .displayName("User")
      .description("The user name of database")
      .defaultValue("")
      .required(true)
      .example("root")
    descriptor = user :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of database")
      .defaultValue("")
      .required(true)
      .example("123456")
      .sensitive(true)
      .sensitive(true)
    descriptor = password :: descriptor

    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("The sql sentence you want to execute")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .example("select * from test")
    descriptor = sql :: descriptor

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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/OracleRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def initialize(ctx: ProcessContext): Unit = {}


}
