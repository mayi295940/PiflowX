package cn.piflow.bundle.flink.kafka

import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.api.{DataTypes, Table}
import org.apache.flink.table.types.DataType

class ReadFromKafka extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Read data from kafka"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var kafka_host: String = _
  var topic: String = _
  var schema: String = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    // 定义 Kafka 配置参数
    val kafkaProperties = new java.util.Properties()
    kafkaProperties.setProperty("bootstrap.servers", kafka_host)
    //todo ...其他连接器和属性设置

    // todo scheme如何定义
    // 定义字段名和字段类型
    val fieldNames: List[String] = schema.split(",").map(x => x.trim).toList
    val fieldTypes: Array[DataType] = schema.split(",").map(x => x.trim).map(_ => DataTypes.STRING())

    // 生成 Kafka 数据源 DDL 语句
    var kafkaSourceDDL = "CREATE TABLE kafka_source ("

    for (i <- fieldNames.indices) {
      kafkaSourceDDL += s"  $fieldNames(${i}) $fieldTypes[$i],"
    }

    kafkaSourceDDL = kafkaSourceDDL.stripMargin +
      ") WITH ('connector' = 'kafka', 'topic' = '$topic', 'properties' = $kafkaProperties)"

    tableEnv.executeSql(kafkaSourceDDL)

    val query =
      s"""
         |INSERT INTO kafka_source
         |SELECT * FROM kafka_source
     """.stripMargin

    out.write(tableEnv.sqlQuery(query))
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    kafka_host = MapUtil.get(map, key = "kafka_host").asInstanceOf[String]
    topic = MapUtil.get(map, key = "topic").asInstanceOf[String]
    schema = MapUtil.get(map, key = "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val kafka_host = new PropertyDescriptor()
      .name("kafka_host")
      .displayName("KAFKA_HOST")
      .defaultValue("")
      .required(true)

    val topic = new PropertyDescriptor()
      .name("topic")
      .displayName("TOPIC")
      .defaultValue("")
      .required(true)

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("SCHEMA")
      .defaultValue("")
      .required(true)

    descriptor = kafka_host :: descriptor
    descriptor = topic :: descriptor
    descriptor = schema :: descriptor
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
