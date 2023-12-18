package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class Minus extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = ""
  override val description: String = "Minus 返回左表中存在且右表中不存在的记录。" +
    "左表中的重复记录只返回一次，换句话说，结果表中没有重复记录。" +
    "两张表必须具有相同的字段类型。"

  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    val descriptor: List[PropertyDescriptor] = List()

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Subtract.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val leftDF: DataStream[Row] = in.read(Port.LeftPort)
    val rightDF: DataStream[Row] = in.read(Port.RightPort)

    val leftTable = tableEnv.fromDataStream(leftDF)
    val rightTable = tableEnv.fromDataStream(rightDF)

    // todo : The MINUS operation on two unbounded tables is currently not supported.
    val subtractTable = leftTable.minus(rightTable)

    val subtractStream = tableEnv.toDataStream(subtractTable)

    out.write(subtractStream)
  }
}