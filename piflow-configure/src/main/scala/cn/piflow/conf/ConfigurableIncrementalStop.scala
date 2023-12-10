package cn.piflow.conf

import cn.piflow.util.{ConfigureUtil, HdfsUtil, PropertyUtil}
import cn.piflow.{IncrementalStop, JobContext}

/**
 * Created by xjzhu@cnic.cn on 7/15/19
 */
abstract class ConfigurableIncrementalStop[DataStream]
  extends ConfigurableStop[DataStream]
    with IncrementalStop[DataStream] {

  override var incrementalPath: String = _

  override def init(flowName: String, stopName: String): Unit = {
    incrementalPath = ConfigureUtil.getIncrementPath().stripSuffix("/") + "/" + flowName + "/" + stopName
  }

  override def readIncrementalStart(): String = {
    if (!HdfsUtil.exists(incrementalPath))
      HdfsUtil.createFile(incrementalPath)
    val value: String = HdfsUtil.getLine(incrementalPath)
    value
  }

  override def saveIncrementalStart(value: String): Unit = {
    HdfsUtil.saveLine(incrementalPath, value)
  }

}
