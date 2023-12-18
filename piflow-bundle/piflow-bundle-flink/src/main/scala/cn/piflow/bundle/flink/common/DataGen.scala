package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.api.{Table, TableDescriptor}

class DataGen extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Mock dataframe."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: String = _
  private var count: Int = _
  private var ratio: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
    count = MapUtil.get(map, "count").asInstanceOf[String].toInt
    ratio = MapUtil.get(map, "ratio").asInstanceOf[String].toInt
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of mock data, format is column:columnType:isNullable. " +
        "Separate multiple fields with commas. " +
        "columnType can be String/Int/Long/Float/Double/Boolean. " +
        "isNullable can be left blank, the default value is false. ")
      .defaultValue("")
      .required(true)
      .example("id:String,name:String,age:Int")
    descriptor = schema :: descriptor

    val count = new PropertyDescriptor()
      .name("count")
      .displayName("Count")
      .description("The count of dataframe")
      .defaultValue("")
      .required(true)
      .example("10")
    descriptor = count :: descriptor

    val ratio = new PropertyDescriptor()
      .name("ratio")
      .displayName("Ratio")
      .description("rows per second")
      .defaultValue("1")
      .required(false)
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

    val tableDescriptor: TableDescriptor = TableDescriptor.forConnector("datagen")
      .option("number-of-rows", count.toString)
      .option("rows-per-second", ratio.toString)
      //.option(DataGenConnectorOptions.ROWS_PER_SECOND, 100L)
      .schema(RowTypeUtil.getRowSchema(schema))
      .build()


    val tmpTable = "SourceTable_" + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryTable(tmpTable, tableDescriptor)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable")

    out.write(resultTable)
  }

}
