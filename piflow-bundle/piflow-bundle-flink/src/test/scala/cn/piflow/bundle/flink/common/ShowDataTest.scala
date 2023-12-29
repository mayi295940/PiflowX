package cn.piflow.bundle.flink.common

import cn.piflow.Constants
import cn.piflow.bundle.flink.source.mock.MockSourceFunction
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.util.IdGenerator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

object ShowDataTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val schema: String = "id:String,name:string,age:int,salary:double,time:date"

    val rowTypeInfo = RowTypeUtil.getRowTypeInfo(schema)

    val df = env.addSource(new MockSourceFunction(rowTypeInfo))
    df.print()

    val showNumber = 5

    val inputTable = tableEnv.fromDataStream(df)

    val tmpTable = this.getClass.getSimpleName.stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    tableEnv.createTemporaryView(tmpTable, inputTable)

    val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable LIMIT $showNumber")

    val resultStream = tableEnv.toDataStream(resultTable)

    // 将 DataStream 结果打印到控制台
    resultStream.print()

    env.execute

  }

}
