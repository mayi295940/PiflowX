package cn.piflow.bundle.flink.kafka

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class WriteToKafka extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Write data to kafka"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var kafka_host: String = _
  private var topic: String = _
  private var schema: String = _
  private var format: String = _
  private var properties: Map[String, Any] = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    var columns: String = null

    if (StringUtils.isNotBlank(schema)) {
      columns = RowTypeUtil.getTableSchema(schema)
    } else {
      columns = RowTypeUtil.getTableSchema(inputTable)
    }

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + IdGenerator.uuidWithoutSplit

    val conf = getWithConf(topic, properties)

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'kafka',
         |'properties.bootstrap.servers' = '$kafka_host',
         |'topic' = '$topic',
         | $conf
         |'format' = '$format'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    inputTable.executeInsert(tmpTable)
  }

  private def getWithConf(topic: String, properties: Map[String, Any]): String = {

    var result = List[String]()

    if (StringUtils.isNotBlank(topic)) {
      result = s"'topic' = '$topic'," :: result
    }

    if (properties != null && properties.nonEmpty) {
      for ((k, v) <- properties) {
        result = s"'$k' = '$v'," :: result
      }
    }

    result.mkString("")
  }


  def initialize(ctx: ProcessContext[Table]): Unit = {}


  def setProperties(map: Map[String, Any]): Unit = {
    kafka_host = MapUtil.get(map, key = "kafka_host").asInstanceOf[String]
    topic = MapUtil.get(map, key = "topic").asInstanceOf[String]
    schema = MapUtil.get(map, key = "schema", "").asInstanceOf[String]
    format = MapUtil.get(map, key = "format").asInstanceOf[String]
    properties = MapUtil.get(map, key = "properties", Map()).asInstanceOf[Map[String, Any]]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val kafka_host = new PropertyDescriptor()
      .name("kafka_host")
      .displayName("KAFKA_HOST")
      .description("逗号分隔的Kafka broker列表。")
      .defaultValue("")
      .required(true)

    val topic = new PropertyDescriptor()
      .name("topic")
      .displayName("TOPIC")
      .description("写入的topi 名。注意不支持topic列表。")
      .defaultValue("")
      .required(true)

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("SCHEMA")
      .defaultValue("id:int,name:string,age:int")
      .required(false)

    val format = new PropertyDescriptor()
      .name("format")
      .displayName("FORMAT")
      .description("用来序列化或反序列化Kafka消息的格式。注意：该配置项和 'value.format' 二者必需其一。")
      .allowableValues(Set("json", "csv", "avro", "parquet", "orc", "raw", "protobuf",
        "debezium-json", "canal-json", "maxwell-json", "ogg-json"))
      .defaultValue("")
      .required(true)

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("PROPERTIES")
      .description("Kafka source连接器其他配置")
      .defaultValue("")
      .required(false)

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = schema :: descriptor
    descriptor = format :: descriptor
    descriptor = properties :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/WriteToKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
