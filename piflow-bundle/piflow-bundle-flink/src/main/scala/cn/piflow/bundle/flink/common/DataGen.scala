package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.util.IdGenerator
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import scala.collection.mutable.{Map => MMap}

class DataGen extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Mock dataframe."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: List[Map[String, Any]] = _
  private var count: Int = _
  private var ratio: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[List[Map[String, Any]]]
    count = MapUtil.get(map, "count").asInstanceOf[String].toInt
    ratio = MapUtil.get(map, "ratio").asInstanceOf[String].toInt
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("数据生成规则")
      .defaultValue("")
      .required(true)
      .language(Language.DataGenSchema)
      .example("[{\"filedName\":\"id\",\"filedType\":\"INT\",\"kind\":\"sequence\",\"start\":1,\"end\":10000}," +
        "{\"filedName\":\"name\",\"filedType\":\"STRING\",\"kind\":\"random\",\"length\":15}," +
        "{\"filedName\":\"age\",\"filedType\":\"INT\",\"kind\":\"random\",\"max\":100,\"min\":1}," +
        "{\"filedName\":\"timeField\",\"filedType\":\"AS PROCTIME()\"}]")

    descriptor = schema :: descriptor

    val count = new PropertyDescriptor()
      .name("count")
      .displayName("Count")
      .description("The count of dataframe")
      .defaultValue("")
      .required(true)
      .dataType(Int.toString())
      .example("10")
    descriptor = count :: descriptor

    val ratio = new PropertyDescriptor()
      .name("ratio")
      .displayName("Ratio")
      .description("rows per second")
      .defaultValue("1")
      .required(false)
      .dataType(Int.toString())
      .example("10")
    descriptor = ratio :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}


  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (columns, conf) = getWithColumnsAndConf(schema)

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    // 生成数据源 DDL 语句
    val sourceDDL =
      s""" CREATE TABLE $tmpTable ($columns) WITH (
         |'connector' = 'datagen',
         | $conf
         | 'number-of-rows'='$count',
         | 'rows-per-second'='$ratio'
         |)"""
        .stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(sourceDDL)

    tableEnv.executeSql(sourceDDL)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")
    out.write(resultTable)

    out.write(resultTable)
  }


  private def getWithColumnsAndConf(schema: List[Map[String, Any]]): (String, String) = {
    var columns = List[String]()
    var conf = List[String]()

    schema.foreach(item => {

      val filedMap = MMap(item.toSeq: _*)

      val filedName = MapUtil.get(filedMap, "filedName").toString
      val filedType = MapUtil.get(filedMap, "filedType").toString
      columns = columns :+ s"$filedName $filedType,"

      val kind = filedMap.getOrElse("kind", "").toString
      if (StringUtils.isNotBlank(kind)) {
        conf = s"'fields.$filedName.kind' = '$kind'," :: conf
      }

      val min = filedMap.getOrElse("min", "").toString
      if (StringUtils.isNotBlank(min)) {
        conf = s"'fields.$filedName.min' = '$min'," :: conf
      }

      val max = filedMap.getOrElse("max", "").toString
      if (StringUtils.isNotBlank(max)) {
        conf = s"'fields.$filedName.max' = '$max'," :: conf
      }

      val length = filedMap.getOrElse("length", "").toString
      if (StringUtils.isNotBlank(length)) {
        conf = s"'fields.$filedName.length' = '$length'," :: conf
      }

      val start = filedMap.getOrElse("start", "").toString
      if (StringUtils.isNotBlank(start)) {
        conf = s"'fields.$filedName.start' = '$start'," :: conf
      }

      val end = filedMap.getOrElse("end", "").toString
      if (StringUtils.isNotBlank(end)) {
        conf = s"'fields.$filedName.end' = '$end'," :: conf
      }

      val maxPast = filedMap.getOrElse("maxPast", "").toString
      if (StringUtils.isNotBlank(maxPast)) {
        conf = s"'fields.$filedName.max-past' = '$maxPast'," :: conf
      }

    })

    (columns.mkString("").trim.dropRight(1), conf.mkString(""))
  }


  override def getEngineType: String = Constants.ENGIN_FLINK

}
