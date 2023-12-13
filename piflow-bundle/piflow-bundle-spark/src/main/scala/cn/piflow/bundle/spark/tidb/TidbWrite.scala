package cn.piflow.bundle.spark.tidb

import java.util.Properties
import cn.piflow._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

class TidbWrite extends ConfigurableStop[DataFrame] {

  override val authorEmail: String = "llei@cnic.com"
  override val description: String = "Write data to tidb"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var driver: String = _
  var url: String = _
  var dbtable: String = _
  var user: String = _
  var password: String = _

  override def perform(in: JobInputStream[DataFrame],
                       out: JobOutputStream[DataFrame],
                       pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sc = spark.sparkContext
    val properties = new Properties()
    properties.put("user", user)
    properties.put("password", password)
    properties.put("driver", driver)
    properties.put("isolationLevel", "NONE") //if not set this value, throw expection
    val df = in.read()
    df.write.mode(SaveMode.Append).jdbc(url, dbtable, properties)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    driver = MapUtil.get(map, key = "driver").asInstanceOf[String]
    url = MapUtil.get(map, key = "url").asInstanceOf[String]
    dbtable = MapUtil.get(map, key = "dbtable").asInstanceOf[String]
    user = MapUtil.get(map, key = "user").asInstanceOf[String]
    password = MapUtil.get(map, key = "password").asInstanceOf[String]

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("The Driver of tidb database")
      .defaultValue("com.mysql.jdbc.Driver")
      .required(true)
      .example("com.mysql.jdbc.Driver")
    descriptor = driver :: descriptor

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url, for example jdbc:mysql://127.0.0.1:4000/mysql")
      .defaultValue("")
      .required(true)
      .example("jdbc:mysql://127.0.0.1:4000/mysql")
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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/TidbWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}
}