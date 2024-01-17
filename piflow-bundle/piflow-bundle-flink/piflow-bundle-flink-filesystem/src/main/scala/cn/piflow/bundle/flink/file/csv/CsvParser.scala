package cn.piflow.bundle.flink.file.csv

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

// todo 暂不支持跳过首行
class CsvParser extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Parse csv file or folder"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var csvPath: String = _
  var header: Boolean = _
  var delimiter: String = _
  var schema: String = _

  def perform(in: JobInputStream[Table],
              out: JobOutputStream[Table],
              pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val columns = RowTypeUtil.getTableSchema(schema)

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

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
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

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

  override def getEngineType: String = Constants.ENGIN_FLINK

}

