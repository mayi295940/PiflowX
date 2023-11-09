package cn.piflow.api

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.util.{FlowFileUtil, JsonUtil}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

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

    //create flow
    val flowBean = FlowBean(map)
    val flow = flowBean.constructFlow(false)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    println("StreamExecutionEnvironment is " + env + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    val process = Runner.create()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .start(flow);

    env.execute(flow.getFlowName())

  }

}
