package cn.piflow.bundle.flink.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.table.api.Table

class IntersectAll extends ConfigurableStop[Table] {

  override val authorEmail: String = ""

  override val description: String = "IntersectAll返回两个表中都存在的记录。" +
    "如果一条记录在两张表中出现多次，那么该记录返回的次数同该记录在两个表中都出现的次数一致，" +
    "也就是说，结果表可能存在重复记录。" +
    "两张表必须具有相同的字段类型。"

  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override def perform(in: JobInputStream[Table],
                       out: JobOutputStream[Table],
                       pec: JobContext[Table]): Unit = {

    val leftTable = in.read(Port.LeftPort)
    val rightTable = in.read(Port.RightPort)

    val resultTable = leftTable.intersectAll(rightTable)
    out.write(resultTable)
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


  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
