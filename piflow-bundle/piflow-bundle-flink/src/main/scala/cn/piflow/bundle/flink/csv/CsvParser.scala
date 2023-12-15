package cn.piflow.bundle.flink.csv

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class CsvParser extends ConfigurableStop[DataStream[Row]] {

  val authorEmail: String = ""
  val description: String = "Parse csv file or folder"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var csvPath: String = _
  var header: Boolean = _
  var delimiter: String = _
  var schema: String = _

  def perform(in: JobInputStream[DataStream[Row]],
              out: JobOutputStream[DataStream[Row]],
              pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val columns = RowTypeUtil.getTableSchema(schema)

    val tmpTable = "CsvParser_" + IdGenerator.uuidWithoutSplit

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'filesystem',
         |'format' = 'csv',
         |'path' = '$csvPath',
         |'csv.field-delimiter' = '$delimiter',
         |'csv.ignore-parse-errors' = 'true'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll("\n", " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    val resultStream = tableEnv.toDataStream(resultTable)
    out.write(resultStream)


    //    val env = pec.get[StreamExecutionEnvironment]()
    //
    //    val csvFormat = CsvReaderFormat.forSchema(
    //      CsvSchema.builder
    //        .addColumn(new CsvSchema.Column(0, "name", CsvSchema.ColumnType.STRING))
    //        .addColumn(new CsvSchema.Column(1, "age", CsvSchema.ColumnType.NUMBER)
    //          .withArrayElementSeparator("#"))
    //        //        .addColumn(new CsvSchema.Column(4, "array", CsvSchema.ColumnType.ARRAY)
    //        //          .withArrayElementSeparator("#"))
    //        .setSkipFirstDataRow(header)
    //        .build,
    //      RowTypeUtil.getRowTypeInfo(schema))
    //
    //    val csvTableSource: FileSource[Row] =
    //      FileSource.forRecordStreamFormat(csvFormat, Path.fromLocalFile(new File(csvPath)))
    //        //.monitorContinuously(Duration.ofMillis(5))
    //        .build()
    //
    //    val df = env.fromSource(csvTableSource, WatermarkStrategy.noWatermarks[Row](), "csvTableSource")
    //    df.print()
    //    out.write(df)

  }

  def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  def setProperties(map: Map[String, Any]): Unit = {
    csvPath = MapUtil.get(map, "csvPath").asInstanceOf[String]
    header = MapUtil.get(map, "header").asInstanceOf[String].toBoolean
    delimiter = MapUtil.get(map, "delimiter").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val csvPath = new PropertyDescriptor()
      .name("csvPath")
      .displayName("CsvPath")
      .description("The path of csv file or folder")
      .defaultValue("")
      .required(true)
      .example("hdfs://127.0.0.1:9000/test/")
    descriptor = csvPath :: descriptor

    val header = new PropertyDescriptor()
      .name("header")
      .displayName("Header")
      .description("Whether the csv file has a header")
      .defaultValue("false")
      .allowableValues(Set("true", "false"))
      .required(true)
      .example("true")
    descriptor = header :: descriptor

    val delimiter = new PropertyDescriptor()
      .name("delimiter")
      .displayName("Delimiter")
      .description("The delimiter of csv file")
      .defaultValue("")
      .required(true)
      .example(",")
    descriptor = delimiter :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of csv file")
      .defaultValue("")
      .required(false)
      //.example("id,name,gender,age")
      .example("name:string,age:int")
    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CsvGroup)
  }

}

