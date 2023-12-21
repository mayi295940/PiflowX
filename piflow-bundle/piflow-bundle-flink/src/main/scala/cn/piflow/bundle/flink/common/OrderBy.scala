package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.api.{ApiExpression, Table}

class OrderBy extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "返回跨所有并行分区的全局有序记录"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var expression: String = _
  private var offset: String = _
  private var fetch: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    expression = MapUtil.get(map, "expression").asInstanceOf[String]
    offset = MapUtil.get(map, "offset").asInstanceOf[String]
    fetch = MapUtil.get(map, "fetch").asInstanceOf[String]
  }

  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()
    var resultTable = inputTable

    if (StringUtils.isNotEmpty(expression)) {
      val fields = expression.split(Constants.COMMA).map(x => x.trim)
      val array = new Array[ApiExpression](fields.length)
      for (x <- fields.indices) {
        val orderExpression: Array[String] = fields(x).split(Constants.ARROW_SIGN).map(x => x.trim)
        orderExpression(1).toLowerCase match {
          case "desc" => array(x) = $(orderExpression(0)).desc()
          case "asc" => array(x) = $(orderExpression(0)).asc()
        }
      }
      resultTable = resultTable.orderBy(array: _*)
    }

    if (StringUtils.isNotEmpty(offset) && StringUtils.isNumeric(offset)) {
      resultTable = resultTable.offset(offset.toInt)
    }

    if (StringUtils.isNotEmpty(fetch) && StringUtils.isNumeric(fetch)) {
      resultTable = resultTable.fetch(fetch.toInt)
    }

    out.write(resultTable)

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val condition = new PropertyDescriptor().name("expression").
      displayName("Expression")
      .description("The expression you want to order")
      .defaultValue("")
      .required(false)
      .example("name->desc,age->asc")
    descriptor = condition :: descriptor

    val offset = new PropertyDescriptor().name("offset").
      displayName("offset")
      .description("Offset 操作根据偏移位置来限定（可能是已排序的）结果集。")
      .defaultValue("")
      .required(false)
      .example("10")
    descriptor = offset :: descriptor

    val fetch = new PropertyDescriptor().name("fetch").
      displayName("fetch")
      .description("Fetch 操作将（可能已排序的）结果集限制为前 n 行。")
      .defaultValue("")
      .required(false)
      .example("10")
    descriptor = fetch :: descriptor

    descriptor

  }

  override def getIcon(): Array[Byte] = {
    // todo 图片
    ImageUtil.getImage("icon/common/SelectField.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
