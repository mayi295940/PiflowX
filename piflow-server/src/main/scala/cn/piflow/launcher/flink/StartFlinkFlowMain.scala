package cn.piflow.launcher.flink

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.util.{ConfigureUtil, FlowFileUtil, JsonUtil}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

import java.io.File

object StartFlinkFlowMain {

  def main(args: Array[String]): Unit = {

    val flowFileName = args(0)

    var flowFilePath = FlowFileUtil.getFlowFileInUserDir(flowFileName)
    val file = new File(flowFilePath)
    if (!file.exists()) {
      flowFilePath = FlowFileUtil.getFlowFilePath(flowFileName)
    }

    val flowJson = FlowFileUtil.readFlowFile(flowFilePath).trim()
    println(flowJson)

    val map = JsonUtil.jsonToMap(flowJson)
    println(map)

    // create flow
    val flowBean = FlowBean.apply[DataStream[Row]](map)
    val flow = flowBean.constructFlow(false)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    println("StreamExecutionEnvironment is " + env + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    val process = Runner.create[DataStream[Row]]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .bind(classOf[StreamTableEnvironment].getName, tableEnv)
      .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      .bind("debug.path", ConfigureUtil.getDebugPath())
      .bind("environmentVariable", flowBean.environmentVariable)
      .start(flow);

    println("pid is " + process.pid + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    env.execute(flow.getFlowName)
  }

}
