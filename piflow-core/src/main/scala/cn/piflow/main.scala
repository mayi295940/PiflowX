package cn.piflow

import java.util.concurrent.{CountDownLatch, TimeUnit}
import cn.piflow.util._

import scala.reflect.ClassTag
//import org.apache.flink.streaming.api.datastream.DataStream
import scala.collection.mutable.{ArrayBuffer, Map => MMap}

trait JobInputStream[DataStream] {
  def isEmpty: Boolean

  def read[DataType: ClassTag](): DataStream

  def ports(): Seq[String]

  def read[DataType: ClassTag](inport: String): DataStream

  def readProperties(): MMap[String, String]

  def readProperties(inport: String): MMap[String, String]
}

trait JobOutputStream[DataStream] {

  def write[DataType: ClassTag](data: DataStream)

  def write[DataType: ClassTag](bundle: String, data: DataStream)

  def writeProperties(properties: MMap[String, String])

  def writeProperties(bundle: String, properties: MMap[String, String])

  def sendError()
}

trait StopJob[DataStream] {
  def jid(): String

  def getStopName(): String

  def getStop(): Stop[DataStream]
}

trait JobContext[DataStream] extends Context[DataStream] {
  def getStopJob(): StopJob[DataStream]

  def getInputStream(): JobInputStream[DataStream]

  def getOutputStream(): JobOutputStream[DataStream]

  def getProcessContext(): ProcessContext[DataStream]
}

trait Stop[DataStream] extends Serializable {
  def initialize(ctx: ProcessContext[DataStream]): Unit

  def perform(in: JobInputStream[DataStream], out: JobOutputStream[DataStream], pec: JobContext[DataStream]): Unit;
}


trait GroupEntry[DataStream] {}

trait Flow[DataStream] extends GroupEntry[DataStream] {
  def getStopNames(): Seq[String];

  def hasCheckPoint(processName: String): Boolean;

  def getStop(name: String): Stop[DataStream];

  def analyze(): AnalyzedFlowGraph[DataStream];

  def show(): Unit;

  def getFlowName(): String;

  def setFlowName(flowName: String): Unit;

  def getCheckpointParentProcessId(): String;

  def setCheckpointParentProcessId(checkpointParentProcessId: String);

  def getRunMode(): String;

  def setRunMode(runMode: String): Unit;


  //Flow Josn String API
  def setFlowJson(flowJson: String);

  def getFlowJson(): String;


  // Flow resource API
  def setDriverMemory(driverMem: String);

  def getDriverMemory(): String;

  def setExecutorNum(executorNum: String);

  def getExecutorNum(): String;

  def setExecutorMem(executorMem: String);

  def getExecutorMem(): String;

  def setExecutorCores(executorCores: String);

  def getExecutorCores(): String;

  def setUUID(uuid: String);

  def getUUID(): String;
}

class FlowImpl[DataStream] extends Flow[DataStream] {
  var name = ""
  var uuid = ""

  val edges = ArrayBuffer[Edge]();
  val stops = MMap[String, Stop[DataStream]]();
  val checkpoints = ArrayBuffer[String]();
  var checkpointParentProcessId = ""
  var runMode = ""
  var flowJson = ""

  //Flow Resource
  var driverMem = ""
  var executorNum = ""
  var executorMem = ""
  var executorCores = ""

  def addStop(name: String, process: Stop[DataStream]) = {
    stops(name) = process;
    this;
  };


  def addCheckPoint(processName: String): Unit = {
    checkpoints += processName;
  }

  override def hasCheckPoint(processName: String): Boolean = {
    checkpoints.contains(processName);
  }

  override def getStop(name: String) = stops(name);

  override def getStopNames(): Seq[String] = stops.map(_._1).toSeq;

  def addPath(path: Path): Flow[DataStream] = {
    edges ++= path.toEdges();
    this;
  }


  override def analyze(): AnalyzedFlowGraph[DataStream] =
    new AnalyzedFlowGraph[DataStream]() {
      val incomingEdges = MMap[String, ArrayBuffer[Edge]]();
      val outgoingEdges = MMap[String, ArrayBuffer[Edge]]();

      edges.foreach { edge =>
        incomingEdges.getOrElseUpdate(edge.stopTo, ArrayBuffer[Edge]()) += edge;
        outgoingEdges.getOrElseUpdate(edge.stopFrom, ArrayBuffer[Edge]()) += edge;
      }

      private def _visitProcess[T](flow: Flow[DataStream], processName: String, op: (String, Map[Edge, T]) => T, visited: MMap[String, T]): T = {
        if (!visited.contains(processName)) {

          //TODO: need to check whether the checkpoint's data exist!!!!
          if (flow.hasCheckPoint(processName) && !flow.getCheckpointParentProcessId().equals("")) {
            val ret = op(processName, null);
            visited(processName) = ret;
            return ret;
          }
          //executes dependent processes
          val inputs =
            if (incomingEdges.contains(processName)) {
              //all incoming edges
              val edges = incomingEdges(processName);
              edges.map { edge =>
                edge ->
                  _visitProcess(flow, edge.stopFrom, op, visited);
              }.toMap
            }
            else {
              Map[Edge, T]();
            }

          val ret = op(processName, inputs);
          visited(processName) = ret;
          ret;
        }
        else {
          visited(processName);
        }
      }


      override def visit[T](flow: Flow[DataStream], op: (String, Map[Edge, T]) => T): Unit = {

        val ends = stops.keys.filterNot(outgoingEdges.contains(_));
        val visited = MMap[String, T]();
        ends.foreach {
          _visitProcess(flow, _, op, visited);
        }

      }

      override def visitStreaming[T](flow: Flow[DataStream], streamingStop: String, streamingData: T, op: (String, Map[Edge, T]) => T): Unit = {

        val visited = MMap[String, T]();
        visited(streamingStop) = streamingData

        val ends = stops.keys.filterNot(outgoingEdges.contains(_));
        ends.foreach {
          _visitProcess(flow, _, op, visited);
        }
      }
    }

  override def getFlowName(): String = {
    this.name
  }

  override def setFlowName(flowName: String): Unit = {
    this.name = flowName;
  }

  //get the processId
  override def getCheckpointParentProcessId(): String = {
    this.checkpointParentProcessId
  }

  override def setCheckpointParentProcessId(checkpointParentProcessId: String) = {
    this.checkpointParentProcessId = checkpointParentProcessId
  }

  override def getRunMode(): String = {
    this.runMode
  }

  override def setRunMode(runMode: String): Unit = {
    this.runMode = runMode
  }


  override def setFlowJson(flowJson: String): Unit = {
    this.flowJson = flowJson
  }

  override def getFlowJson(): String = {
    flowJson
  }

  override def setDriverMemory(driverMem: String): Unit = {
    this.driverMem = driverMem
  }

  override def getDriverMemory(): String = {
    this.driverMem
  }

  override def setExecutorNum(executorNum: String): Unit = {
    this.executorNum = executorNum
  }

  override def getExecutorNum(): String = {
    this.executorNum
  }

  override def setExecutorMem(executorMem: String): Unit = {
    this.executorMem = executorMem
  }

  override def getExecutorMem(): String = {
    this.executorMem
  }

  override def setExecutorCores(executorCores: String): Unit = {
    this.executorCores = executorCores
  }

  override def getExecutorCores(): String = {
    this.executorCores
  }


  override def setUUID(uuid: String): Unit = {
    this.uuid = uuid;
  }

  override def getUUID(): String = {
    this.uuid
  }

  override def show(): Unit = {}
}

trait AnalyzedFlowGraph[DataStream] {
  def visit[T](flow: Flow[DataStream], op: (String, Map[Edge, T]) => T): Unit;

  def visitStreaming[T](flow: Flow[DataStream], streamingStop: String, streamingData: T, op: (String, Map[Edge, T]) => T): Unit;
}

trait Process[DataStream] {
  def pid(): String;

  def awaitTermination();

  def awaitTermination(timeout: Long, unit: TimeUnit);

  def getFlow(): Flow[DataStream];

  def fork(child: Flow[DataStream]): Process[DataStream];

  def stop(): Unit;
}

trait ProcessContext[DataStream] extends Context[DataStream] {
  def getFlow(): Flow[DataStream];

  def getProcess(): Process[DataStream];
}


trait GroupContext[DataStream] extends Context[DataStream] {

  def getGroup(): Group[DataStream]

  def getGroupExecution(): GroupExecution

}

class JobInputStreamImpl[DataStream] () extends JobInputStream[DataStream] {
  //only returns DataFrame on calling read()
  val inputs = MMap[String, DataStream]();
  val inputsProperties = MMap[String, () => MMap[String, String]]()

  override def isEmpty: Boolean = inputs.isEmpty

  def attach(inputs: Map[Edge, JobOutputStreamImpl[DataStream]]) = {
    this.inputs ++= inputs.filter(x => x._2.contains(x._1.outport))
      .map(x => (x._1.inport, x._2.getDataFrame(x._1.outport)));

    this.inputsProperties ++= inputs.filter(x => x._2.contains(x._1.outport))
      .map(x => (x._1.inport, x._2.getDataFrameProperties(x._1.outport)));
  }


  override def ports(): Seq[String] = {
    inputs.keySet.toSeq;
  }

  override def read[DataType: ClassTag](): DataStream = {
    if (inputs.isEmpty)
      throw new NoInputAvailableException()

    read(inputs.head._1);
  }

  override def read[ataType: ClassTag](inport: String): DataStream = {
    inputs(inport).asInstanceOf[DataStream]
  }

  override def readProperties(): MMap[String, String] = {

    readProperties("")
  }

  override def readProperties(inport: String): MMap[String, String] = {

    inputsProperties(inport)()
  }
}

class JobOutputStreamImpl[DataStream]() extends JobOutputStream[DataStream] with Logging {
  private val defaultPort = "default"

  val mapDataFrame = MMap[String, DataStream]();

  val mapDataFrameProperties = MMap[String, () => MMap[String, String]]();

  override def write[DataType: ClassTag](data: DataStream): Unit = write("", data);

  override def sendError(): Unit = ???

  override def write[DataType: ClassTag](outport: String, data: DataStream): Unit = {
    mapDataFrame(outport) = data;
  }

  def contains(port: String) = mapDataFrame.contains(port);

  def getDataFrame(port: String) = mapDataFrame(port);

  def showDataDataStream(count: Int) = {

    mapDataFrame.foreach(en => {
      val portName = if (en._1.equals("")) "default" else en._1
      println(portName + " port: ")
      // en._2.asInstanceOf[DataStream].print()

    })
  }


  override def writeProperties(properties: MMap[String, String]): Unit = {

    writeProperties("", properties)

  }

  override def writeProperties(outport: String, properties: MMap[String, String]): Unit = {

    mapDataFrameProperties(outport) = () => properties

  }

  def getDataFrameProperties(port: String) = {
    if (!mapDataFrameProperties.contains(port)) {
      mapDataFrameProperties(port) = () => MMap[String, String]()

    }
    mapDataFrameProperties(port)
  }
}

class ProcessImpl[DataStream](flow: Flow[DataStream], runnerContext: Context[DataStream], runner: Runner[DataStream], parentProcess: Option[Process[DataStream]] = None)
  extends Process[DataStream] with Logging {

  val id = "process_" + IdGenerator.uuid() + "_" + IdGenerator.nextId[Process[DataStream]];
  val executionString = "" + id + parentProcess.map("(parent=" + _.toString + ")").getOrElse("");

  logger.debug(s"create process: $this, flow: $flow");
  flow.show();

  val process = this;
  val runnerListener = runner.getListener();
  val processContext = createContext(runnerContext);
  val latch = new CountDownLatch(1);
  var running = false;

  //val env = StreamExecutionEnvironment.getExecutionEnvironment

  val jobs = MMap[String, StopJobImpl[DataStream]]();
  flow.getStopNames().foreach { stopName =>
    val stop = flow.getStop(stopName);
    stop.initialize(processContext);

    val pe = new StopJobImpl(stopName, stop, processContext);
    jobs(stopName) = pe;
    //runnerListener.onJobInitialized(pe.getContext());
  }

  val analyzed = flow.analyze();
  val checkpointParentProcessId = flow.getCheckpointParentProcessId()


  analyzed.visit[JobOutputStreamImpl[DataStream]](flow, performStopByCheckpoint)


  //perform stop use checkpoint
  def performStopByCheckpoint(stopName: String, inputs: Map[Edge, JobOutputStreamImpl[DataStream]]) = {
    val pe = jobs(stopName);

    var outputs: JobOutputStreamImpl[DataStream] = null
    try {
      //runnerListener.onJobStarted(pe.getContext());

      println("Visit process " + stopName + "!!!!!!!!!!!!!")
      outputs = pe.perform(inputs);

      //outputs.showData(10)

      //runnerListener.onJobCompleted(pe.getContext());

    }
    catch {
      case e: Throwable =>
        //runnerListener.onJobFailed(pe.getContext());
        throw e;
    }

    outputs;
  }

  //env.execute(flow.getFlowName())

  /*val workerThread = new Thread(new Runnable() {
    def perform() {

      //val env = processContext.get[StreamExecutionEnvironment]()
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      println("StreamExecutionEnvironment in worderThread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

      val jobs = MMap[String, StopJobImpl]();
      flow.getStopNames().foreach { stopName =>
        val stop = flow.getStop(stopName);
        stop.initialize(processContext);

        val pe = new StopJobImpl(stopName, stop, processContext);
        jobs(stopName) = pe;
        //runnerListener.onJobInitialized(pe.getContext());
      }

      val analyzed = flow.analyze();
      val checkpointParentProcessId = flow.getCheckpointParentProcessId()


      analyzed.visit[JobOutputStreamImpl](flow,performStopByCheckpoint)


      //perform stop use checkpoint
      def performStopByCheckpoint(stopName: String, inputs: Map[Edge, JobOutputStreamImpl]) = {
        val pe = jobs(stopName);

        var outputs : JobOutputStreamImpl = null
        try {
          //runnerListener.onJobStarted(pe.getContext());

          println("Visit process " + stopName + "!!!!!!!!!!!!!")
          outputs = pe.perform(inputs);

          //runnerListener.onJobCompleted(pe.getContext());

        }
        catch {
          case e: Throwable =>
            //runnerListener.onJobFailed(pe.getContext());
            throw e;
        }

        outputs;
      }


      //env.execute(flow.getFlowName())

    }

    override def run(): Unit = {
      running = true;

      //onFlowStarted
      //runnerListener.onProcessStarted(processContext);
      try {
        perform();
        //onFlowCompleted
        //runnerListener.onProcessCompleted(processContext);
      }
      //onFlowFailed
      catch {
        case e: Throwable =>
          //runnerListener.onProcessFailed(processContext);
          throw e;
      }
      finally {
        latch.countDown();
        running = false;
      }
    }
  });*/

  //IMPORTANT: start thread
  //workerThread.start();

  override def toString(): String = executionString;

  override def awaitTermination(): Unit = {
    latch.await();
  }

  override def awaitTermination(timeout: Long, unit: TimeUnit): Unit = {
    latch.await(timeout, unit);
    if (running)
      stop();
  }

  override def pid(): String = id;

  override def getFlow(): Flow[DataStream] = flow;

  private def createContext(runnerContext: Context[DataStream]): ProcessContext[DataStream] = {

    new CascadeContext[DataStream](runnerContext) with ProcessContext[DataStream] {
      override def getFlow(): Flow[DataStream] = flow
      override def getProcess(): Process[DataStream] = process;
    }
  }


  override def fork(child: Flow[DataStream]): Process[DataStream] = {
    //add flow process stack
    val process = new ProcessImpl(child, runnerContext, runner, Some(this));
    runnerListener.onProcessForked(processContext, process.processContext);
    process;
  }

  //TODO: stopSparkJob()
  override def stop(): Unit = {
    /*if (!running)
      throw new ProcessNotRunningException(this);

    workerThread.interrupt();
    runnerListener.onProcessAborted(processContext);
    latch.countDown();*/
  }
}

class JobContextImpl[DataStream](job: StopJob[DataStream], processContext: ProcessContext[DataStream])
  extends CascadeContext(processContext)
    with JobContext[DataStream]
    with Logging {

  val is: JobInputStreamImpl[DataStream] = new JobInputStreamImpl[DataStream]();

  val os = new JobOutputStreamImpl[DataStream]();

  def getStopJob() = job;

  def getInputStream(): JobInputStream[DataStream] = is;

  def getOutputStream(): JobOutputStream [DataStream]= os;

  override def getProcessContext(): ProcessContext[DataStream] = processContext;
}

class StopJobImpl[DataStream](stopName: String, stop: Stop[DataStream], processContext: ProcessContext[DataStream])
  extends StopJob[DataStream] with Logging {
  val id = "job_" + IdGenerator.nextId[StopJob[DataStream]];
  val pec = new JobContextImpl(this, processContext);

  override def jid(): String = id;

  def getContext() = pec;

  def perform(inputs: Map[Edge, JobOutputStreamImpl[DataStream]]): JobOutputStreamImpl[DataStream] = {
    pec.getInputStream().asInstanceOf[JobInputStreamImpl[DataStream]].attach(inputs);
    stop.perform(pec.getInputStream(), pec.getOutputStream(), pec);
    pec.getOutputStream().asInstanceOf[JobOutputStreamImpl[DataStream]];
  }

  override def getStopName(): String = stopName;

  override def getStop(): Stop[DataStream] = stop;
}

trait Context[DataStream] {
  def get(key: String): Any;

  def get(key: String, defaultValue: Any): Any;

  def get[T]()(implicit m: Manifest[T]): T = {
    get(m.runtimeClass.getName).asInstanceOf[T];
  }

  def put(key: String, value: Any): this.type;

  def put[T](value: T)(implicit m: Manifest[T]): this.type =
    put(m.runtimeClass.getName, value);
}

class CascadeContext[DataStream](parent: Context[DataStream] = null) extends Context[DataStream] with Logging {
  val map = MMap[String, Any]();

  override def get(key: String): Any = internalGet(key,
    () => throw new ParameterNotSetException(key));

  override def get(key: String, defaultValue: Any): Any = internalGet(key,
    () => {
      logger.warn(s"value of '$key' not set, using default: $defaultValue");
      defaultValue;
    });

  def internalGet(key: String, op: () => Unit): Any = {
    if (map.contains(key)) {
      map(key);
    }
    else {
      if (parent != null)
        parent.get(key);
      else
        op();
    }
  };

  override def put(key: String, value: Any): this.type = {
    map(key) = value;
    this;
  }
}

class FlowException(msg: String = null, cause: Throwable = null) extends RuntimeException(msg, cause) {

}

class NoInputAvailableException extends FlowException() {

}

class ParameterNotSetException(key: String) extends FlowException(s"parameter not set: $key") {

}

//sub flow
class FlowAsStop[DataStream](flow: Flow[DataStream]) extends Stop[DataStream] {
  override def initialize(ctx: ProcessContext[DataStream]): Unit = {
  }

  override def perform(in: JobInputStream[DataStream], out: JobOutputStream[DataStream], pec: JobContext[DataStream]): Unit = {
    pec.getProcessContext().getProcess().fork(flow).awaitTermination();
  }
}

class ProcessNotRunningException[DataStream](process: Process[DataStream]) extends FlowException() {

}

class InvalidPathException(head: Any) extends FlowException() {

}