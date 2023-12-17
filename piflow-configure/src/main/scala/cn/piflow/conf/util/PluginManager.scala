package cn.piflow.conf.util

import cn.piflow.Constants
import cn.piflow.conf.ConfigurableStop
import cn.piflow.util.PropertyUtil
import com.sksamuel
import com.sksamuel.scrimage.Image
import org.clapper.classutil.ClassFinder

import java.io.{BufferedInputStream, File}
import java.net.{MalformedURLException, URL}
import scala.collection.mutable.{Map => MMap}

class PluginManager {

  private val pluginPath = PropertyUtil.getClassPath()
  private val pluginMap = MMap[String, PluginClassLoader]()

  def PlugInManager(): Unit = {}

  def getPluginPath: String = {
    this.pluginPath
  }

  def getConfigurableStop[DataStream](plugName: String,
                                      bundleName: String): ConfigurableStop[DataStream] = {
    try {
      val plugin = pluginPath + plugName
      val forName = Class.forName(bundleName, true, getLoader(plugin))
      val ins = forName.newInstance.asInstanceOf[ConfigurableStop[DataStream]]
      return ins
    } catch {
      case e: IllegalAccessException =>
        e.printStackTrace()
      case e: InstantiationException =>
        e.printStackTrace()
      case e: ClassNotFoundException =>
        e.printStackTrace()
    }
    null
  }

  def getConfigurableStop[DataStream](bundleName: String): ConfigurableStop[DataStream] = {
    val it = pluginMap.keys.iterator
    while (it.hasNext) {
      val plugin = it.next
      try {
        val forName = Class.forName(bundleName, true, getLoader(plugin))
        val ins = forName.newInstance.asInstanceOf[ConfigurableStop[DataStream]]
        System.out.println(bundleName + " is found in " + plugin)
        return ins
      } catch {
        case e: IllegalAccessException =>
          e.printStackTrace()
        case e: InstantiationException =>
          e.printStackTrace()
        case e: ClassNotFoundException =>
          e.printStackTrace()
          System.err.println(bundleName + " can not be found in " + plugin)
      }
    }
    null
  }

  def getConfigurableStopIcon[DataStream](imagePath: String,
                                          bundleName: String): Array[Byte] = {
    val it = pluginMap.keys.iterator
    while (it.hasNext) {
      val plugin = it.next
      try {
        val forName = Class.forName(bundleName, true, getLoader(plugin))
        forName.newInstance.asInstanceOf[ConfigurableStop[DataStream]]
        val imageInputStream = getLoader(plugin).getResourceAsStream(imagePath)
        val input = new BufferedInputStream(imageInputStream)
        Image.fromStream(input).bytes(sksamuel.scrimage.writer)
      } catch {
        case e: IllegalAccessException =>
          e.printStackTrace()
        case e: InstantiationException =>
          e.printStackTrace()
        case e: ClassNotFoundException =>
          System.err.println(bundleName + " can not be found in " + plugin)
          e.printStackTrace()
      }
    }
    null
  }

  def getPluginConfigurableStops[DataStream]: List[ConfigurableStop[DataStream]] = {

    var stopList = List[ConfigurableStop[DataStream]]()
    val pluginIterator = pluginMap.keys.iterator
    while (pluginIterator.hasNext) {
      val plugin: String = pluginIterator.next
      val finder = ClassFinder(Seq(new File(plugin)))
      val classes = finder.getClasses

      try {
        for (externalClass <- classes) {
          try {
            if (externalClass.superClassName.equals(ClassUtil.configurableStopClass)) {
              val forName = Class.forName(externalClass.name, true, getLoader(plugin))
              val ins = forName.newInstance.asInstanceOf[ConfigurableStop[DataStream]]
              System.out.println("Find ConfigurableStop: " + externalClass.name + " in " + plugin)
              stopList = ins +: stopList
            }

          } catch {
            case e: IllegalAccessException =>
              e.printStackTrace()
            case e: InstantiationException =>
              System.err.println(externalClass.name + " can not be instantiation in " + plugin)
              e.printStackTrace()
            case e: ClassNotFoundException =>
              e.printStackTrace()
              System.err.println(externalClass.name + " can not be found in " + plugin)
            case e: Exception =>
              e.printStackTrace()
          }
        }
      } catch {
        case e: UnsupportedOperationException =>
          System.err.println("external plugin throw UnsupportedOperationException.")
          e.printStackTrace()
      }
    }
    stopList
  }

  def getPluginConfigurableStops[DataStream](pluginName: String): List[ConfigurableStop[DataStream]] = {

    var stopList = List[ConfigurableStop[DataStream]]()
    var plugin = this.getPluginPath + pluginName
    //temp
    plugin = plugin.replace(Constants.SINGLE_SLASH, "\\")
    if (pluginMap.contains(plugin)) {

      val finder = ClassFinder(Seq(new File(plugin)))
      val classes = finder.getClasses
      try {
        for (externalClass <- classes) {

          try {
            if (externalClass.superClassName.equals(ClassUtil.configurableStopClass)) {
              val forName = Class.forName(externalClass.name, true, getLoader(plugin))
              val ins = forName.newInstance.asInstanceOf[ConfigurableStop[DataStream]]
              System.out.println("Find ConfigurableStop: " + externalClass.name + " in " + plugin)
              stopList = ins +: stopList
            }

          } catch {
            case e: IllegalAccessException =>
              e.printStackTrace()
            case e: InstantiationException =>
              System.err.println(externalClass.name + " can not be instantiation in " + plugin)
              e.printStackTrace()
            case e: ClassNotFoundException =>
              e.printStackTrace()
              System.err.println(externalClass.name + " can not be found in " + plugin)
          }
        }
      } catch {
        case e: UnsupportedOperationException =>
          System.err.println("external plugin throw UnsupportedOperationException.")
          e.printStackTrace()
      }
    }
    stopList
  }

  private def addLoader(pluginName: String, loader: PluginClassLoader): Unit = {
    this.pluginMap.put(pluginName, loader)
  }

  private def getLoader(pluginName: String): PluginClassLoader = this.pluginMap(pluginName)

  def loadPlugin(pluginName: String): Unit = {
    this.pluginMap.remove(pluginName)
    val loader = new PluginClassLoader
    val pluginUrl = "jar:file:" + pluginName + "!/"
    var url: URL = null
    try
      url = new URL(pluginUrl)
    catch {
      case e: MalformedURLException =>
        e.printStackTrace()
    }
    loader.addURLFile(url)
    addLoader(pluginName, loader)
    System.out.println("load " + pluginName + " success")
  }

  def unloadPlugin(pluginName: String): Unit = {
    if (this.pluginMap.contains(pluginName)) {
      this.pluginMap(pluginName).unloadJarFiles()
      this.pluginMap.remove(pluginName)
    }
  }
}

object PluginManager {
  private var instance: PluginManager = _

  def getInstance: PluginManager = {
    if (instance == null)
      instance = new PluginManager()
    instance
  }
}
