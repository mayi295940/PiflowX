# MysqlCdc组件

### 组件说明

MySQL CDC连接器允许从MySQL数据库读取快照数据和增量数据。

### 计算引擎

flink

### 组件分组

cdc

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值  | 允许值 | 是否必填                 | 描述                                                                                                                                                                    | 例子                                                                                                                                                                                                                                                                                                           |
| --------------- | --------------- | ---- | --- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| hostname        | Hostname        | ""   | 无   | 是                    | MySQL 数据库服务器的IP地址或主机名。                                                                                                                                                | 127.0.0.1                                                                                                                                                                                                                                                                                                    |
| username        | Username        | ""   | 无   | 是                    | 连接到MySQL数据库服务器时要使用的MySQL用户的名称。                                                                                                                                        | root                                                                                                                                                                                                                                                                                                         |
| password        | Password        | ""   | 无   | 连接MySQL数据库服务器时使用的密码。 | JDBC用户名。如果指定了username和password中的任一参数，则两者必须都被指定。                                                                                                                       | 123456                                                                                                                                                                                                                                                                                                       |
| databaseName    | DatabaseName    | ""   | 无   | 是                    | 要监视的MySQL服务器的数据库名称。数据库名称还支持正则表达式，以监视多个与正则表达式匹配的表。                                                                                                                     | test                                                                                                                                                                                                                                                                                                         |
| tableName       | SCHEMA          | ""   | 无   | 是                    | 连接到JDBC表的名称。                                                                                                                                                          | test                                                                                                                                                                                                                                                                                                         |
| tableName       | TableName       | ""   | 无   | 否                    | 需要监视的 MySQL 数据库的表名。表名支持正则表达式，以监视满足正则表达式的多个表。注意：MySQL CDC 连接器在正则匹配表名时，会把用户填写的 database-name， table-name 通过字符串 `\\.` 连接成一个全路径的正则表达式，然后使用该正则表达式和 MySQL 数据库中表的全限定名进行正则匹配。 | test                                                                                                                                                                                                                                                                                                         |
| port            | Port            | 3306 | 无   | 否                    | MySQL数据库服务器的整数端口号。。                                                                                                                                                   | 3306                                                                                                                                                                                                                                                                                                         |
| serverId        | ServerId        | ""   | 无   | 否                    |                                                                                                                                                                       | 读取数据使用的 server id，server id 可以是个整数或者一个整数范围，比如 '5400' 或 '5400-5408', 建议在 'scan.incremental.snapshot.enabled' 参数为启用时，配置成整数范围。因为在当前 MySQL 集群中运行的所有 slave 节点，标记每个 salve 节点的 id 都必须是唯一的。 所以当连接器加入 MySQL 集群作为另一个 slave 节点（并且具有唯一 id 的情况下），它就可以读取 binlog。 默认情况下，连接器会在 5400 和 6400 之间生成一个随机数，但是我们建议用户明确指定 Server id。 |
| tableDefinition | TableDefinition | ""   | 无   | 是                    | Flink table定义。                                                                                                                                                        |                                                                                                                                                                                                                                                                                                              |
| properties      | PROPERTIES      | ""   | 无   | 否                    | 连接器其他配置。                                                                                                                                                              |                                                                                                                                                                                                                                                                                                              |

### MysqlCdc示例配置

```json
{
  "flow": {
    "name": "MysqlCdcTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "MysqlCdc1",
        "bundle": "cn.piflow.bundle.flink.cdc.mysql.MysqlCdc",
        "properties": {
          "hostname": "127.0.0.1",
          "port": "3306",
          "username": "root",
          "password": "123456",
          "databaseName": "test",
          "tableName": "test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\",\"primaryKey\":true},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{\"server-time-zone\":\"Asia/Shanghai\"}"
        }
      },
      {
        "uuid": "2222",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowChangeLogData",
        "properties": {
          "showNumber": "100"
        }
      }
    ],
    "paths": [
      {
        "from": "MysqlCdc1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`MysqlCdc`从数据库`test`中读取`test`表数据，并使用`ShowChangeLogData`组件将数据打印在控制台。

#### 演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/cdcMysqlCdc.gif)
