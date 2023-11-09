package cn.piflow.api

import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object HTTPClientGetStopInfo {

  def main(args: Array[String]): Unit = {

    val url = "http://10.0.85.83:8001/stop/info?bundle=cn.piflow.bundle.csv.CsvParser"
    val client = HttpClients.createDefault()
    val getFlowInfo: HttpGet = new HttpGet(url)

    val response: CloseableHttpResponse = client.execute(getFlowInfo)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    println("result : " + str)
  }

}
