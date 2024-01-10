package cn.piflow.bundle.flink.file.json

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.util.DateUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class JsonStringParser extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  override val description: String = "Parse json string"

  var content: List[Map[String, String]] = _
  var schema: String = _

  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (rowType, fieldNames: Array[String]) = RowTypeUtil.getDataType(schema)

    val children = rowType.getChildren

    val colNum: Int = children.size()

    val listROW: List[Row] = content.map(lineMap => {

      // todo time format

      val row = new Row(colNum)
      for (i <- 0 until colNum) {

        val colType = children.get(i).getConversionClass.getSimpleName.toLowerCase()
        colType match {
          case "string" => row.setField(i, lineMap(fieldNames(i)))
          case "integer" => row.setField(i, lineMap(fieldNames(i)).toInt)
          case "long" => row.setField(i, lineMap(fieldNames(i)).toLong)
          case "double" => row.setField(i, lineMap(fieldNames(i)).toDouble)
          case "float" => row.setField(i, lineMap(fieldNames(i)).toFloat)
          case "boolean" => row.setField(i, lineMap(fieldNames(i)).toBoolean)
          case "date" => row.setField(i, DateUtils.strToDate(lineMap(fieldNames(i))))
          case "timestamp" => row.setField(i, DateUtils.strToSqlTimestamp(lineMap(fieldNames(i))))
          case _ => row.setField(i, lineMap(fieldNames(i)))
        }
      }

      row
    })

    out.write(tableEnv.fromValues(rowType, listROW: _*))
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    content = MapUtil.get(map, "content").asInstanceOf[List[Map[String, String]]]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val content = new PropertyDescriptor()
      .name("content")
      .displayName("content")
      .defaultValue("")
      .required(true)
      .example("{\"id\":\"13\",\"name\":\"13\",\"score\":\"13\",\"school\":\"13\",\"class\":\"13\"}")
    descriptor = content :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of json string")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/json/JsonStringParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JsonGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
