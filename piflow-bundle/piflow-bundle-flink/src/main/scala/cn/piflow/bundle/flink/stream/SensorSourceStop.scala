package cn.piflow.bundle.flink.stream

import cn.piflow.bundle.flink.source.sensor.{SensorReading, SensorSource, SensorTimeAssigner}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.types.Row

class SensorSourceStop extends ConfigurableStop[DataStream[Row]] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Sensor reading"
  override val inportList: List[String] = List()
  override val outportList: List[String] = List(Port.DefaultPort)

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    List()
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/SelectHiveQL.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def initialize(ctx: ProcessContext[DataStream[Row]]): Unit = {}

  override def perform(in: JobInputStream[DataStream[Row]],
                       out: JobOutputStream[DataStream[Row]],
                       pec: JobContext[DataStream[Row]]): Unit = {

    //val env = pec.get[StreamExecutionEnvironment]()
    //val sensorData: DataStream[SensorReading] = env.addSource(new SensorSource)
    // .assignTimestampsAndWatermarks(new SensorTimeAssigner)
    //out.write(sensorData)

  }
}
