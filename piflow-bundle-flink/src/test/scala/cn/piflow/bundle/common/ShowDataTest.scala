package cn.piflow.bundle.common

import cn.piflow.bundle.source.mock.MockSourceFunction
import cn.piflow.bundle.util.RowTypeUtil
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.types.Row
import org.apache.flink.util.Collector

object ShowDataTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val schema: String = "id:String,name:string,age:int,salary:double,time:date"

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new MockSourceFunction(rowTypeInfo))(rowTypeInfo)

    val showNumber = 5

    val showDf = df.keyBy(row => row.getField(0).asInstanceOf[String])
      .process(new KeyedProcessFunction[String, Row, Row]() {

        private var showNumberState: ValueState[Integer] = _

        override def open(parameters: Configuration): Unit = {
          super.open(parameters)
          showNumberState = getRuntimeContext.getState(new ValueStateDescriptor[Integer]("showNumberState", Types.INT))
        }

        @throws[Exception]
        override def processElement(value: Row, ctx: KeyedProcessFunction[String, Row, Row]#Context, out: Collector[Row]): Unit = {
          var showNumberStateValue = showNumberState.value()
          if (showNumberStateValue == null) {
            showNumberStateValue = 0;
          }
          println("showNumberStateValue = " + showNumberStateValue)
          if (showNumberStateValue < showNumber) {
            out.collect(value)
          }
          showNumberStateValue = showNumberStateValue + 1;
          showNumberState.update(showNumberStateValue)
          println("update showNumberStateValue = " + showNumberState.value())
        }
      })

    showDf.print()

    env.execute

  }

}
