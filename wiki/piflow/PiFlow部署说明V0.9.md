# 1.Piflow Server

## 1.1 环境要求

1.已部署Spark2，Hadoop，Yarn，Hive(可选)

2.JDK1.8

3.scala 2.11.8

4.需使用的端口包括
  - 8002: PiFLow Server
  - 50002: h2db port of PiFlow Server
  - 6002: PiFlow Web service
  - 6001: PiFlow web Page access address
  - 6443: the default https listening port of tomcat

## 1.2 Release说明

https://github.com/cas-bigdatalab/piflow/releases/tag/v0.9

## 1.3安装文件

### 1.3.1 下载安装文件：

https://github.com/cas-bigdatalab/piflow/releases/download/v0.9/piflow-server-v0.9.tar.gz

将piflow-server-v0.9.tar.gz解压，如下图所示：

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
fs.defaultFS=hdfs://10.0.88.23:9000

#yarn resourcemanager.hostname
yarn.resourcemanager.hostname=10.0.88.23

#if you want to use hive, set hive metastore uris
hive.metastore.uris=thrift://10.0.88.23:9083

#show data in log, set 0 if you do not want to show data in logs
data.show=10

#server port
server.port=8002

#h2db port
h2.port=50002

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

https://github.com/cas-bigdatalab/piflow-web/releases/download/v0.9/piflow-web-v0.9.tar.gz

将piflow-web-v0.9.tar.gz解压，如下图所示：  
![](https://github.com/cas-bigdatalab/piflow/blob/master/doc/V0.8/web_folder.png?raw=true)

解压后内容说明：

(1)、piflow-web.jar 为 piflow-web的启动jar包。

(2)、tomcat为piflow-web启动前端的容器。

(3)、config.properties为配置文件。

(4)、start.sh 为启动脚本

(5)、stop.sh 为停止脚本

(6)、status.sh 为查看状态脚本

(7)、restart.sh 为重启脚本

(8)、piflowWeb.log 为日志文件

(9)、temp_v0.7.sh 为平滑升级的补丁脚本（不需要执行）

(10)、error.log 为平滑升级的补丁脚本的日志文件

 

**config.properties配置文件如下;**

如果想使用MySQL数据库则使用这份配置文件

```properties

server.port=6002
server.servlet.session.timeout=3600

syspara.interfaceUrlHead=http://10.0.90.155:8002
syspara.isIframe=true

# Total maximum value of uploaded files
spring.servlet.multipart.max-request-size=512MB
# Maximum value of a single file
spring.servlet.multipart.max-file-size=512MB

# data source
sysParam.datasource.type=mysql
# MySQL Configuration
#Configure the connection address of MySQL
spring.datasource.url = jdbc:mysql://10.0.85.81:3306/piflow_web_0.9?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false
#Configure database user name
spring.datasource.username=root
#Configuration database password
spring.datasource.password=123456
#Configure JDBC Driver
# Can not be configured, according to the URL automatic identification, recommended configuration
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

spring.flyway.locations=classpath:db/flyway-mysql/

# Log Coordination Standard
logging.level.org.flywaydb=warn
logging.level.com.nature.mapper=warn
logging.level.root=warn
logging.level.org.springframework.security=warn
logging.level.org.hibernate.SQL=warn


```



 

如果想使用H2DB数据库则使用这份配置文件

 ```properties

server.port=6002
server.servlet.session.timeout=3600

syspara.interfaceUrlHead=http://10.0.90.155:8002
syspara.isIframe=true

# Total maximum value of uploaded files
spring.servlet.multipart.max-request-size=512MB
# Maximum value of a single file
spring.servlet.multipart.max-file-size=512MB

# data source
sysParam.datasource.type=h2
# h2 Configuration
#Configure the connection address of H2DB
spring.datasource.url=jdbc:h2:file:/media/nature/linux_disk_0/PiFlow_DB/piflow_web
#Configure database user name
spring.datasource.username=Admin
#Configuration database password
spring.datasource.password=Admin
#Configure JDBC Driver
# Can not be configured, according to the URL automatic identification, recommended configuration
spring.datasource.driver-class-name=org.h2.Driver
##H2DB web console settings
spring.datasource.platform=h2
#After this configuration, h2 web consloe can be accessed remotely. Otherwise it can only be accessed locally.
spring.h2.console.settings.web-allow-others=true
#With this configuration, you can access h2 web consloe through YOUR_URL / h2. YOUR_URL is the access URL of your program.
spring.h2.console.path=/h2
#With this configuration, h2 web consloe will start when the program starts. Of course this is the default. If you don't want to start h2 web consloe when you start the program, then set it to false.
spring.h2.console.enabled=true

spring.flyway.locations=classpath:db/flyway-h2db/


# Log Coordination Standard
logging.level.org.flywaydb=warn
logging.level.com.nature.mapper=warn
logging.level.root=warn
logging.level.org.springframework.security=warn
logging.level.org.hibernate.SQL=warn

 ```



 

运行

```
  cd piflow-web 
  ./start.sh  
```



 

 

访问进行登陆注册：http://serverIp:6001

![](http://image-picgo.test.upcdn.net/img/20200602135741.png)
