package cn.piflow.bundle.jdbc

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.types.Row

import java.math.BigDecimal
import java.sql.{Date, PreparedStatement}

class MysqlWrite extends ConfigurableStop {

  val authorEmail: String = "xjzhu@cnic.cn"
  val description: String = "Write data to mysql database with jdbc"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var user: String = _
  private var password: String = _
  private var tableName: String = _
  private var driver: String = _
  private var batchSize: Int = _
  private var saveMode: String = _
  private var columnReflect: String = _
  private var partition: String = _

  def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val jdbcDF = in.read().asInstanceOf[DataStream[Row]]

    val dataType: RowTypeInfo = jdbcDF.getType.asInstanceOf[RowTypeInfo]
    val types = dataType.getFieldTypes
    val fieldNum = dataType.getTotalFields
    val placeholders = (0 until fieldNum).map(_ => "?").toList
    val sql = "insert into %s values(%s)".format(tableName, placeholders.mkString(","))

    val jdbcSink = JdbcSink.sink(sql,
      new JdbcStatementBuilder[Row]() {
        def accept(preparedStatement: PreparedStatement, row: Row): Unit = {
          (0 until fieldNum).foreach(i => {
            types(i) match {
              case BasicTypeInfo.INT_TYPE_INFO => preparedStatement.setInt(i + 1, row.getField(i).asInstanceOf[Int])
              case BasicTypeInfo.STRING_TYPE_INFO => preparedStatement.setString(i + 1, row.getField(i).asInstanceOf[String])
              case BasicTypeInfo.DOUBLE_TYPE_INFO => preparedStatement.setDouble(i + 1, row.getField(i).asInstanceOf[Double])
              case BasicTypeInfo.BYTE_TYPE_INFO => preparedStatement.setByte(i + 1, row.getField(i).asInstanceOf[Byte])
              case BasicTypeInfo.FLOAT_TYPE_INFO => preparedStatement.setFloat(i + 1, row.getField(i).asInstanceOf[Float])
              case BasicTypeInfo.LONG_TYPE_INFO => preparedStatement.setLong(i + 1, row.getField(i).asInstanceOf[Long])
              case BasicTypeInfo.BOOLEAN_TYPE_INFO => preparedStatement.setBoolean(i + 1, row.getField(i).asInstanceOf[Boolean])
              case BasicTypeInfo.DATE_TYPE_INFO => preparedStatement.setDate(i + 1, row.getField(i).asInstanceOf[Date])
              case BasicTypeInfo.BIG_DEC_TYPE_INFO => preparedStatement.setBigDecimal(i + 1, row.getField(i).asInstanceOf[BigDecimal])
              case _ =>
            }
          })
        }
      },
      JdbcExecutionOptions.builder()
        .withMaxRetries(3)
        .withBatchSize(batchSize)
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
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    saveMode = MapUtil.get(map, "saveMode").asInstanceOf[String]
    driver = MapUtil.get(map, "driver").asInstanceOf[String]
    batchSize = MapUtil.get(map, "batchSize").asInstanceOf[String].toInt
    //    columnReflect = MapUtil.get(map, "columnReflect").asInstanceOf[String]
    //    partition = MapUtil.get(map, "partition").asInstanceOf[String]
    //    partitions = MapUtil.get(map, "numPartitions").asInstanceOf[String]
    //    isolationLevel = MapUtil.get(map, "isolationLevel").asInstanceOf[String]
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


}
