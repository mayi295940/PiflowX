package cn.piflow.bundle.flink.es7

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.JsonUtil
import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.{CheckpointConfig, StreamExecutionEnvironment}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

object BaseTest {

  def testFlow(jsonPath: String,
               mode: RuntimeExecutionMode = RuntimeExecutionMode.STREAMING): Unit = {

    //parse flow json
    val flowJsonStr = FileUtil.fileReader(jsonPath)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    //create flow
    val flowBean = FlowBean.apply[Table](map)
    val flow = flowBean.constructFlow()
    println(flow)

    // Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setRuntimeMode(mode)
    env.setParallelism(1)
    // 每20秒作为checkpoint的一个周期
    env.enableCheckpointing(20000);
    // 两次checkpoint间隔最少是10秒
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(10000);
    // 程序取消或者停止时不删除checkpoint
    env.getCheckpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    // checkpoint必须在60秒结束,否则将丢弃
    env.getCheckpointConfig.setCheckpointTimeout(60000);
    // 同一时间只能有一个checkpoint
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1);
    // 设置EXACTLY_ONCE语义,默认就是这个
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    // checkpoint存储位置
    env.getCheckpointConfig.setCheckpointStorage("file:///src/test/resources/checkpoint");
    // 设置执行模型为Streaming方式
    env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

    val tableEnv = StreamTableEnvironment.create(env)

    val process = Runner.create[Table]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .bind(classOf[StreamTableEnvironment].getName, tableEnv)
      .bind("checkpoint.path", "")
      .bind("debug.path", "")
      .start(flow)

    env.execute(flow.getFlowName)

    val pid = process.pid()
    println(pid + "!!!!!!!!!!!!!!!!!!!!!")

    //process.awaitTermination()
  }

}
