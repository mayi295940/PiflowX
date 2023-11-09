package cn.piflow.bundle.common

import cn.piflow.bundle.source.mock.MockSourceFunction
import cn.piflow.bundle.util.RowTypeUtil
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object MockDataTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val count: Int = 10

    val schema: String = "id:String,name:string,age:int,salary:double,time:date"

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new MockSourceFunction(rowTypeInfo, count))(rowTypeInfo)

    df.print()

    env.execute()

  }


}
