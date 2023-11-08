![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-logo2.png)
πFlow是一个简单易用，功能强大的大数据流水线系统。

## 目录

- [特性](#特性)
- [架构](#架构)
- [要求](#要求)
- [开始](#开始)
- [Docker镜像](#Docker镜像)
- [页面展示](#页面展示)
- [联系我们](#联系我们)

## 特性

- 简单易用
  - 可视化配置流水线
  - 监控流水线
  - 查看流水线日志
  - 检查点功能
  - 流水线调度
  
- 扩展性强:
  - 支持自定义开发数据处理组件
  
- 性能优越：
  - 基于分布式计算引擎Spark开发 
  
- 功能强大：
  - 提供100+的数据处理组件
  - 包括Hadoop 、Spark、MLlib、Hive、Solr、Redis、MemCache、ElasticSearch、JDBC、MongoDB、HTTP、FTP、XML、CSV、JSON等
  - 集成了微生物领域的相关算法

## 架构
![](https://gitee.com/opensci/piflow/raw/master/doc/architecture.png) 
## 要求
* JDK 1.8
* Scala-2.11.8
* Apache Maven 3.1.0 
* Spark-2.1.0 及以上版本
* Hadoop-2.6.0 

## 开始
### Build πFlow:  
- `install external package`
          
          mvn install:install-file -Dfile=/../piflow/piflow-bundle/lib/spark-xml_2.11-0.4.2.jar -DgroupId=com.databricks -DartifactId=spark-xml_2.11 -Dversion=0.4.2 -Dpackaging=jar
          mvn install:install-file -Dfile=/../piflow/piflow-bundle/lib/java_memcached-release_2.6.6.jar -DgroupId=com.memcached -DartifactId=java_memcached-release -Dversion=2.6.6 -Dpackaging=jar
          mvn install:install-file -Dfile=/../piflow/piflow-bundle/lib/ojdbc6-11.2.0.3.jar -DgroupId=oracle -DartifactId=ojdbc6 -Dversion=11.2.0.3 -Dpackaging=jar
          mvn install:install-file -Dfile=/../piflow/piflow-bundle/lib/edtftpj.jar -DgroupId=ftpClient -DartifactId=edtftp -Dversion=1.0.0 -Dpackaging=jar
          

- `mvn clean package -Dmaven.test.skip=true`

          [INFO] Replacing original artifact with shaded artifact.
          [INFO] Reactor Summary:
          [INFO]
          [INFO] piflow-project ..................................... SUCCESS [  4.369 s]
          [INFO] piflow-core ........................................ SUCCESS [01:23 min]
          [INFO] piflow-configure ................................... SUCCESS [ 12.418 s]
          [INFO] piflow-bundle ...................................... SUCCESS [02:15 min]
          [INFO] piflow-server ...................................... SUCCESS [02:05 min]
          [INFO] ------------------------------------------------------------------------
          [INFO] BUILD SUCCESS
          [INFO] ------------------------------------------------------------------------
          [INFO] Total time: 06:01 min
          [INFO] Finished at: 2020-05-21T15:22:58+08:00
          [INFO] Final Memory: 118M/691M
          [INFO] ------------------------------------------------------------------------


### 运行 πflow Server：

- `Intellij上运行PiFlow Server`:   
  - 下载 piflow: git clone https://github.com/cas-bigdatalab/piflow.git
  - 将PiFlow导入到Intellij
  - 编辑配置文件config.properties
  - Build PiFlow jar包:   
    - Run --> Edit Configurations --> Add New Configuration --> Maven  
    - Name: package
    - Command line: clean package -Dmaven.test.skip=true -X  
    - run 'package' (piflow jar file will be built in ../piflow/piflow-server/target/piflow-server-0.9.jar)  
    
  - 运行 HttpService:   
    - Edit Configurations --> Add New Configuration --> Application  
    - Name: HttpService
    - Main class : cn.piflow.api.Main  
    - Environment Variable: SPARK_HOME=/opt/spark-2.2.0-bin-hadoop2.6(change the path to your spark home)  
    - run 'HttpService'
  
  - 测试 HttpService:   
    - 运行样例流水线: ../piflow/piflow-server/src/main/scala/cn/piflow/api/HTTPClientStartMockDataFlow.scala
    - 需要修改API中的server ip 和 port
  
  
- `通过Release版本运行PiFlow`:
  - 根据需求下载最新版本PiFlow:  
    https://github.com/cas-bigdatalab/piflow/releases/download/v1.0/piflow-server-v1.0.tar.gz    
    
  - 解压piflow-server-v1.0.tar.gz:  
    tar -zxvf piflow-server-v1.0.tar.gz
    
  - 编辑配置文件config.properties  
  
  - 运行、停止、重启PiFlow Server  
    start.sh、stop.sh、 restart.sh、 status.sh  
  
  - 测试 PiFlow Server
    - 设置环境变量 PIFLOW_HOME  
      - vim /etc/profile  
        export PIFLOW_HOME=/yourPiflowPath  
      	export PATH=$PATH:$PIFLOW_HOME/bin  
        
      - 运行如下命令   
        piflow flow start example/mockDataFlow.json  
        piflow flow stop appID  
        piflow flow info appID  
        piflow flow log appID  
      
        piflow flowGroup start example/mockDataGroup.json  
        piflow flowGroup stop groupId  
        piflow flowGroup info groupId  
        
- `如何配置config.properties`
     
      #spark and yarn config
      spark.master=yarn
      spark.deploy.mode=cluster
      
      #hdfs default file system
      fs.defaultFS=hdfs://10.0.86.191:9000
      
      #yarn resourcemanager.hostname
      yarn.resourcemanager.hostname=10.0.86.191
      
      #if you want to use hive, set hive metastore uris
      #hive.metastore.uris=thrift://10.0.88.71:9083
      
      #show data in log, set 0 if you do not want to show data in logs
      data.show=10
      
      #server port
      server.port=8002
      
      #h2db port
      h2.port=50002
  
### 运行πFlow Web请到如下链接，πFlow Server 与 πFlow Web版本要对应：
  - https://github.com/cas-bigdatalab/piflow-web/releases/tag/v1.0  
  
  
### 接口Restful API：

- flow json（可查看piflow-bin/example文件夹下的流水线样例）
  <details>
    <summary>flow example</summary>
    <pre>
      <code>
        {
          "flow": {
            "name": "MockData",
            "executorMemory": "1g",
            "executorNumber": "1",
            "uuid": "8a80d63f720cdd2301723b7461d92600",
            "paths": [
              {
                "inport": "",
                "from": "MockData",
                "to": "ShowData",
                "outport": ""
              }
            ],
            "executorCores": "1",
            "driverMemory": "1g",
            "stops": [
              {
                "name": "MockData",
                "bundle": "cn.piflow.bundle.common.MockData",
                "uuid": "8a80d63f720cdd2301723b7461d92604",
                "properties": {
                  "schema": "title:String, author:String, age:Int",
                  "count": "10"
                },
                "customizedProperties": {
                }
              },
              {
                "name": "ShowData",
                "bundle": "cn.piflow.bundle.common.ShowData",
                "uuid": "8a80d63f720cdd2301723b7461d92602",
                "properties": {
                  "showNumber": "5"
                },
                "customizedProperties": {
                }
              }
            ]
          }
        }
       </code>
    </pre>
  </details>
- CURL方式：
  - curl -0 -X POST http://10.0.86.191:8002/flow/start -H "Content-type: application/json" -d 'this is your flow json'
  
- 命令行方式： 
  - set PIFLOW_HOME  
    vim /etc/profile  
  	export PIFLOW_HOME=/yourPiflowPath/piflow-bin  
    export PATH=\$PATH:\$PIFLOW_HOME/bin  

  - command example  
    piflow flow start yourFlow.json  
    piflow flow stop appID  
    piflow flow info appID  
    piflow flow log appID  

    piflow flowGroup start yourFlowGroup.json  
    piflow flowGroup stop groupId  
    piflow flowGroup info groupId  
    
## Docker镜像  

  - 拉取Docker镜像  
    docker pull registry.cn-hangzhou.aliyuncs.com/cnic_piflow/piflow:v1.1    
    
  - 查看Docker镜像的信息    
    docker images
     
  - 通过镜像Id运行一个Container，所有PiFlow服务会自动运行。请注意设置HOST_IP   
    docker run -h master -itd --env HOST_IP=\*.\*.\*.\* --name piflow-v1.1 -p 6001:6001 -p 6002:6002  [imageID]  
    
  - 访问 "HOST_IP:6001", 启动时间可能有些慢，需要等待几分钟
  
  - if somethings goes wrong,  all the application are in /opt  folder  
  
## 页面展示
- `登录`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-login.png)
  
- `流水线列表`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-flowlist.png)
  
- `创建流水线`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-createflow.png)
  
- `配置流水线`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-flowconfig.png)
  
- `运行流水线`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-loadflow.png)
  
- `监控流水线`:  

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-monitor.png)

- `流水线日志`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-log.png)
  
- `流水线组列表`:  

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-group-list.png)

- `配置流水线组`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-configure-group.png)

- `监控流水线组`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-monitor-group.png)

- `运行态流水线列表`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-processlist.png)
  
- `流水线模板列表`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-templatelist.png)
  
- `数据源`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-datasourcelist.png)

- `调度`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-schedulelist.png)
  
- `自定义组件`:

  ![](https://gitee.com/opensci/piflow/raw/master/doc/piflow-stophublist.png)
  
  
## 联系我们
- Name:吴老师  
- Mobile Phone：18910263390  
- WeChat：18910263390  
- Email: wzs@cnic.cn  
- QQ Group：1003489545  
  ![](https://gitee.com/opensci/piflow/raw/master/doc/PiFlowUserGroup_QQ.jpeg)
- WeChat group is valid for 7 days  
  ![](https://gitee.com/opensci/piflow/raw/master/doc/PiFlowUserGroup.png)
