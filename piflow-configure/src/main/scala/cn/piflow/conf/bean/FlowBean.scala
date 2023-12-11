package cn.piflow.conf.bean

import cn.piflow.conf.util.{MapUtil, ScalaExecutorUtil}
import cn.piflow.util.JsonUtil
import cn.piflow.{FlowImpl, Path}
import net.liftweb.json.JsonDSL._
import net.liftweb.json._

class FlowBean[DataStream] extends GroupEntryBean {

  /*@BeanProperty*/
  var uuid: String = _
  var name: String = _
  var checkpoint: String = _
  var checkpointParentProcessId: String = _
  private var runMode: String = _
  private var showData: String = _

  var stops: List[StopBean[DataStream]] = List()
  var paths: List[PathBean] = List()

  //flow resource info
  private var driverMem: String = _
  private var executorNum: String = _
  private var executorMem: String = _
  var executorCores: String = _

  //flow json string
  var flowJson: String = _

  def init(map: Map[String, Any]): Unit = {

    val flowJsonOjb = JsonUtil.toJson(map)
    this.flowJson = JsonUtil.format(flowJsonOjb)

    val flowMap = MapUtil.get(map, "flow").asInstanceOf[Map[String, Any]]

    this.uuid = MapUtil.get(flowMap, "uuid").asInstanceOf[String]
    this.name = MapUtil.get(flowMap, "name").asInstanceOf[String]
    this.checkpoint = flowMap.getOrElse("checkpoint", "").asInstanceOf[String]
    this.checkpointParentProcessId = flowMap.getOrElse("checkpointParentProcessId", "").asInstanceOf[String]
    this.runMode = flowMap.getOrElse("runMode", "RUN").asInstanceOf[String]
    this.showData = flowMap.getOrElse("showData", "0").asInstanceOf[String]

    this.driverMem = flowMap.getOrElse("driverMemory", "1g").asInstanceOf[String]
    this.executorNum = flowMap.getOrElse("executorNumber", "1").asInstanceOf[String]
    this.executorMem = flowMap.getOrElse("executorMemory", "1g").asInstanceOf[String]
    this.executorCores = flowMap.getOrElse("executorCores", "1").asInstanceOf[String]

    //construct StopBean List
    val stopsList = MapUtil.get(flowMap, "stops").asInstanceOf[List[Map[String, Any]]]
    stopsList.foreach(stopMap => {
      val stop = StopBean[DataStream](this.name, stopMap)
      this.stops = stop +: this.stops
    })

    //construct PathBean List
    val pathsList = MapUtil.get(flowMap, "paths").asInstanceOf[List[Map[String, Any]]]
    pathsList.foreach(pathMap => {
      val path = PathBean(pathMap)
      this.paths = path +: this.paths
    })
  }

  //create Flow by FlowBean
  def constructFlow(buildScalaJar: Boolean = true): FlowImpl[DataStream] = {

    if (buildScalaJar)
      ScalaExecutorUtil.buildScalaExcutorJar(this)

    val flow = new FlowImpl[DataStream]()

    flow.setFlowJson(this.flowJson)
    flow.setFlowName(this.name)
    flow.setUUID(uuid)
    flow.setCheckpointParentProcessId(this.checkpointParentProcessId)
    flow.setRunMode(this.runMode)

    flow.setDriverMemory(this.driverMem)
    flow.setExecutorNum(this.executorNum)
    flow.setExecutorCores(this.executorCores)
    flow.setExecutorMem(this.executorMem)

    this.stops.foreach(stopBean => {
      flow.addStop(stopBean.name, stopBean.constructStop())
    })

    this.paths.foreach(pathBean => {
      flow.addPath(Path.from(pathBean.from).via(pathBean.outport, pathBean.inport).to(pathBean.to))
    })

    if (!this.checkpoint.equals("")) {
      val checkpointList = this.checkpoint.split(",")
      checkpointList.foreach { checkpoint => flow.addCheckPoint(checkpoint) }
    }

    flow
  }

  def toJson: String = {
    val json =
      "flow" ->
        ("uuid" -> this.uuid) ~
          ("name" -> this.name) ~
          ("stops" ->
            stops.map { stop =>
              ("uuid" -> stop.uuid) ~
                ("name" -> stop.name) ~
                ("bundle" -> stop.bundle)
            }) ~
          ("paths" ->
            paths.map { path =>
              ("from" -> path.from) ~
                ("outport" -> path.outport) ~
                ("inport" -> path.inport) ~
                ("to" -> path.to)
            })

    val jsonString = compactRender(json)
    jsonString
  }

}

object FlowBean {
  def apply[DataStream](map: Map[String, Any]): FlowBean[DataStream] = {
    val flowBean = new FlowBean[DataStream]()
    flowBean.init(map)
    flowBean
  }
}
