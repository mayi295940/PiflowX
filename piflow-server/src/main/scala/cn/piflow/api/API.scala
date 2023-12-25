package cn.piflow.api

import cn.piflow.conf.bean.{FlowBean, GroupBean}
import cn.piflow.conf.util.{ClassUtil, MapUtil, OptionUtil, PluginManager}
import cn.piflow.conf.{ConfigurableStop, VisualizationType}
import cn.piflow.launcher.flink.{FlinkFlowLauncher, FlinkLauncher}
import cn.piflow.launcher.spark.SparkFlowLauncher
import cn.piflow.util.HdfsUtil.{getJsonMapList, getLine}
import cn.piflow.util._
import cn.piflow.{Constants, GroupExecution, Process, Runner}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.util.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataInputStream, FileStatus, FileSystem, Path}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.spark.launcher.SparkAppHandle
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.CountDownLatch
import java.util.zip.{ZipEntry, ZipOutputStream}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

object API {

  def addSparkJar(addSparkJarName: String): String = {
    var id = ""
    val sparkJarFile = new File(PropertyUtil.getSpartJarPath())
    val jarFile = FileUtil.getJarFile(sparkJarFile)
    breakable {
      jarFile.foreach(i => {
        if (i.getName.equals(addSparkJarName)) {
          id = H2Util.addSparkJar(addSparkJarName)
          break
        }
      })
    }
    id
  }

  def removeSparkJar(sparkJarId: String): Boolean = {
    val sparkJarState = H2Util.removeSparkJar(sparkJarId)
    if (sparkJarState == SparkJarState.ON) {
      false
    } else {
      true
    }

  }

  /*def getSparkJarInfo(sparkJarId : String) : String = {
    val pluginInfo = H2Util.getPluginInfo(pluginId)
    pluginInfo
  }*/

  def addPlugin(pluginManager: PluginManager, pluginName: String): String = {
    var id = ""
    val classpathFile = new File(pluginManager.getPluginPath)
    val jarFile = FileUtil.getJarFile(classpathFile)
    breakable {
      jarFile.foreach(i => {
        if (i.getName.equals(pluginName)) {

          pluginManager.unloadPlugin(i.getAbsolutePath)
          pluginManager.loadPlugin(i.getAbsolutePath)
          id = H2Util.addPlugin(pluginName)
          break
        }
      })
    }
    id
  }

  def removePlugin(pluginManager: PluginManager, pluginId: String): Boolean = {
    var result = false
    val pluginName = H2Util.getPluginInfoMap(pluginId).getOrElse("name", "")
    if (pluginName != "") {
      val classpathFile = new File(pluginManager.getPluginPath)
      val jarFile = FileUtil.getJarFile(classpathFile)
      breakable {
        jarFile.foreach(i => {
          println(i.getAbsolutePath)
          if (i.getName.equals(pluginName)) {
            pluginManager.unloadPlugin(i.getAbsolutePath)
            H2Util.removePlugin(pluginName)
            result = true
            break
          }
        })
      }
    }

    result
  }

  def getPluginInfo(pluginId: String): String = {
    val pluginInfo = H2Util.getPluginInfo(pluginId)
    pluginInfo
  }

  def getConfigurableStopInPlugin(pluginManager: PluginManager, pluginName: String): String = {
    var bundleList = List[String]()
    val stops = pluginManager.getPluginConfigurableStops(pluginName)
    stops.foreach(s => {
      bundleList = s.getClass.getName +: bundleList
    })

    """{"bundles":"""" + bundleList.mkString(",") + """"}"""
  }

  def getConfigurableStopInfoInPlugin(pluginManager: PluginManager, pluginName: String): String = {
    var bundleList = List[String]()
    val stops = pluginManager.getPluginConfigurableStops(pluginName)
    stops.foreach(s => {
      bundleList = s.getClass.getName +: bundleList
    })
    val jsonString = ClassUtil.findConfigurableStopListInfo(bundleList)
    jsonString
  }

  def getResourceInfo: String = {

    try {
      val metricsURL = ConfigureUtil.getYarnResourceMatrics()
      val client = HttpClients.createDefault()
      val get: HttpGet = new HttpGet(metricsURL)

      val response: CloseableHttpResponse = client.execute(get)
      val entity = response.getEntity
      val str = EntityUtils.toString(entity, "UTF-8")
      val yarnInfo = JsonUtil.jsonToMap(str)
      val matricInfo = MapUtil.get(yarnInfo, "clusterMetrics").asInstanceOf[Map[String, Any]]

      val totalVirtualCores = matricInfo.getOrElse("totalVirtualCores", "")
      val allocatedVirtualCores = matricInfo.getOrElse("allocatedVirtualCores", "")
      val remainingVirtualCores = totalVirtualCores.asInstanceOf[Double] - allocatedVirtualCores.asInstanceOf[Double]
      val cpuInfo = Map(
        "totalVirtualCores" -> totalVirtualCores,
        "allocatedVirtualCores" -> allocatedVirtualCores,
        "remainingVirtualCores" -> remainingVirtualCores
      )

      val totalMemoryGB = matricInfo.getOrElse("totalMB", "").asInstanceOf[Double] / 1024
      val allocatedMemoryGB = matricInfo.getOrElse("allocatedMB", "").asInstanceOf[Double] / 1024
      val remainingMemoryGB = totalMemoryGB - allocatedMemoryGB
      val memoryInfo = Map(
        "totalMemoryGB" -> totalMemoryGB,
        "allocatedMemoryGB" -> allocatedMemoryGB,
        "remainingMemoryGB" -> remainingMemoryGB
      )

      val hdfsInfo = HdfsUtil.getCapacity()
      val map = Map("cpu" -> cpuInfo, "memory" -> memoryInfo, "hdfs" -> hdfsInfo)
      val resultMap = Map("resource" -> map)

      JsonUtil.format(JsonUtil.toJson(resultMap))
    } catch {
      case ex: Exception => ""
    }

  }

  def getScheduleInfo(scheduleId: String): String = {

    val scheduleInfo = H2Util.getScheduleInfo(scheduleId)
    scheduleInfo
  }

  def startGroup(groupJson: String): GroupExecution = {

    println("StartGroup API get json: \n" + groupJson)

    val map = JsonUtil.jsonToMap(groupJson)
    val flowGroupMap = MapUtil.get(map, "group").asInstanceOf[Map[String, Any]]

    //create flowGroup
    val groupBean = GroupBean(map)
    val group = groupBean.constructGroup()

    val flowGroupExecution = Runner.create()
      .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      .bind("debug.path", ConfigureUtil.getDebugPath())
      .start(group)

    flowGroupExecution
  }

  def stopGroup(flowGroupExecution: GroupExecution): String = {
    flowGroupExecution.stop()
    "ok"
  }

  def getFlowGroupInfo(groupId: String): String = {
    val flowGroupInfo = H2Util.getFlowGroupInfo(groupId)
    flowGroupInfo
  }

  def getFlowGroupProgress(flowGroupID: String): String = {
    val progress = H2Util.getGroupProgressPercent(flowGroupID)
    progress
  }

  def startFlow(flowJson: String): (String, Any) = {

    println("StartFlow API get json: \n" + flowJson)

    val flowMap = JsonUtil.jsonToMap(flowJson)

    val env =  flowMap("flow").asInstanceOf[Map[String, String]]("engineType")

    if ("flink".equals(env)) {
      val (appId, handle) = this.startFlinkFlow(flowMap)
      (appId, handle)
    } else {
      val (appId, handle) = this.startSparkFlow(flowMap)
      (appId, handle)
    }
  }

  private def startSparkFlow(flowMap: Map[String, Any]): (String, SparkAppHandle) = {

    var appId: String = null

    //create flow
    val flowBean = FlowBean[DataFrame](flowMap)
    val flow = flowBean.constructFlow()

    val uuid = flow.getUUID
    val appName = flow.getFlowName
    val (stdout, stderr) = getLogFile(uuid, appName)

    val countDownLatch = new CountDownLatch(1)

    val handle = SparkFlowLauncher.launch(flow).startApplication(new SparkAppHandle.Listener {
      override def stateChanged(handle: SparkAppHandle): Unit = {
        appId = handle.getAppId
        val sparkAppState = handle.getState
        if (appId != null) {
          println("Spark job with app id: " + appId + ",\t State changed to: " + sparkAppState)
        } else {
          println("Spark job's state changed to: " + sparkAppState)
        }
        if (handle.getState.isFinal) {
          countDownLatch.countDown()
          println("Task is finished!")
        }
      }

      override def infoChanged(handle: SparkAppHandle): Unit = {
        //println("Info:" + handle.getState().toString)
      }

    })

    while (handle.getAppId == null) {
      Thread.sleep(100)
    }

    while (!H2Util.isFlowExist(handle.getAppId)) {
      Thread.sleep(1000)
    }

    appId = handle.getAppId

    (appId, handle)

  }

  private def startFlinkFlow(flowMap: Map[String, Any]): (String, Any) = {

    var appId: String = null

    //create flow
    val flowBean = FlowBean[Table](flowMap)
    val flow = flowBean.constructFlow()

    val uuid = flow.getUUID
    val appName = flow.getFlowName
    val (stdout, stderr) = getLogFile(uuid, appName)

    appId = FlinkFlowLauncher.launch(flow)

    (appId, null)

  }

  def stopFlow(appID: String, process: Any): String = {

    //yarn application kill appId
    stopFlowOnYarn(appID)

    process match {
      case _v: SparkAppHandle =>
        process.asInstanceOf[SparkAppHandle].kill()
      case _ =>
    }

    //update db
    H2Util.updateFlowState(appID, FlowState.KILLED)
    H2Util.updateFlowFinishedTime(appID, new Date().toString)

    "ok"
  }

  def stopFlowOnYarn(appID: String): String = {
    //yarn application kill appId
    val url = ConfigureUtil.getYarnResourceManagerWebAppAddress() + appID + "/state"
    val client = HttpClients.createDefault()
    val put: HttpPut = new HttpPut(url)
    val body = "{\"state\":\"KILLED\"}"
    put.addHeader("Content-Type", "application/json")
    put.setEntity(new StringEntity(body))
    val response: CloseableHttpResponse = client.execute(put)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    str
  }

  def getFlowInfo(appID: String): String = {
    val flowInfo = H2Util.getFlowInfo(appID)
    flowInfo
  }

  def getFlowProgress(appID: String): String = {
    val progress = H2Util.getFlowProgress(appID)
    progress
  }

  def getFlowYarnInfo(appID: String): String = {

    val url = ConfigureUtil.getYarnResourceManagerWebAppAddress() + appID
    val client = HttpClients.createDefault()
    val get: HttpGet = new HttpGet(url)

    val response: CloseableHttpResponse = client.execute(get)
    val entity = response.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    str
  }

  def getFlowCheckpoint(appId: String): String = {
    val checkpointPath = ConfigureUtil.getCheckpointPath().stripSuffix(Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + appId
    val checkpointList = HdfsUtil.getFiles(checkpointPath)
    """{"checkpoints":"""" + checkpointList.mkString(",") + """"}"""
  }

  def getFlowDebugData(appId: String, stopName: String, port: String): String = {

    val debugPath: String = ConfigureUtil.getDebugPath().stripSuffix(Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + appId + Constants.SINGLE_SLASH + stopName + Constants.SINGLE_SLASH + port
    val schema = HdfsUtil.getLine(debugPath + "_schema")
    val result = "{\"schema\":\"" + schema + "\", \"debugDataPath\": \"" + debugPath + "\"}"
    result
  }

  def getFlowVisualizationData(appId: String, stopName: String, visualizationType: String): String = {

    var dimensionMap = Map[String, List[String]]()
    val visuanlizationPath: String = ConfigureUtil.getVisualizationPath().stripSuffix(Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + appId + Constants.SINGLE_SLASH + stopName + Constants.SINGLE_SLASH

    val visualizationSchema = getLine(visuanlizationPath + "/schema")
    val schemaArray = visualizationSchema.split(",")
    val jsonMapList = getJsonMapList(visuanlizationPath + "/data")


    if (VisualizationType.LineChart == visualizationType ||
      VisualizationType.Histogram == visualizationType) {

      val jsonTupleList = jsonMapList.flatMap(map => map.toSeq)
      val visualizationInfo = jsonTupleList.groupBy(_._1)
      visualizationInfo.foreach(dimension => {
        var valueList = List[String]()
        val dimensionList = dimension._2
        dimensionList.foreach(dimensionAndCountPair => {
          val v = String.valueOf(dimensionAndCountPair._2)
          println(v)
          valueList = valueList :+ v
        })
        dimensionMap += (dimension._1 -> valueList)
      })
      //dimensionMap
      var lineChartMap = Map[String, Any]()
      var legend = List[String]()
      val x = schemaArray(0)
      lineChartMap += {
        "xAxis" -> Map("type" -> x, "data" -> OptionUtil.getAny(dimensionMap.get(schemaArray(0))))
      }
      //lineChartMap += {"yAxis" -> Map("type" -> "value")}
      var seritesList = List[Map[String, Any]]()
      dimensionMap.filterKeys(!_.equals(x)).foreach(item => {
        val name_action = item._1
        val data = item._2
        val name = name_action.split("_")(0)
        val action = name_action.split("_")(1)
        val vType = visualizationType match {
          case VisualizationType.LineChart => "line"
          case VisualizationType.Histogram => "bar"
        }
        val map = Map("name" -> name, "type" -> vType, "stack" -> action, "data" -> data)
        seritesList = map +: seritesList
        legend = name +: legend
      })
      lineChartMap += {
        "series" -> seritesList
      }
      lineChartMap += {
        "legent" -> legend
      }
      val visualizationJsonData = JsonUtil.format(JsonUtil.toJson(lineChartMap))
      println(visualizationJsonData)
      visualizationJsonData
    } else if (VisualizationType.ScatterPlot == visualizationType) {

      val legendColumn = schemaArray(0)
      val abscissaColumn = schemaArray(1)
      val yaxisColumn = schemaArray(2)

      //get legend
      val legendList = jsonMapList.map(item => {
        item.getOrElse(legendColumn, "").asInstanceOf[String]
      }).distinct

      //get schema
      val newSchema = schemaArray.filter(_ != legendColumn)
      val schemaList = ListBuffer[Map[String, Any]]()
      var index = 0
      newSchema.foreach(column => {
        val schemaMap = Map("name" -> column, "index" -> index, "text" -> column)
        schemaList.append(schemaMap)
        index = index + 1
      })

      //get series
      val seriesList = ListBuffer[Map[String, Any]]()
      legendList.foreach(legend => {

        val legendDataList = ListBuffer[List[String]]()
        jsonMapList.foreach(item => {
          if (item.getOrElse(legendColumn, "").asInstanceOf[String].equals(legend)) {
            val dataList = ListBuffer[String]()
            newSchema.foreach(column => {
              val value = item.getOrElse(column, "").asInstanceOf[String]
              dataList.append(value)
            })
            legendDataList.append(dataList.toList)
          }
        })

        val legendMap = Map[String, Any]("name" -> legend, "type" -> "scatter", "data" -> legendDataList.toList)
        seriesList.append(legendMap)
      })

      val resultMap = Map[String, Any]("legend" -> legendList, "schema" -> schemaList.toList, "series" -> seriesList.toList)
      val visualizationJsonData = JsonUtil.format(JsonUtil.toJson(resultMap))
      println(visualizationJsonData)
      visualizationJsonData
    } else if (VisualizationType.PieChart == visualizationType) {
      var legend = List[String]()
      val schemaArray = visualizationSchema.split(",")
      val schemaReplaceMap = Map(schemaArray(1) -> "value", schemaArray(0) -> "name")
      val jsonMapList = getJsonMapList(visuanlizationPath + "/data")

      var pieChartList = List[Map[String, Any]]()
      jsonMapList.foreach(map => {
        var lineMap = Map[String, Any]()
        for (i <- 0 until schemaArray.size) {
          val column = schemaArray(i)
          lineMap += (schemaReplaceMap.getOrElse(column, "") -> map.getOrElse(column, ""))
        }
        pieChartList = lineMap +: pieChartList
      })
      pieChartList.foreach(item => {
        legend = item.getOrElse("name", "").toString +: legend
      })
      val pieChartMap = Map("legend" -> legend, "series" -> pieChartList)
      val visualizationJsonData = JsonUtil.format(JsonUtil.toJson(pieChartMap))
      println(visualizationJsonData)
      visualizationJsonData
    } else if (VisualizationType.Table == visualizationType) {
      //println(visualizationSchema)
      //println(jsonMapList)
      val resultMap = Map[String, Any]("schema" -> schemaArray.toList, "data" -> jsonMapList)
      val visualizationJsonData = JsonUtil.format(JsonUtil.toJson(resultMap))
      println(visualizationJsonData)
      visualizationJsonData
    }
    else {
      ""
    }
  }

  def getStopInfo(bundle: String): String = {
    try {
      val str = ClassUtil.findConfigurableStopInfo(bundle)
      str
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        throw ex
    }
  }

  def getAllGroups(): String = {
    val groups = ClassUtil.findAllGroups().mkString(",")
    """{"groups":"""" + groups + """"}"""
  }

  def getAllStops(): String = {
    var stops: List[String] = List()
    val stopList = ClassUtil.findAllConfigurableStop()
    stopList.foreach(s => stops = s.getClass.getName +: stops)
    """{"stops":"""" + stops.mkString(",") + """"}"""
  }

  def getAllStopsWithGroup(engineType: String): String = {

    var resultList: List[String] = List()
    var stops = List[(String, String)]()

    var configurableStopList: List[Any] = null

    if (Constants.ENGIN_SPARK.equalsIgnoreCase(engineType)) {
      configurableStopList = ClassUtil.findAllConfigurableStop[DataFrame]("cn.piflow.bundle.spark")
    } else {
      configurableStopList = ClassUtil.findAllConfigurableStop[Table]("cn.piflow.bundle.flink")
    }

    configurableStopList.foreach(s => {
      //generate (group,bundle) pair and put into stops
      val configurableStop = s.asInstanceOf[ConfigurableStop[Any]]
      val groupList = configurableStop.getGroup()
      groupList.foreach(group => {
        val tuple = (group, configurableStop.getClass.getName)
        stops = tuple +: stops
      })
    })

    //(CommonGroup,List((CommonGroup,cn.piflow.bundle.common.Fork),(CommonGroup,cn.piflow.bundle.common.Merge),(...)))
    val groupsInfo = stops.groupBy(_._1)
    groupsInfo.foreach(group => {
      val stopList = group._2
      stopList.foreach(groupAndstopPair => {
        println(groupAndstopPair._1 + ":\t\t" + groupAndstopPair._2)
        var groupAndStop = groupAndstopPair._1 + ":" + groupAndstopPair._2
        resultList = groupAndStop +: resultList
      })
    })
    println("Total stop count : " + stops.size)

    """{"stopWithGroup":"""" + resultList.mkString(",") + """"}"""
  }

  private def getLogFile(uuid: String, appName: String): (File, File) = {
    val now: Date = new Date()
    val dataFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
    val nowDate = dataFormat.format(now)

    val stdoutPathString = PropertyUtil.getPropertyValue("log.path") + Constants.SINGLE_SLASH + appName + "_" + uuid + "_stdout_" + nowDate
    val stdout = new File(stdoutPathString)

    val stderrPathString = PropertyUtil.getPropertyValue("log.path") + Constants.SINGLE_SLASH + appName + "_" + uuid + "_stderr_" + nowDate
    val stderr = new File(stderrPathString)

    (stdout, stderr)
  }

  //Flink Flow
  def startFlinkYarnSessionFlow(flowJson: String) = {

    println(flowJson)
    val map = JsonUtil.jsonToMap(flowJson)
    println(map)

    //create flow
    val flowBean = FlowBean.apply[Table](map)
    val flow = flowBean.constructFlow(false)
    val env = FlinkLauncher.launchYarnSession(flow)

    val process = Runner.create[Table]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .start(flow)
    env.execute(flow.getFlowName)
  }

  def startFlinkYarnClusterFlow(flowJson: String) = {

    println(flowJson)
    val map = JsonUtil.jsonToMap(flowJson)
    println(map)

    //create flow
    val flowBean = FlowBean(map)
    val flow = flowBean.constructFlow(false)

    val appId = FlinkLauncher.launchYarnCluster(flow)

    appId
  }

  def getHdfsDataByPath(hdfsPath: String): ByteArrayInputStream = {
    val conf = new Configuration()
    conf.set("fs.defaultFS", PropertyUtil.getPropertyValue("fs.defaultFS"))
    val fs: FileSystem = FileSystem.get(conf)
    val fileStatusArr: Array[FileStatus] = fs.listStatus(new Path(hdfsPath))
    val map = mutable.HashMap[String, FSDataInputStream]()
    for (elem <- fileStatusArr) {
      val name = elem.getPath.getName
      val inputStream = fs.open(elem.getPath)
      map.put(name, inputStream)
    }

    val byteArrayOutputStream = new ByteArrayOutputStream()
    val zos = new ZipOutputStream(byteArrayOutputStream)
    var zipEntry: ZipEntry = null
    for (elem <- map) {
      zipEntry = new ZipEntry(elem._1)
      zos.putNextEntry(zipEntry)
      IOUtils.copyBytes(elem._2, zos, 1024 * 1024 * 50, false)
      zos.closeEntry()
    }
    zos.close()
    //    println("copy hdfsDir end time:"+new Date(System.currentTimeMillis()))
    val byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray)
    byteArrayInputStream
  }

}

class WaitProcessTerminateRunnable(spark: SparkSession, process: Process[DataFrame]) extends Runnable {
  override def run(): Unit = {
    process.awaitTermination()
    //spark.close()
  }
}

