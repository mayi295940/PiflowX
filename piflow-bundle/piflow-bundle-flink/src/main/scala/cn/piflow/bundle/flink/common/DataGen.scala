package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.TableDescriptor
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class DataGen extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Mock dataframe."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var schema: String = _
  private var count: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
    count = MapUtil.get(map, "count").asInstanceOf[String].toInt
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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/MockData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val tableDescriptor: TableDescriptor = TableDescriptor.forConnector("datagen")
      .option("number-of-rows", "500")
      .option("rows-per-second", "5")
      //.option(DataGenConnectorOptions.ROWS_PER_SECOND, 100L)
      .schema(RowTypeUtil.getRowSchema(schema))
      .build()

    tableEnv.createTemporaryTable("SourceTable", tableDescriptor)

    val resultTable = tableEnv.sqlQuery("SELECT * FROM SourceTable")
    val df = tableEnv.toDataStream(resultTable)

    out.write(df)
  }

}
