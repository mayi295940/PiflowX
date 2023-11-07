package cn.piflow.bundle.jdbc

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.types.Row

import java.sql.PreparedStatement

class MysqlWrite extends ConfigurableStop {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Write data to mysql database with jdbc"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var url: String = _
  var user: String = _
  var password: String = _
  var dbtable: String = _
  var driver: String = _
  var saveMode: String = _

  def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val jdbcDF = in.read().asInstanceOf[DataStream[Row]]

    val jdbcSink = JdbcSink.sink("insert into ws values(?,?,?)",
      new JdbcStatementBuilder[Row]() {
        def accept(preparedStatement: PreparedStatement, row: Row): Unit = {
          preparedStatement.setString(1, "1")
          preparedStatement.setLong(2, 1L)
          preparedStatement.setInt(3, 1)
        }
      },
      JdbcExecutionOptions.builder()
        .withMaxRetries(3)
        .withBatchSize(100)
        .withBatchIntervalMs(3000)
        .build(),
      new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
        .withUrl(url)
        .withUsername(user)
        .withPassword(password)
        .withDriverName(driver)
        .withConnectionCheckTimeoutSeconds(60)
        .build()
    )

    jdbcDF.addSink(jdbcSink)
  }

  def initialize(ctx: ProcessContext): Unit = {

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    user = MapUtil.get(map, "user").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    dbtable = MapUtil.get(map, "dbtable").asInstanceOf[String]
    saveMode = MapUtil.get(map, "saveMode").asInstanceOf[String]
    driver = MapUtil.get(map, "driver").asInstanceOf[String]
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
    descriptor = password :: descriptor

    val dbtable = new PropertyDescriptor()
      .name("dbtable")
      .displayName("DBTable")
      .description("The table you want to write")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = dbtable :: descriptor

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


}
