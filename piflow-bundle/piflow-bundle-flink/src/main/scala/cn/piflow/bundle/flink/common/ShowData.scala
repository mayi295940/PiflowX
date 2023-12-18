package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class ShowData extends ConfigurableStop[DataStream[Row]] {

  // the email of author
  val authorEmail: String = ""
  // the description of Stop
  val description: String = "Show Data"
  //the inport list of Stop
  val inportList: List[String] = List(Port.DefaultPort)
  //the outport list of Stop
  val outportList: List[String] = List(Port.DefaultPort)

  //the customized properties of your Stop
  private var showNumber: Int = _


  def perform(in: JobInputStream[DataStream[Row]],
              out: JobOutputStream[DataStream[Row]],
              pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val df: DataStream[Row] = in.read()

    val inputTable = tableEnv.fromDataStream(df)

    val resultTable = inputTable.limit(showNumber)

    val resultStream = tableEnv.toDataStream(resultTable)

    // 将 DataStream 结果打印到控制台
    resultStream.print()

    out.write(df)
  }

  def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  //set customized properties of your Stop
  def setProperties(map: Map[String, Any]): Unit = {
    showNumber = MapUtil.get(map, "showNumber").asInstanceOf[String].toInt
  }

  //get descriptor of customized properties
  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .defaultValue("10")
      .required(true)
      .example("10")
    descriptor = showNumber :: descriptor
    descriptor
  }

  // get icon of Stop
  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ShowData.png", this.getClass.getName)
  }

  // get group of Stop
  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

}

