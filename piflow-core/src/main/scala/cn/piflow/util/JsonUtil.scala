package cn.piflow.util

import com.alibaba.fastjson2.{JSON, JSONArray, JSONObject}
import org.json4s.DefaultFormats

import scala.collection.JavaConverters.mapAsScalaMapConverter

/**
 * Created by xjzhu@cnic.cn on 4/30/19
 */
object JsonUtil {

  def toJson(map: Map[String, Any]): JSONObject = {
    val str = org.json4s.native.Json(DefaultFormats).write(map)
    JSON.parseObject(str)
  }

  def format(t: Any, i: Int = 0): String = {
    val str = JSON.toJSONString(JSONObject.parseObject(t.toString), com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat)
    str
  }


  def jsonToSome(str: String): Some[Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    val map1: Map[String, Any] = map.map(x => {
      if (x._2.toString.startsWith("[")) (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else (x._1, x._2)
    })
    Some(map1)
  }

  def jsonToMap(str: String): Map[String, Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    val map1: Map[String, Any] = map.map(x => {
      if (x._2.toString.startsWith("[")) (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else (x._1, x._2)
    })
    map1
  }


  def jsonObjectToMapUtil(str: String): Map[String, Any] = {
    val map: Map[String, Any] = JSON.parseObject(str).asScala.toMap[String, Any]
    map.map(x => {
      if (x._2.toString.startsWith("[")) (x._1, jsonArrayToMapUtil(JSON.parseArray(x._2.toString)))
      else if (x._2.toString.startsWith("{")) (x._1, jsonObjectToMapUtil(x._2.toString))
      else if (x._2 == null) (x._1, "")
      else (x._1, x._2)
    })
  }


  def jsonArrayToMapUtil(jsonArray: JSONArray): List[Any] = {
    val list: List[Any] = jsonArray.toArray().map {
      case (other) => if (other.toString.startsWith("{")) jsonObjectToMapUtil(other.toString)
      case (other) => other
    }.toList
    list
  }



  //  def toJson(arr : List[Any]) : JSONArray = {
  //    JSONArray(arr.map {
  //      case (innerMap : Map[String, Any]) => toJson(innerMap)
  //      case (innerArray : List[Any]) => toJson(innerArray)
  //      case (other) => other
  //    })
  //  }
  //
  //  def toJson(map:Map[String,Any]):JSONObject = {
  //    JSONObject(map.map {
  //      case(key, innerMap:Map[String, Any]) => (key, toJson(innerMap))
  //      case(key, innerArray: List[Any]) => (key, toJson(innerArray))
  //      case(key, other) => (key, if (other == null) "" else other )
  //    })
  //  }
  //
  //  def format(t:Any, i: Int = 0) : String = t match {
  //    case o: JSONObject =>
  //      o.obj.map{
  //        case (k, v) =>
  //          "    "*(i+1) + JSONFormat.defaultFormatter(k) + ": " + format(v, i+1)
  //      }.mkString("{\n",",\n","\n" + "    "*i + "}")
  //    case a: JSONArray =>
  //      a.list.map{
  //        e => "    "*(i+1) + format(e, i+1)
  //      }.mkString("[\n",",\n","\n" + "    "*i + "]")
  //    case _ => JSONFormat defaultFormatter t
  //  }

}
