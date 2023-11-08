package cn.piflow.bundle.common

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.state.{StateTtlConfig, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.time.Time
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

import java.time.Duration

class Distinct extends ConfigurableStop {

  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String = "Duplicate based on the specified column name or all colume names"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var columnNames: String = _
  private var watermarkColumn: String = _

  override def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {
    val inDf: DataStream[Row] = in.read().asInstanceOf[DataStream[Row]]
    var outDf: DataStream[Row] = null

    var watermarkStrategy: WatermarkStrategy[Row] = null

    // 水印字段
    if (StringUtils.isNotBlank(watermarkColumn)) {
      watermarkStrategy = WatermarkStrategy
        .forBoundedOutOfOrderness[Row](Duration.ofSeconds(20))
        .withTimestampAssigner(new SerializableTimestampAssigner[Row] {
          override def extractTimestamp(element: Row, recordTimestamp: Long): Long =
            element.getField(watermarkColumn).asInstanceOf[String].toLong
        })
    } else {
      watermarkStrategy = WatermarkStrategy.noWatermarks()
    }

    outDf = inDf.assignTimestampsAndWatermarks(watermarkStrategy)
      .keyBy(row => row.getField(0).asInstanceOf[String])
      .process(new KeyedProcessFunction[String, Row, Row]() {
        // 为每个 key 创建一个私有的状态
        private var state: ValueState[Row] = _
        private val keyName = "DistinctKey"

        override def open(parameters: Configuration): Unit = {
          // 创建一个状态描述器
          val stateDescriptor = new ValueStateDescriptor(keyName, classOf[Row])
          // 设置状态的生存时间，过时销毁，主要是为减少内存
          stateDescriptor.enableTimeToLive(StateTtlConfig.newBuilder(Time.seconds(60)).build)
          // 完成 Keyed State 的创建。
          state = getRuntimeContext.getState(stateDescriptor)
        }

        @throws[Exception]
        override def processElement(in: Row, ctx: KeyedProcessFunction[String, Row, Row]#Context, out: Collector[Row]): Unit = {
          // 从状态中拿出对象
          var cur = state.value
          // 如果为空则为新数据，否则就是重复数据
          if (cur == null) {
            cur = in
            // 记得更新下状态
            state.update(cur)
            if (StringUtils.isNotBlank(watermarkColumn)) {
              // 注册个定时器任务，60 秒后可以不算是新数据
              // 即用户 60 秒点击多次只能算一次有效点击
              ctx.timerService.registerEventTimeTimer(cur.getField(watermarkColumn).asInstanceOf[String].toLong + 60000)
            }

            // 新数据可以向下传递
            out.collect(cur)
          } else {
            println("[Duplicate Data] " + in)
          }
        }

        // 触发定时任务
        @throws[Exception]
        def onTimer(timestamp: Long, ctx: Context, out: Row): Unit = {
          val cur = state.value
          // 利用定时任务将状态清空
          if (cur.getField("timestamp").asInstanceOf[String].toLong + 60000 <= timestamp) {
            state.clear()
          }
        }

      })

    out.write(outDf)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
    watermarkColumn = MapUtil.get(map, "watermarkColumn").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val fields = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description("Fill in the column names you want to duplicate," +
        "multiple columns names separated by commas,if not," +
        "all the columns will be deduplicated")
      .defaultValue("")
      .required(false)
      .example("id")
    descriptor = fields :: descriptor

    val watermarkColumn = new PropertyDescriptor()
      .name("watermarkColumn")
      .displayName("WatermarkColumn")
      .description("watermark column")
      .defaultValue("")
      .required(false)
      .example("id")
    descriptor = watermarkColumn :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }


  override def initialize(ctx: ProcessContext): Unit = {

  }

}
