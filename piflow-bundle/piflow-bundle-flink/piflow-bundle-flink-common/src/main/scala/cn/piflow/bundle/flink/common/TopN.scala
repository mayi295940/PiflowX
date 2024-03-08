package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class TopN extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "按列排序的N个最小值或最大值"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var column_list: String = _
  private var partition_list: String = _
  private var order_list: String = _
  private var tableName: String = _
  private var topNum: Int = _
  private var conditions: String = _
  private var isWindow: Boolean = _

  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    var partitionExp = ""
    if (isWindow) {
      partitionExp = "window_start, window_end"
      if (partition_list.nonEmpty) {
        partitionExp = partitionExp + Constants.COMMA + partition_list
      }
    } else {
      partitionExp = partition_list
    }

    val columnExp = if (column_list.nonEmpty) column_list else "*"

    val conditionsExp = if (conditions.isEmpty) "" else " AND " + conditions

    val orderList: Array[String] = order_list.split(Constants.COMMA)
      .map(x => x.split(Constants.ARROW_SIGN)
        .mkString(Constants.SPACE))

    val sql =
      s""" SELECT $columnExp
         |FROM (
         |   SELECT $columnExp,
         |     ROW_NUMBER() OVER (PARTITION BY $partitionExp
         |       ORDER BY ${orderList.mkString(Constants.COMMA)}) AS rownum
         |   FROM $tableName)
         |WHERE rownum <= $topNum
         | $conditionsExp
         |"""
        .stripMargin
        .replaceAll("\r\n", Constants.SPACE)
        .replaceAll(Constants.LINE_SPLIT_N, Constants.SPACE)

    println(sql)

    val resultTable = tableEnv.sqlQuery(sql)
    out.write(resultTable)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    column_list = MapUtil.get(map, "column_list").asInstanceOf[String]
    partition_list = MapUtil.get(map, "partition_list").asInstanceOf[String]
    order_list = MapUtil.get(map, "order_list").asInstanceOf[String]
    tableName = MapUtil.get(map, "tableName").asInstanceOf[String]
    topNum = MapUtil.get(map, "topNum", "10").asInstanceOf[String].toInt
    conditions = MapUtil.get(map, "conditions").asInstanceOf[String]
    isWindow = MapUtil.get(map, "isWindow", "false").asInstanceOf[String].toBoolean
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val column_list = new PropertyDescriptor()
      .name("column_list")
      .displayName("column_list")
      .description("查询字段")
      .defaultValue("*")
      .required(true)
      .language(Language.Text)
      .example("name,age")

    descriptor = column_list :: descriptor

    val partition_list = new PropertyDescriptor()
      .name("partition_list")
      .displayName("partition_list")
      .description("分区字段")
      .defaultValue("")
      .required(false)
      .language(Language.Text)
      .example("name,age")

    descriptor = partition_list :: descriptor

    val order_list = new PropertyDescriptor()
      .name("order_list")
      .displayName("order_list")
      .description("排序字段")
      .defaultValue("")
      .required(true)
      .language(Language.Text)
      .example("name->asc,age-desc")

    descriptor = order_list :: descriptor

    val tableName = new PropertyDescriptor()
      .name("tableName")
      .displayName("tableName")
      .description("表名")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = tableName :: descriptor

    val topNum = new PropertyDescriptor()
      .name("topNum")
      .displayName("topNum")
      .description("TopN的条⽬数")
      .defaultValue("10")
      .required(true)
      .dataType(Int.toString())
      .example("10")
    descriptor = topNum :: descriptor

    val conditions = new PropertyDescriptor()
      .name("conditions")
      .displayName("conditions")
      .description("查询条件")
      .required(false)
      .defaultValue("")
      //.example("age > 10 and name = 'test'")
    descriptor = conditions :: descriptor

    val isWindow = new PropertyDescriptor()
      .name("isWindow")
      .displayName("isWindow")
      .description("是否窗口TopN")
      .defaultValue("false")
      .allowableValues(Set("ture", "false"))
      .required(false)
      .example("false")
    descriptor = isWindow :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}