package cn.piflow.bundle.flink.cdc.mongodb

import cn.piflow._
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{IdGenerator, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class MongoDBCdc extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "MongoDB CDC连接器允许从MongoDB读取快照数据和增量数据。"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var hosts: String = _
  private var username: String = _
  private var password: String = _
  private var database: String = _
  private var collection: String = _
  private var tableDefinition: FlinkTableDefinition = _
  private var properties: Map[String, Any] = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns,
    ifNotExists,
    tableComment,
    partitionStatement,
    _,
    _) = RowTypeUtil.getTableSchema(tableDefinition)

    var tmpTable: String = ""
    if (StringUtils.isEmpty(tableDefinition.getRegisterTableName)) {
      tmpTable = this.getClass.getSimpleName.stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit
    } else {
      tmpTable += tableDefinition.getFullRegisterTableName
    }

    val ddl =
      s""" CREATE TABLE $ifNotExists $tmpTable
         | $columns
         | $tableComment
         | $partitionStatement
         | WITH (
         |'connector' = 'mongodb-cdc',
         |$getWithConf
         |'hosts' = '$hosts'
         |)
         |"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

  }

  private def getWithConf: String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(username)) {
      result = s"'username' = '$username'," :: result
    }

    if (StringUtils.isNotBlank(password)) {
      result = s"'password' = '$password'," :: result
    }

    if (StringUtils.isNotBlank(database)) {
      result = s"'database' = '$database'," :: result
    }

    if (StringUtils.isNotBlank(collection)) {
      result = s"'collection' = '$collection'," :: result
    }

    if (properties != null && properties.nonEmpty) {
      for ((k, v) <- properties) {
        result = s"'$k' = '$v'," :: result
      }
    }

    result.mkString("")
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    hosts = MapUtil.get(map, "hosts").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    database = MapUtil.get(map, "database").asInstanceOf[String]
    collection = MapUtil.get(map, "collection").asInstanceOf[String]
    val tableDefinitionMap = MapUtil.get(map, key = "tableDefinition", Map()).asInstanceOf[Map[String, Any]]
    tableDefinition = JsonUtil.mapToObject[FlinkTableDefinition](tableDefinitionMap, classOf[FlinkTableDefinition])
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val tableDefinition = new PropertyDescriptor()
      .name("tableDefinition")
      .displayName("TableDefinition")
      .description("Flink table定义。")
      .defaultValue("")
      .language(Language.FlinkTableSchema)
      .example("")
      .required(true)
    descriptor = tableDefinition :: descriptor

    val url = new PropertyDescriptor()
      .name("hosts")
      .displayName("hosts")
      .description("MongoDB 服务器的主机名和端口对的逗号分隔列表。")
      .defaultValue("")
      .required(true)
      .example("localhost:27017,localhost:27018")
      .language(Language.Text)
    descriptor = url :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("Username")
      .description("连接到 MongoDB 时要使用的数据库用户的名称。只有当MongoDB配置为使用身份验证时，才需要填写。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("连接到 MongoDB 时要使用的密码。只有当MongoDB配置为使用身份验证时，才需要填写。")
      .defaultValue("")
      .required(false)
      .example("12345")
      .language(Language.Text)
      .sensitive(true)
    descriptor = password :: descriptor

    val database = new PropertyDescriptor()
      .name("database")
      .displayName("database")
      .description("要监视更改的数据库的名称。 如果未设置，则将捕获所有数据库。" +
        "该数据库还支持正则表达式来监视与正则表达式匹配的多个数据库。")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .example("test")
    descriptor = database :: descriptor

    val collection = new PropertyDescriptor()
      .name("collection")
      .displayName("collection")
      .description("数据库中要监视更改的集合的名称。 如果未设置，则将捕获所有集合。" +
        "该集合还支持正则表达式来监视与完全限定的集合标识符匹配的多个集合。")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .example("test")
    descriptor = collection :: descriptor

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("PROPERTIES")
      .description("连接器其他配置。")
      .defaultValue("{}")
      .required(false)

    descriptor = properties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/mongodb/MongoCdc.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CdcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}