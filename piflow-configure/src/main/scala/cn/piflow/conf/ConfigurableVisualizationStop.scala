package cn.piflow.conf

import cn.piflow.util.ConfigureUtil
import cn.piflow.{Constants, VisualizationStop}

/**
 * Created by xjzhu@cnic.cn on 8/11/202
 */
abstract class ConfigurableVisualizationStop[DataStream]
  extends ConfigurableStop[DataStream]
    with VisualizationStop[DataStream] {

  override var visualizationPath: String = _
  override var processId: String = _
  override var stopName: String = _

  override def init(stopName: String): Unit = {
    this.stopName = stopName
  }

  override def getVisualizationPath(processId: String): String = {
    visualizationPath = ConfigureUtil
      .getVisualizationPath()
      .stripSuffix(Constants.SINGLE_SLASH) + Constants.SINGLE_SLASH + processId + Constants.SINGLE_SLASH + stopName
    visualizationPath
  }

}
