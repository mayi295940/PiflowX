package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class Intersect extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = ""

  override val description: String = "Intersect返回两个表中都存在的记录。" +
    "如果一条记录在一张或两张表中存在多次，则只返回一条记录，也就是说结果表中不存在重复的记录。" +
    "两张表必须具有相同的字段类型。"

  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val leftDF = in.read(Port.LeftPort)
    val rightDF = in.read(Port.RightPort)

    val leftTable = tableEnv.fromDataStream(leftDF)
    val rightTable = tableEnv.fromDataStream(rightDF)

    val resultTable = leftTable.intersect(rightTable)
    val resultStream = tableEnv.toDataStream(resultTable)
    out.write(resultStream)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    val descriptor: List[PropertyDescriptor] = List()
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }


  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

}
