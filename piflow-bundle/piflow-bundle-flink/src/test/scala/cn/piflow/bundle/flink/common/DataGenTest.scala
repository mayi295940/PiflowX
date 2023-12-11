package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.util.RowTypeUtil
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.TableDescriptor
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

object DataGenTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val count: Int = 10

    val schema: String = "id:String,name:string,age:int,salary:double,time:date"

    val tableDescriptor: TableDescriptor = TableDescriptor.forConnector("datagen")
      .option("number-of-rows", count.toString)
      .option("rows-per-second", "5")
      //.option(DataGenConnectorOptions.ROWS_PER_SECOND, count.toLong)
      .schema(RowTypeUtil.getRowSchema(schema))
      .build()

    tableEnv.createTemporaryTable("SourceTable", tableDescriptor)

    val resultTable = tableEnv.sqlQuery("SELECT * FROM SourceTable")
    val df = tableEnv.toDataStream(resultTable)

    df.print()

    env.execute()

  }

}
