package cn.piflow.api

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

object HTTPClientStartFlowIncremental {

  def main(args: Array[String]): Unit = {
    //val json:String = """{"flow":{"name":"Flow","uuid":"1234","stops":[{"uuid":"1111","name":"XmlParser","bundle":"cn.piflow.bundle.xml.XmlParser","properties":{"xmlpath":"hdfs://10.0.86.89:9000/xjzhu/dblp.mini.xml","rowTag":"phdthesis"}},{"uuid":"2222","name":"SelectField","bundle":"cn.piflow.bundle.common.SelectField","properties":{"schema":"title,author,pages"}},{"uuid":"3333","name":"PutHiveStreaming","bundle":"cn.piflow.bundle.hive.PutHiveStreaming","properties":{"database":"sparktest","table":"dblp_phdthesis"}},{"uuid":"4444","name":"CsvParser","bundle":"cn.piflow.bundle.csv.CsvParser","properties":{"csvPath":"hdfs://10.0.86.89:9000/xjzhu/phdthesis.csv","header":"false","delimiter":",","schema":"title,author,pages"}},{"uuid":"555","name":"Merge","bundle":"cn.piflow.bundle.common.Merge","properties":{}},{"uuid":"666","name":"Fork","bundle":"cn.piflow.bundle.common.Fork","properties":{"outports":["out1","out2","out3"]}},{"uuid":"777","name":"JsonSave","bundle":"cn.piflow.bundle.json.JsonSave","properties":{"jsonSavePath":"hdfs://10.0.86.89:9000/xjzhu/phdthesis.json"}},{"uuid":"888","name":"CsvSave","bundle":"cn.piflow.bundle.csv.CsvSave","properties":{"csvSavePath":"hdfs://10.0.86.89:9000/xjzhu/phdthesis_result.csv","header":"true","delimiter":","}}],"paths":[{"from":"XmlParser","outport":"","inport":"","to":"SelectField"},{"from":"SelectField","outport":"","inport":"data1","to":"Merge"},{"from":"CsvParser","outport":"","inport":"data2","to":"Merge"},{"from":"Merge","outport":"","inport":"","to":"Fork"},{"from":"Fork","outport":"out1","inport":"","to":"PutHiveStreaming"},{"from":"Fork","outport":"out2","inport":"","to":"JsonSave"},{"from":"Fork","outport":"out3","inport":"","to":"CsvSave"}]}}"""
    val json =
      """
        |{
        |  "flow":{
        |    "name":"incremental",
        |    "uuid":"1234",
        |    "stops":[
        |      {
        |        "uuid":"1111",
        |        "name":"MysqlReadIncremental",
        |        "bundle":"cn.piflow.bundle.jdbc.MysqlReadIncremental",
        |        "properties":{
        |            "url":"jdbc:mysql://10.0.86.90:3306/sparktest",
        |            "sql":"select * from student where id > #~#",
        |            "user":"root",
        |            "password":"root",
        |            "incrementalField":"id"
        |        }
        |      },
        |      {
        |        "uuid":"888",
        |        "name":"CsvSave",
        |        "bundle":"cn.piflow.bundle.csv.CsvSave",
        |        "properties":{
        |          "csvSavePath":"hdfs://10.0.86.89:9000/xjzhu/phdthesis_result.csv",
        |          "header":"true",
        |          "delimiter":","
        |        }
        |      }
        |    ],
        |    "paths":[
        |      {
        |        "from":"MysqlReadIncremental",
        |        "outport":"",
        |        "inport":"",
        |        "to":"CsvSave"
        |      }
        |    ]
        |  }
        |}
      """.stripMargin

    val url = "http://10.0.86.98:8001/flow/start"
    val timeout = 1800
    val requestConfig = RequestConfig.custom()
      .setConnectTimeout(timeout * 1000)
      .setConnectionRequestTimeout(timeout * 1000)
      .setSocketTimeout(timeout * 1000).build()

    val client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()

    val post: HttpPost = new HttpPost(url)
    post.addHeader("Content-Type", "application/json")
    post.setEntity(new StringEntity(json))


    val response: CloseableHttpResponse = client.execute(post)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    println("Code is " + str)
  }

}
