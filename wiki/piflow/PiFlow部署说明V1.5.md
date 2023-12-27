# 1.Piflow Server

## 1.1 环境要求

1.已部署Spark2，Hadoop，Yarn，Hive(可选)，Docker(可选)

2.JDK1.8

3.scala 2.11.8

4.需使用的端口包括
  - 8002: PiFLow Server
  - 50002: h2db port of PiFlow Server
  - 6002: PiFlow Web service
  - 6001: PiFlow web Page access address
  - 6443: the default https listening port of tomcat

## 1.2 Release说明

https://github.com/cas-bigdatalab/piflow/releases/tag/v1.5

## 1.3安装文件

### 1.3.1 下载安装文件：

https://github.com/cas-bigdatalab/piflow/releases/download/v1.5/piflow-server-v1.5.tar.gz

将piflow-server-v1.5.tar.gz解压，如下图所示：

![](https://github.com/cas-bigdatalab/piflow/blob/master/doc/V0.8/jar_foler.png?raw=true)

1. bin为PiFlow命令行工具；

2. classpath为用户自定开发组件Stop放置路径；

3. config.properties为配置文件；

4. lib为piflowServer所需jar包；piflow-server-0.9.jar为piflowServer本身jar

5. logs为PiFlow日志目录

6. start.sh、restart.sh、stop.sh、status.sh为piflowServer启动停止脚本。

7. scala为可编程脚本存放目录

8. sparkJar为spark依赖jar包目录

9. flowFile为流水线及流水线组json存放目录

10. example为流水线及流水线组配置样例

### 1.3.2 Config.properties配置文件如下;

```properties
#Spark master and deploy mode
spark.master=yarn
 spark.deploy.mode=cluster
 
#hdfs default file system
fs.defaultFS=hdfs://10.0.85.83:9000

#yarn resourcemanager hostname
yarn.resourcemanager.hostname=10.0.85.83

#if you want to use hive, set hive metastore uris
#hive.metastore.uris=thrift://10.0.85.83:9083

#show data in log, set 0 if you do not show the logs
data.show=10

#monitor the throughput of flow
monitor.throughput=true

#server port
server.port=8001

#h2db port
h2.port=50001

#If you want to upload python stop,please set hdfs configs
#example hdfs.cluster=hostname:hostIP
#hdfs.cluster=master:127.0.0.1
#hdfs.web.url=master:50070
```



 

## 1.3 环境配置

配置集群的环境变量（自定义按需配置）

```
export JAVA_HOME=/opt/java
export JRE_HOME=/opt/java/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH

export HADOOP_HOME=/opt/hadoop-2.6.0
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

export HIVE_HOME=/opt/apache-hive-2.3.6-bin
export PATH=$PATH:$HIVE_HOME/bin

export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export PATH=$PATH:$SPARK_HOME/bin

export SCALA_HOME=/opt/scala-2.11.8
export PATH=$PATH:$SCALA_HOME/bin

export PIFLOW_HOME=/data/piflowServer
export PATH=$PATH:${PIFLOW_HOME}/bin

export DISPLAY=

```

配置Spark-env.sh文件，环境根据实际需求进行修改

```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib64/python3.6/site-packages/jep/

```

## 1.4 运行

```
  ./start.sh   
```







 

# 2.Piflow Web

## 1.1 环境要求

1. MYSQL5.7

2. JDK1.8

## 1.2 项目部署

下载安装文件：

https://github.com/cas-bigdatalab/piflow-web/releases/download/v1.5/piflow-web-v1.5.tar.gz

将piflow-web-v1.0.tar.gz解压，如下图所示：  
![](https://github.com/cas-bigdatalab/piflow/blob/master/doc/V1.5/w01.png?raw=true)

解压后内容说明：

(1)、piflow-tomcat 为piflow-web启动容器。

(2)、config.properties为配置文件。

(3)、start.sh 为启动脚本

(4)、stop.sh 为停止脚本

(5)、status.sh 为查看状态脚本

(6)、restart.sh 为重启脚本

(7)、logs 为日志路径

(8)、storage 为web文件存储路径

(9)、temp_v0.7.sh 为平滑升级的补丁脚本（不需要执行）

 

**config.properties配置文件如下;**

如果想使用MySQL数据库则使用这份配置文件

```properties
server.servlet.session.timeout=3600
syspara.interfaceUrlHead=http://127.0.0.1:8002
syspara.livyServer=http://127.0.0.1:8998
server.hdfs.url=None
syspara.isIframe=true

# Total maximum value of uploaded files
spring.servlet.multipart.max-request-size=512MB
# Maximum value of a single file
spring.servlet.multipart.max-file-size=512MB

# data source
sysParam.datasource.type=mysql
# MySQL Configuration
#Configure the connection address of MySQL
spring.datasource.url = jdbc:mysql://127.0.0.1:3306/piflow_web?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false
#Configure database user name
spring.datasource.username=nature
#Configuration database password
spring.datasource.password=123456
#Configure JDBC Driver
# Can not be configured, according to the URL automatic identification, recommended configuration
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

spring.flyway.locations=classpath:db/flyway-mysql/

# Log Coordination Standard
logging.level.cn.cnic.*.mapper.*=warn
logging.level.root=warn
logging.level.org.flywaydb=warn
logging.level.org.springframework.security=warn
logging.level.org.hibernate.SQL=warn

# If you need to upload python stop,please set docker.host
#docker.host=tcp://localhost:2375
#If you want to push docker images,please set these params
#docker.tls.verify=false
#docker.registry.url=http://localhost:7185
#docker.project.name=piflow
#docker.registry.user.name=admin
#docker.registry.password=admin

```


运行

```
  cd piflow-web 
  ./start.sh  
```



 

 

访问进行登陆注册：http://serverIp:6001

![](http://image-picgo.test.upcdn.net/img/20200602135741.png)
