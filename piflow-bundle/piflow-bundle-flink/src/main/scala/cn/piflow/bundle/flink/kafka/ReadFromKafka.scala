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

class ReadFromKafka extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Read data from kafka"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var kafka_host: String = _
  private var topic: String = _
  private var topic_pattern: String = _
  private var startup_mode: String = _
  private var schema: String = _
  private var format: String = _
  private var group: String = _
  private var properties: Map[String, Any] = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val columns = RowTypeUtil.getTableSchema(schema)

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + IdGenerator.uuidWithoutSplit

    val conf = getWithConf(topic, topic_pattern, group, startup_mode, properties)

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'kafka',
         |'properties.bootstrap.servers' = '$kafka_host',
         | $conf
         |'format' = '$format'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val query = s"SELECT * FROM $tmpTable"
    out.write(tableEnv.sqlQuery(query))

  }

  private def getWithConf(topic: String,
                          topic_pattern: String,
                          group: String,
                          startup_mode: String,
                          properties: Map[String, Any]): String = {

    var result = List[String]()

    if (StringUtils.isNotBlank(topic)) {
      result = s"'topic' = '$topic'," :: result
    }

    if (StringUtils.isNotBlank(topic_pattern)) {
      result = s"'topic-pattern' = '$topic_pattern'," :: result
    }

    if (StringUtils.isNotBlank(group)) {
      result = s"'properties.group.id' = '$group'," :: result
    }

    if (StringUtils.isNotBlank(startup_mode)) {
      result = s"'scan.startup.mode' = '$startup_mode'," :: result
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
    topic = MapUtil.get(map, key = "topic", "").asInstanceOf[String]
    topic_pattern = MapUtil.get(map, key = "topic_pattern", "").asInstanceOf[String]
    startup_mode = MapUtil.get(map, key = "startup_mode", "").asInstanceOf[String]
    schema = MapUtil.get(map, key = "schema", "").asInstanceOf[String]
    format = MapUtil.get(map, key = "format").asInstanceOf[String]
    group = MapUtil.get(map, key = "group", "").asInstanceOf[String]
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
      .description("读取数据的topic名。亦支持用分号间隔的topic列表，如 'topic-1;topic-2'。" +
        "注意，'topic' 和 'topic-pattern' 两个选项只能使用其中一个。")
      .defaultValue("")
      .required(false)

    val topic_pattern = new PropertyDescriptor()
      .name("topic_pattern")
      .displayName("TOPIC_PATTERN")
      .description("匹配读取topic名称的正则表达式。在作业开始运行时，所有匹配该正则表达式的topic都将被Kafka consumer订阅。" +
        "注意，'topic' 和 'topic-pattern' 两个选项只能使用其中一个。")
      .defaultValue("")
      .required(false)

    val startup_mode = new PropertyDescriptor()
      .name("startup_mode")
      .displayName("STARTUP_MODE")
      .description("Kafka consumer 的启动模式。")
      .allowableValues(Set("earliest-offset", "latest-offset", "group-offsets", "timestamp", "specific-offsets"))
      .defaultValue("")
      .required(false)

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("SCHEMA")
      .defaultValue("id:int,name:string,age:int")
      .required(true)

    val format = new PropertyDescriptor()
      .name("format")
      .displayName("FORMAT")
      .description("用来序列化或反序列化Kafka消息的格式。注意：该配置项和 'value.format' 二者必需其一。")
      .allowableValues(Set("json", "csv", "avro", "parquet", "orc", "raw", "protobuf",
        "debezium-json", "canal-json", "maxwell-json", "ogg-json"))
      .defaultValue("")
      .required(true)

    val group = new PropertyDescriptor()
      .name("group")
      .displayName("GROUP")
      .description("Kafka source的消费组id。如果未指定消费组ID，" +
        "则会使用自动生成的\"KafkaSource-{tableIdentifier}\"作为消费组ID。")
      .defaultValue("")
      .required(false)

    val properties = new PropertyDescriptor()
      .name("properties")
      .displayName("PROPERTIES")
      .description("Kafka source连接器其他配置")
      .defaultValue("")
      .required(false)

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = topic_pattern :: descriptor
    descriptor = startup_mode :: descriptor
    descriptor = schema :: descriptor
    descriptor = format :: descriptor
    descriptor = group :: descriptor
    descriptor = properties :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/kafka/ReadFromKafka.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.KafkaGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
