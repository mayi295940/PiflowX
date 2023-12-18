package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class MinusAll extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = ""
  override val description: String = "MinusAll 返回右表中不存在的记录。" +
    "在左表中出现 n 次且在右表中出现 m 次的记录，在结果表中出现 (n - m) 次，" +
    "例如，也就是说结果中删掉了在右表中存在重复记录的条数的记录。两张表必须具有相同的字段类型。"

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
    val subtractTable = leftTable.minusAll(rightTable)

    val subtractStream = tableEnv.toDataStream(subtractTable)

    out.write(subtractStream)
  }
}
