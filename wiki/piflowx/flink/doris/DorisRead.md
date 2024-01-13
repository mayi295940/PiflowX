# JDBCRead组件

### 组件说明

使用JDBC驱动向任意类型的关系型数据库读取数据。

### 计算引擎

flink

### 有界性

Scan Source: Bounded

Lookup Source: Sync Mode

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
| tableName                 | SCHEMA                    | ""  | 无   | 是    | 连接到JDBC表的名称。                                    | test                               |
| connectionMaxRetryTimeout | ConnectionMaxRetryTimeout | 60s | 无   | 否    | 最大重试超时时间，以秒为单位且不应该小于 1 秒。                       | 60s                                |
| fetchSize                 | FetchSize                 | ""  | 无   | 否    | 每次循环读取时应该从数据库中获取的行数。如果指定的值为 '0'，则该配置项会被忽略。      | 500                                |
| tableDefinition           | TableDefinition           | ""  | 无   | 是    | Flink table定义。                                  |                                    |
| properties                | PROPERTIES                | ""  | 无   | 否    | 连接器其他配置。                                        |                                    |

### JDBCRead示例配置

```json
{
  "flow": {
    "name": "JDBCReadTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "JDBCRead1",
        "bundle": "cn.piflow.bundle.flink.jdbc.JDBCRead",
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
        "uuid": "2222",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "100"
        }
      }
    ],
    "paths": [
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

本示例演示了通过`JDBCRead`从数据库`test`中读取`test`表数据，并使用`ShowData`组件将数据打印在控制台。

#### 演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/jdbcJDBCRead.gif)
