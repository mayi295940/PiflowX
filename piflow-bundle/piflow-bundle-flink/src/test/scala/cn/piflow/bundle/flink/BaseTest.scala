package cn.piflow.bundle.flink

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.JsonUtil
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row
import org.h2.tools.Server

object BaseTest {

  def testFlow(jsonPath: String): Unit = {

    //parse flow json
    val flowJsonStr = FileUtil.fileReader(jsonPath)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    //create flow
    val flowBean = FlowBean.apply[DataStream[Row]](map)
    val flow = flowBean.constructFlow()
    println(flow)

    Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val process = Runner.create[DataStream[Row]]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .bind(classOf[StreamTableEnvironment].getName, tableEnv)
      .bind("checkpoint.path", "")
      .bind("debug.path", "")
      .start(flow)

    process.awaitTermination()
    val pid = process.pid()
    println(pid + "!!!!!!!!!!!!!!!!!!!!!")
  }

}
