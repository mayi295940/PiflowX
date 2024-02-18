# JDBCWrite组件

### 组件说明

使用JDBC驱动向任意类型的关系型数据库写入数据。

### 计算引擎

flink

### 有界性

Sink: Batch

Sink: Streaming Append & Upsert Mode

### 组件分组

Jdbc

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称                        | 展示名称                      | 默认值 | 允许值 | 是否必填 | 描述                                              | 例子                                 |
| ------------------------- | ------------------------- | --- | --- | ---- | ----------------------------------------------- | ---------------------------------- |
| url                       | Url                       | ""  | 无   | 是    | JDBC数据库url。                                     | jdbc:mysql://127.0.0.1:3306/dbname |
| driver                    | Driver                    | ""  | 无   | 否    | 用于连接到此URL的JDBC驱动类名，如果不设置，将自动从URL中推导。            | com.mysql.cj.jdbc.Driver           |
| username                  | Username                  | ""  | 无   | 否    | JDBC用户名。如果指定了username和password中的任一参数，则两者必须都被指定。 | root                               |
| password                  | Password                  | ""  | 无   | 否    | JDBC密码。                                         | 123456                             |
| tableName                 | DBTable                   | ""  | 无   | 是    | 连接到JDBC表的名称。                                    | test                               |
| connectionMaxRetryTimeout | ConnectionMaxRetryTimeout | 60s | 无   | 否    | 最大重试超时时间，以秒为单位且不应该小于 1 秒。                       | 60s                                |
| tableDefinition           | TableDefinition           | ""  | 无   | 是    | Flink table定义。                                  |                                    |
| properties                | PROPERTIES                | ""  | 无   | 否    | 连接器其他配置。                                        |                                    |

### JDBCWrite示例配置

```json
{
  "flow": {
    "name": "JDBCWriteTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "JDBCExecuteSql1",
        "bundle": "cn.piflow.bundle.flink.jdbc.JDBCExecuteSql",
        "properties": {
          "url": "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true",
          "username": "root",
          "password": "123456",
          "driver": "com.mysql.cj.jdbc.Driver",
          "sql": "CREATE TABLE IF NOT EXISTS `test` (`id` int DEFAULT NULL,`name` varchar(20) DEFAULT NULL,`age` int DEFAULT NULL);"
        }
      },
      {
        "uuid": "1111",
        "name": "DataGen1",
        "bundle": "cn.piflow.bundle.flink.common.DataGen",
        "properties": {
          "schema": "[{\"filedName\":\"id\",\"filedType\":\"INT\",\"kind\":\"sequence\",\"start\":1,\"end\":10000},{\"filedName\":\"name\",\"filedType\":\"STRING\",\"kind\":\"random\",\"length\":15},{\"filedName\":\"age\",\"filedType\":\"INT\",\"kind\":\"random\",\"max\":100,\"min\":1}]",
          "count": "100",
          "ratio": "50"
        }
      },
      {
        "uuid": "2222",
        "name": "JDBCWrite1",
        "bundle": "cn.piflow.bundle.beam.jdbc.JDBCWrite",
        "properties": {
          "url": "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true",
          "username": "root",
          "password": "123456",
          "tableName": "test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\"},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "3333",
        "name": "JDBCRead1",
        "bundle": "cn.piflow.bundle.beam.jdbc.JDBCRead",
        "properties": {
          "url": "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true",
          "username": "root",
          "password": "123456",
          "tableName": "test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\"},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "4444",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "100"
        }
      }
    ],
    "paths": [
      {
        "from": "JDBCExecuteSql1",
        "outport": "",
        "inport": "",
        "to": "DataGen1"
      },
      {
        "from": "DataGen1",
        "outport": "",
        "inport": "",
        "to": "JDBCWrite1"
      },
      {
        "from": "JDBCWrite1",
        "outport": "",
        "inport": "",
        "to": "JDBCRead1"
      },
      {
        "from": "JDBCRead1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

1. 首先使用`JDBCExecuteSql`组件执行建表DDL语句，往数据库添加表；

2. 使用`DataGen`组件生成100条测试数据；

3. 使用`JDBCWrite`组件将测试数据写入到数据库；

4. 验证写入的结果，使用`JDBCRead`组件读取数据，并使用`ShowData`组件将结果打印在控制台。

#### 演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/jdbcJDBCWrite.gif)
