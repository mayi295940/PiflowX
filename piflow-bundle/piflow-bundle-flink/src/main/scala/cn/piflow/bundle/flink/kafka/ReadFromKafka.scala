package cn.piflow.bundle.flink.kafka

import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.types.DataType
import org.apache.flink.types.Row

class ReadFromKafka extends ConfigurableStop[DataStream[Row]] {

  val description: String = "Read data from kafka"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  var kafka_host: String = _
  var topic: String = _
  var schema: String = _

  def perform(in: JobInputStream[DataStream[Row]],
              out: JobOutputStream[DataStream[Row]],
              pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    //    import org.apache.flink.api.scala._
    //    //checkpoint配置
    //    // 每隔100 ms进行启动一个检查点【设置checkpoint的周期】
    //    env.enableCheckpointing(100)
    //    // 设置模式为exactly-once （这是默认值）
    //    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //    // 确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
    //    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
    //    // 检查点必须在一分钟内完成，或者被丢弃【checkpoint的超时时间】
    //    env.getCheckpointConfig.setCheckpointTimeout(60000)
    //    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //
    //    /*表示一旦Flink处理程序被cancel后，会保留Checkpoint数据，
    //    *以便根据实际需要恢复到指定的Checkpoint
    //    */
    //    env.getCheckpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION)
    //    env.getCheckpointConfig.setCheckpointStorage("hdfs://checkpoints")
    //
    //    //设置state backend
    //    env.setStateBackend(new EmbeddedRocksDBStateBackend())
    //
    //    val kafkaSource: KafkaSource[String] = KafkaSource.builder[String]()
    //      .setBootstrapServers(kafka_host)
    //      .setTopics(topic)
    //      .setGroupId("my-group")
    //      .setStartingOffsets(OffsetsInitializer.earliest())
    //      .setValueOnlyDeserializer(new SimpleStringSchema)
    //      .build()
    //
    //    val stream: DataStream[Row] = env.fromSource(
    //      kafkaSource, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)), "mySource")


    // 定义 Kafka 配置参数
    val kafkaProperties = new java.util.Properties()
    kafkaProperties.setProperty("bootstrap.servers", kafka_host)
    // ...其他连接器和属性设置

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

    val result = tableEnv.sqlQuery(query)

    val rowStream = tableEnv.toDataStream[Row](result, classOf[Row])

    out.write(rowStream)
  }

  def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {

  }

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

  override val authorEmail: String = "liangdchg@163.com"
}
