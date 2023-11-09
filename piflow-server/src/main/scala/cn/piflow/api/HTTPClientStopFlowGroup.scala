package cn.piflow.api

import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object HTTPClientStopFlowGroup {
  def main(args: Array[String]): Unit = {
    val json = """{"groupId":"group_91198855-afbc-4726-856f-31326173a90f"}"""
    val url = "http://10.0.85.83:8001/group/stop"
    val client = HttpClients.createDefault()
    val post: HttpPost = new HttpPost(url)

    post.addHeader("Content-Type", "application/json")
    post.setEntity(new StringEntity(json))

    val response: CloseableHttpResponse = client.execute(post)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    println(str)
  }

}
