package cn.piflow.bundle.flux.util

import cn.piflow.bundle.flux.util.CommonUtil.toTimeStamp
import org.apache.spark.sql.functions.{col, when}
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * @Author renhao
 * @Description:
 * @Data 2023/3/24 18:19
 * @Modified By:
 */
class PM_LE extends Serializable{

//  def main(args: Array[String]): Unit = {
//
//    val fluxCBS = readExcel(spark, "/Users/renhao/Desktop/关键技术小组/piflow算法处理组件/PM/通量数据-wpl.xls")
//    val meteorological = readExcel(spark, "/Users/renhao/Desktop/关键技术小组/piflow算法处理组件/PM/插补处理-气象数据.xls")
//
//    //通量设备安装高度6层
//    val arrayH = List(8.0, 8.0, 4.0, 4.0, 6.0, 1.0, 0.0).reverse
//
//    //待校正存储项LE字段名
//    val LE = "LE"
//    //H2O浓度对应字段名
//    val arrayLE = List("Pvapor_1_AVG", "Pvapor_2_AVG", "Pvapor_3_AVG", "Pvapor_4_AVG", "Pvapor_5_AVG", "Pvapor_6_AVG", "Pvapor_7_AVG")
//    //温度对应字段名
//    val arrayHS = List("Ta_1_AVG", "Ta_2_AVG", "Ta_3_AVG", "Ta_4_AVG", "Ta_5_AVG", "Ta_6_AVG", "Ta_7_AVG")
//    //时间间隔，单位为秒
//    val time_nterval = 1800
//
//    val LV: Double = 2440.0
//
//    calculateStoredItem(fluxCBS,meteorological, arrayH, LE, arrayLE, arrayHS, time_nterval, LV)
//      .orderBy(col("year").cast("double"), $"month".cast("double"), $"day".cast("double"), $"hour".cast("double"))
//      .show()
//    //      .take(15).foreach(println)
//  }

  //廓线法(PM)校正存储项FC、LE、HS
  def calculateStoredItem(spark:SparkSession,fluxCBS: DataFrame, meteorological:DataFrame,arrayH: List[Double], LE: String, arrayLE: List[String], arrayHS: List[String]
                          , time_nterval: Integer, LV: Double): DataFrame = {

    import spark.implicits._

    val arrayCols = List(LE).++(arrayLE).++(arrayHS).++(List("timeStamp")).distinct

    val toTimeS = spark.udf.register("toTimeStamp", toTimeStamp _)
    fluxCBS.createOrReplaceTempView("flux")

    val fluxTimeStamp = fluxCBS.withColumn("timeStamp", toTimeS(col("year"),col("month"),col("day"),col("hour")))
    val meteorological_Stamp = meteorological.withColumn("timeStamp", toTimeS(col("year"),col("month"),col("day"),col("hour")))

    val inputJoinDF = fluxTimeStamp.join(meteorological_Stamp,Seq("timeStamp"),"left").select(arrayCols.map(col(_)): _*)
    val inputJoinDFM = inputJoinDF.withColumn("timeStamp",$"timeStamp".plus(time_nterval * 1000))

    val temp = inputJoinDF.as("a").join(inputJoinDFM.as("b"), Seq("timeStamp"), "left")

    val fluxJoinSemih_temp = temp.where(col(s"b.${arrayLE(0)}").isNotNull)
    val fluxJoinSemih = fluxJoinSemih_temp.select(arrayCols.map(x => {
      if (x == LE) {
        Array(col(s"a.$x").as(s"a_$x"))
      } else if (x == "timeStamp") {
        Array(col("timeStamp"))
      } else {
        Array(col(s"a.$x").as(s"a_$x")
          , col(s"b.$x").as(s"b_$x"))
      }
    }).flatMap(x => x): _*)

    val fluxJoinSemihArrayKeys: Seq[String] = fluxJoinSemih.schema.map(_.name)
    val flux_ca = fluxJoinSemih.rdd
      .map(rowToMap(fluxJoinSemihArrayKeys, _))
      .map(x => {
        val le_s_t = calculateDif(x, arrayH, arrayLE, arrayHS)
        val le_s = x.get(s"a_$LE").get.toString.toDouble + (le_s_t.last / time_nterval * LV)
        (x.get("timeStamp").get.toString, le_s_t(0), le_s_t(1)
          , le_s_t(2)
          , le_s_t(3)
          , le_s_t(4)
          , le_s_t(5)
          , le_s_t(6)
          , le_s_t(7)
          , le_s.formatted("%.5f"))
      }).toDF("timeStamp", "d1"
      , "d2"
      , "d3"
      , "d4"
      , "d5"
      , "d6"
      , "d7"
      , "d8"
      , s"flux_$LE")

    val colsResult: Seq[Column] = fluxCBS.schema.map(_.name).map(x => {
      if (x == LE) {
        when(col(s"flux_$x").isNull, col(x))
          .otherwise(col(s"flux_$x")).as(x)
      } else {
        col(x)
      }
    })
    val resultDF = fluxTimeStamp.join(flux_ca, Seq("timeStamp"), "left")
          .select(colsResult: _*)
    resultDF
  }

  //计算水汽密度
  def calulateV(double_LE: Double, double_HS: Double): Double = {
    216.5 * double_LE / (273.15 + double_HS)
  }

  //两个时刻的数据，计算CO2浓度、潜热通量、显热通量差值进行计算
  def calculateDif(map: Map[String, Any], arrayH: List[Double], arrayV: List[String], arrayHS: List[String]): ArrayBuffer[Double] = {
    val arr: ArrayBuffer[Double] = new ArrayBuffer[Double]()
    var result = 0.0
    for (index <- 0 to arrayH.length - 1) {
      val fc_interval = arrayH(index) * (
        calulateV(map.get(s"a_${arrayV(index)}").get.toString.toDouble, map.get(s"a_${arrayHS(index)}").get.toString.toDouble)
          - calulateV(map.get(s"b_${arrayV(index)}").get.toString.toDouble, map.get(s"b_${arrayHS(index)}").get.toString.toDouble)
        )
      arr.append(fc_interval)
      result += fc_interval
    }
    arr.append(result)
    arr
  }

  //将dataFrame中每一行数据转化成map[String,Any]数据
  def rowToMap(array: Seq[String], row: Row): Map[String, Any] = {
    val map = new mutable.HashMap[String, Any]()
    for (index <- 0 to array.length - 1) {
      map.+=((array(index), row.get(index)))
    }
    map.toMap
  }

}
