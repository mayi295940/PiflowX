package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class CsvSave extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "Save the data as a csv file."
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var csvSavePath: String = _
  private var header: Boolean = _
  private var delimiter: String = _
  private var partition: String = _
  private var saveMode: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    csvSavePath = MapUtil.get(map, "csvSavePath").asInstanceOf[String]
    header = MapUtil.get(map, "header").asInstanceOf[String].toBoolean
    delimiter = MapUtil.get(map, "delimiter").asInstanceOf[String]
    partition = MapUtil.get(map, key = "partition").asInstanceOf[String]
    saveMode = MapUtil.get(map, "saveMode").asInstanceOf[String]
  }

  // todo partition > 0 文件合并
  // todo  文件名如何定义？
  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {


    val tableEnv = pec.get[StreamTableEnvironment]()

    val df: DataStream[Row] = in.read()
    val inputTable = tableEnv.fromDataStream(df)
    val inputTmpTable = "InputTmpTable" + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(inputTmpTable, inputTable)


    val columns = RowTypeUtil.getTableSchema(inputTable)
    val tmpTable = "CsvSave_" + IdGenerator.uuidWithoutSplit

    val conf = getWithConf(delimiter, partition)

    val sinkDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'filesystem',
         |'format' = 'csv',
         |'path' = '$csvSavePath',
         | $conf
         |'csv.ignore-parse-errors' = 'true'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sinkDDL)
    tableEnv.executeSql(sinkDDL)

    tableEnv.executeSql(s"INSERT INTO $tmpTable SELECT * FROM $inputTmpTable")

    //    if ("row".equals(formatMode)) {
    //
    //      val rowFormatSink: FileSink[Row] = FileSink
    //        .forRowFormat(new Path(csvSavePath), new FileSystemTableSink.ProjectionEncoder[Row]("UTF-8"))
    //        .withRollingPolicy(
    //          DefaultRollingPolicy.builder()
    //            .withRolloverInterval(Duration.ofMinutes(15))
    //            .withInactivityInterval(Duration.ofMinutes(5))
    //            .withMaxPartSize(MemorySize.ofMebiBytes(1024))
    //            .build())
    //        .withOutputFileConfig(
    //          OutputFileConfig
    //            .builder
    //            .withPartSuffix(".csv")
    //            .build)
    //        .build()
    //
    //      df.sinkTo(rowFormatSink)
    //    } else {
    //
    //      val dataType: RowTypeInfo = df.getType.asInstanceOf[RowTypeInfo]
    //
    //      val avroSchema = AvroSchema.create(AvroSchema.Type.INT)
    //
    //      val bulkFormatSink: FileSink[Row] = FileSink
    //        .forBulkFormat(new Path(csvSavePath), AvroParquetWriters.forReflectRecord(classOf[Row]))
    //        .withBucketAssigner(new DateTimeBucketAssigner())
    //        .withBucketCheckInterval(5)
    //        // bulk模式下的文件滚动策略，只有一种： 当 checkpoint发生时，进行文件滚动
    //        .withRollingPolicy(OnCheckpointRollingPolicy.build())
    //        .withOutputFileConfig(
    //          OutputFileConfig
    //            .builder
    //            .withPartSuffix(".csv")
    //            .build)
    //        .build
    //
    //      if ("".equals(partition)) {
    //        df.sinkTo(bulkFormatSink)
    //      } else {
    //        df.sinkTo(bulkFormatSink)
    //      }
    //    }
  }

  private def getWithConf(delimiter: String, partition: String): String = {
    var result = List[String]()

    if (StringUtils.isNotBlank(delimiter)) {
      result = s"'csv.field-delimiter' = '$delimiter'," :: result
    }

    if (StringUtils.isNotBlank(partition)) {
      val partitionNum = partition.toInt
      if (partitionNum < 0) {
        throw new IllegalArgumentException("partition number must be greater than 0")
      }

      result = s"'sink.parallelism' = '$partition'," :: result
      result = s"'auto-compaction' = 'true'," :: result
      // todo result = s"'compaction.file-size' = '1mb'," :: result
    }

    result.mkString("")
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    val saveModeOption = Set("append", "overwrite", "error", "ignore")

    var descriptor: List[PropertyDescriptor] = List()

    val csvSavePath = new PropertyDescriptor()
      .name("csvSavePath")
      .displayName("CsvSavePath")
      .description("The save path of csv file")
      .defaultValue("")
      .required(true)
      .example("hdfs://127.0.0.1:9000/test/")
    descriptor = csvSavePath :: descriptor

    val header = new PropertyDescriptor()
      .name("header")
      .displayName("Header")
      .description("Whether the csv file has a header")
      .allowableValues(Set("true", "false"))
      .defaultValue("false")
      .required(true)
      .example("false")
    descriptor = header :: descriptor

    val delimiter = new PropertyDescriptor()
      .name("delimiter")
      .displayName("Delimiter")
      .description("The delimiter of csv file")
      .defaultValue(",")
      .required(true)
      .example(",")
    descriptor = delimiter :: descriptor

    val partition = new PropertyDescriptor()
      .name("partition")
      .displayName("Partition")
      .description("The partition of csv file,you can specify the number of partitions saved as csv or not")
      .defaultValue("")
      .required(false)
      .example("3")
    descriptor = partition :: descriptor

    val saveMode = new PropertyDescriptor()
      .name("saveMode")
      .displayName("SaveMode")
      .description("The save mode for csv file")
      .allowableValues(saveModeOption)
      .defaultValue("append")
      .required(true)
      .example("append")
    descriptor = saveMode :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvSave.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CsvGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

}

