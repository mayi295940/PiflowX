package cn.piflow.bundle.spark.visualization

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup}
import cn.piflow.util.PropertyUtil
import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.spark.sql.{DataFrame, SparkSession}

class CustomView extends ConfigurableVisualizationStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Save the custom view data as a csv file."
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  override var visualizationType: String = "CustomView"

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvSave.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Visualization)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(in: JobInputStream[DataFrame],
                       out: JobOutputStream[DataFrame],
                       pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()

    val hdfs = PropertyUtil.getVisualDataDirectoryPath()
    val appID = spark.sparkContext.applicationId

    val df = in.read()
    val filePath = hdfs + appID + Constants.SINGLE_SLASH + pec.getStopJob.getStopName
    df.repartition(1).write
      .format("csv")
      .mode("overwrite")
      .option("header", "true")
      .option("delimiter", ",")
      .save(filePath)
  }

  override def init(stopName: String): Unit = {
    this.stopName = stopName
  }

  override def getVisualizationPath(processId: String): String = {
    visualizationPath = processId + Constants.SINGLE_SLASH + stopName
    visualizationPath
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
