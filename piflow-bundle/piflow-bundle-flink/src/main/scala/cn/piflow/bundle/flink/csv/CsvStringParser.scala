package cn.piflow.bundle.flink.csv

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.util.DateUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class CsvStringParser extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  override val description: String = "Parse csv string"

  var string: String = _
  var delimiter: String = _
  var schema: String = _

  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val rowType = RowTypeUtil.getRowTypeInfo(schema)
    val colNum: Int = rowType.getArity

    val arrStr: Array[String] = string.split(Constants.LINE_SPLIT_N).map(x => x.trim)

    val listROW: List[Row] = arrStr.map(line => {

      val seqSTR: Seq[String] = line.split(delimiter).map(x => x.trim).toSeq

      // todo time format

      val row = new Row(colNum)
      for (i <- 0 until colNum) {

        val colType = rowType.getTypeAt(i).getTypeClass.getSimpleName.toLowerCase()
        colType match {
          case "string" => row.setField(i, seqSTR(i))
          case "integer" => row.setField(i, seqSTR(i).toInt)
          case "long" => row.setField(i, seqSTR(i).toLong)
          case "double" => row.setField(i, seqSTR(i).toDouble)
          case "float" => row.setField(i, seqSTR(i).toFloat)
          case "boolean" => row.setField(i, seqSTR(i).toBoolean)
          case "date" => row.setField(i, DateUtils.strToDate(seqSTR(i)))
          case "timestamp" => row.setField(i, DateUtils.strToSqlTimestamp(seqSTR(i)))
          case _ => row.setField(i, seqSTR(i))
        }
      }

      row
    }).toList

    out.write(tableEnv.fromValues(rowType, listROW))
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    string = MapUtil.get(map, "string").asInstanceOf[String]
    delimiter = MapUtil.get(map, "delimiter").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val string = new PropertyDescriptor()
      .name("string")
      .displayName("String")
      .defaultValue("")
      .required(true)
      .example("1,zs\n2,ls\n3,ww")
    descriptor = string :: descriptor

    val delimiter = new PropertyDescriptor()
      .name("delimiter")
      .displayName("Delimiter")
      .description("The delimiter of CSV string")
      .defaultValue("")
      .required(true)
      .example(",")
    descriptor = delimiter :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of CSV string")
      .defaultValue("")
      .required(false)
      .example("")
    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvStringParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CsvGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

}
