package cn.piflow

import cn.piflow.util._

import java.util.Date
import scala.collection.mutable.ArrayBuffer

trait Runner[DataStream] {
  def bind(key: String, value: Any): Runner[DataStream]

  def start(flow: Flow[DataStream]): Process[DataStream]

  def start(group: Group[DataStream]): GroupExecution

  def addListener(listener: RunnerListener[DataStream]): Unit

  def removeListener(listener: RunnerListener[DataStream]): Unit

  def getListener: RunnerListener[DataStream]
}

object Runner {

  def create[DataStream](): Runner[DataStream] = new Runner[DataStream]() {

    val listeners: ArrayBuffer[RunnerListener[DataStream]] = ArrayBuffer[RunnerListener[DataStream]](new RunnerLogger())

    val compositeListener: RunnerListener[DataStream] = new RunnerListener[DataStream]() {
      override def onProcessStarted(ctx: ProcessContext[DataStream]): Unit = {
        listeners.foreach(_.onProcessStarted(ctx))
      }

      override def onProcessFailed(ctx: ProcessContext[DataStream]): Unit = {
        listeners.foreach(_.onProcessFailed(ctx))
      }

      override def onProcessCompleted(ctx: ProcessContext[DataStream]): Unit = {
        listeners.foreach(_.onProcessCompleted(ctx))
      }

      override def onJobStarted(ctx: JobContext[DataStream]): Unit = {
        listeners.foreach(_.onJobStarted(ctx))
      }

      override def onJobCompleted(ctx: JobContext[DataStream]): Unit = {
        listeners.foreach(_.onJobCompleted(ctx))
      }

      override def onJobInitialized(ctx: JobContext[DataStream]): Unit = {
        listeners.foreach(_.onJobInitialized(ctx))
      }

      override def onProcessForked(ctx: ProcessContext[DataStream],
                                   child: ProcessContext[DataStream]): Unit = {
        listeners.foreach(_.onProcessForked(ctx, child))
      }

      override def onJobFailed(ctx: JobContext[DataStream]): Unit = {
        listeners.foreach(_.onJobFailed(ctx))
      }

      override def onProcessAborted(ctx: ProcessContext[DataStream]): Unit = {
        listeners.foreach(_.onProcessAborted(ctx))
      }

      override def monitorJobCompleted(ctx: JobContext[DataStream],
                                       outputs: JobOutputStream[DataStream]): Unit = {
        //TODO:
        listeners.foreach(_.monitorJobCompleted(ctx, outputs))
      }

      override def onGroupStarted(ctx: GroupContext[DataStream]): Unit = {
        listeners.foreach(_.onGroupStarted(ctx))
      }

      override def onGroupCompleted(ctx: GroupContext[DataStream]): Unit = {
        listeners.foreach(_.onGroupCompleted(ctx))
      }

      override def onGroupFailed(ctx: GroupContext[DataStream]): Unit = {
        listeners.foreach(_.onGroupFailed(ctx))
      }

      override def onGroupStoped(ctx: GroupContext[DataStream]): Unit = {
        //TODO
      }
    }

    override def addListener(listener: RunnerListener[DataStream]): Unit = {
      listeners += listener
    }

    override def getListener: RunnerListener[DataStream] = compositeListener

    val ctx = new CascadeContext[DataStream]()

    override def bind(key: String, value: Any): this.type = {
      ctx.put(key, value)
      this
    }

    override def start(flow: Flow[DataStream]): Process[DataStream] = {
      new ProcessImpl[DataStream](flow, ctx, this)
    }

    override def start(group: Group[DataStream]): GroupExecution = {
      new GroupExecutionImpl(group, ctx, this)
    }

    override def removeListener(listener: RunnerListener[DataStream]): Unit = {
      listeners -= listener
    }
  }
}

trait RunnerListener[DataStream] {
  def onProcessStarted(ctx: ProcessContext[DataStream]): Unit

  def onProcessForked(ctx: ProcessContext[DataStream], child: ProcessContext[DataStream]): Unit

  def onProcessCompleted(ctx: ProcessContext[DataStream]): Unit

  def onProcessFailed(ctx: ProcessContext[DataStream]): Unit

  def onProcessAborted(ctx: ProcessContext[DataStream]): Unit

  def onJobInitialized(ctx: JobContext[DataStream]): Unit

  def onJobStarted(ctx: JobContext[DataStream]): Unit

  def onJobCompleted(ctx: JobContext[DataStream]): Unit

  def onJobFailed(ctx: JobContext[DataStream]): Unit

  def monitorJobCompleted(ctx: JobContext[DataStream], outputs: JobOutputStream[DataStream]): Unit

  def onGroupStarted(ctx: GroupContext[DataStream]): Unit

  def onGroupCompleted(ctx: GroupContext[DataStream]): Unit

  def onGroupFailed(ctx: GroupContext[DataStream]): Unit

  def onGroupStoped(ctx: GroupContext[DataStream]): Unit

}

class RunnerLogger[DataStream] extends RunnerListener[DataStream] with Logging {
  //TODO: add GroupID or ProjectID
  override def onProcessStarted(ctx: ProcessContext[DataStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val flowName = ctx.getFlow.toString
    val time = new Date().toString
    logger.debug(s"process started: $pid, flow: $flowName, time: $time")
    println(s"process started: $pid, flow: $flowName, time: $time")
    //update flow state to STARTED
    val appId = getAppId(ctx)
    H2Util.addFlow(appId, pid, ctx.getFlow.getFlowName)
    H2Util.updateFlowState(appId, FlowState.STARTED)
    H2Util.updateFlowStartTime(appId, time)
  }

  override def onJobStarted(ctx: JobContext[DataStream]): Unit = {
    val jid = ctx.getStopJob.jid()
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job started: $jid, stop: $stopName, time: $time")
    println(s"job started: $jid, stop: $stopName, time: $time")
    //update stop state to STARTED
    H2Util.updateStopState(getAppId(ctx), stopName, StopState.STARTED)
    H2Util.updateStopStartTime(getAppId(ctx), stopName, time)
  }

  override def onJobFailed(ctx: JobContext[DataStream]): Unit = {
    ctx.getProcessContext
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job failed: $stopName, time: $time")
    println(s"job failed: $stopName, time: $time")
    //update stop state to FAILED
    H2Util.updateStopFinishedTime(getAppId(ctx), stopName, time)
    H2Util.updateStopState(getAppId(ctx), stopName, StopState.FAILED)

  }

  override def onJobInitialized(ctx: JobContext[DataStream]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job initialized: $stopName, time: $time")
    println(s"job initialized: $stopName, time: $time")
    //add stop into h2 db and update stop state to INIT
    val appId = getAppId(ctx)
    H2Util.addStop(appId, stopName)
    H2Util.updateStopState(appId, stopName, StopState.INIT)
  }

  override def onProcessCompleted(ctx: ProcessContext[DataStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process completed: $pid, time: $time")
    println(s"process completed: $pid, time: $time")
    //update flow state to COMPLETED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(appId, FlowState.COMPLETED)

  }

  override def onJobCompleted(ctx: JobContext[DataStream]): Unit = {
    val stopName = ctx.getStopJob.getStopName
    val time = new Date().toString
    logger.debug(s"job completed: $stopName, time: $time")
    println(s"job completed: $stopName, time: $time")
    //update stop state to COMPLETED
    val appId = getAppId(ctx)
    H2Util.updateStopFinishedTime(appId, stopName, time)
    H2Util.updateStopState(appId, stopName, StopState.COMPLETED)

  }

  override def onProcessFailed(ctx: ProcessContext[DataStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process failed: $pid, time: $time")
    println(s"process failed: $pid, time: $time")
    //update flow state to FAILED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(getAppId(ctx), FlowState.FAILED)

  }

  override def onProcessAborted(ctx: ProcessContext[DataStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process aborted: $pid, time: $time")
    println(s"process aborted: $pid, time: $time")
    //update flow state to ABORTED
    val appId = getAppId(ctx)
    H2Util.updateFlowFinishedTime(appId, time)
    H2Util.updateFlowState(appId, FlowState.ABORTED)

  }

  override def onProcessForked(ctx: ProcessContext[DataStream],
                               child: ProcessContext[DataStream]): Unit = {
    val pid = ctx.getProcess.pid()
    val cid = child.getProcess.pid()
    val time = new Date().toString
    logger.debug(s"process forked: $pid, child flow execution: $cid, time: $time")
    println(s"process forked: $pid, child flow execution: $cid, time: $time")
    //update flow state to FORK
    H2Util.updateFlowState(getAppId(ctx), FlowState.FORK)
  }

  private def getAppId(ctx: Context[DataStream]): String = {
    // todo
    //    val sparkSession = ctx.get(classOf[SparkSession].getName).asInstanceOf[SparkSession]
    //    sparkSession.sparkContext.applicationId
    ""
  }

  override def monitorJobCompleted(ctx: JobContext[DataStream],
                                   outputs: JobOutputStream[DataStream]): Unit = {
    val appId = getAppId(ctx)
    val stopName = ctx.getStopJob.getStopName
    logger.debug(s"job completed: monitor $stopName")
    println(s"job completed: monitor $stopName")

  }

  override def onGroupStarted(ctx: GroupContext[DataStream]): Unit = {
    //TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val childCount = ctx.getGroupExecution.getChildCount
    val time = new Date().toString
    //val flowCount = ctx.getGroupExecution().getFlowCount()
    logger.debug(s"Group started: $groupId, group: $flowGroupName, time: $time")
    println(s"Group started: $groupId, group: $flowGroupName, time: $time")
    //update flow group state to STARTED
    H2Util.addGroup(groupId, flowGroupName, childCount)
    H2Util.updateGroupState(groupId, GroupState.STARTED)
    H2Util.updateGroupStartTime(groupId, time)
  }

  override def onGroupCompleted(ctx: GroupContext[DataStream]): Unit = {
    //TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group completed: $groupId, time: $time")
    println(s"Group completed: $groupId, time: $time")
    //update flow group state to COMPLETED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.COMPLETED)

  }

  override def onGroupStoped(ctx: GroupContext[DataStream]): Unit = {
    //TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val flowGroupName = ctx.getGroup.getGroupName
    val time = new Date().toString
    logger.debug(s"Group stoped: $groupId, time: $time")
    println(s"Group stoped: $groupId, time: $time")
    //update flow group state to COMPLETED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.KILLED)

  }

  override def onGroupFailed(ctx: GroupContext[DataStream]): Unit = {
    //TODO: write monitor data into db
    val groupId = ctx.getGroupExecution.getGroupId
    val time = new Date().toString
    logger.debug(s"Group failed: $groupId, time: $time")
    println(s"Group failed: $groupId, time: $time")
    //update flow group state to FAILED
    H2Util.updateGroupFinishedTime(groupId, time)
    H2Util.updateGroupState(groupId, GroupState.FAILED)

  }

}
