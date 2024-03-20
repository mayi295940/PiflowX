# OracleCdc组件

### 组件说明

Oracle CDC连接器允许从Oracle数据库读取快照数据和增量数据。

### 计算引擎

flink

### 组件分组

cdc

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值  | 允许值 | 是否必填 | 描述                                                                                                      | 例子        |
| --------------- | --------------- | ---- | --- | ---- | ------------------------------------------------------------------------------------------------------- | --------- |
| hostname        | Hostname        | ""   | 无   | 是    | Oracle数据库服务器的IP地址或主机名。如果url不为空，则可能未配置hostname，否则hostname不能为空。                                           | 127.0.0.1 |
| username        | Username        | ""   | 无   | 是    | 连接到Oracle数据库服务器时要使用的Oracle用户的名称。                                                                        | root      |
| password        | Password        | ""   | 无   | 是    | 连接Oracle数据库服务器时使用的密码。                                                                                   | 123456    |
| databaseName    | DatabaseName    | ""   | 无   | 是    | 要监视的Oracle服务器的数据库名称。                                                                                    | test      |
| schemaName      | Schema          | ""   | 无   | 是    | 要监视的Oracle数据库的Schema                                                                                    |           |
| tableName       | TableName       | ""   | 无   | 是    | 需要监视的Oracle数据库的表名。                                                                                      | test      |
| port            | Port            | 1521 | 无   | 否    | Oracle数据库服务器的整数端口号。                                                                                     | 1521      |
| url             |                 | ""   | 无   | 否    | Oracle数据库服务器的JdbcUrl。如果配置了hostname和port参数，则默认情况下URL由SID格式的hostname port database-name连接。否则，您需要配置 URL参数。 |           |
| tableDefinition | TableDefinition | ""   | 无   | 是    | Flink table定义。                                                                                          |           |
| properties      | PROPERTIES      | ""   | 无   | 否    | 连接器其他配置。                                                                                                |           |

### OracleCdc示例配置

```json
{
  "flow": {
    "name": "OracleCdcTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "OracleCdc1",
        "bundle": "cn.piflow.bundle.flink.cdc.oracle.OracleCdc",
        "properties": {
          "hostname": "192.168.186.102",
          "username": "dbzuser",
          "password": "dbz",
          "databaseName": "ORCLCDB",
          "tableName": "PRODUCTS_TEST",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\",\"primaryKey\":true},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "2222",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "100",
          "changeLog": true
        }
      }
    ],
    "paths": [
      {
        "from": "OracleCdc1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`OracleCdc`从数据库`ORCLCDB`中读取`PRODUCTS_TEST`表数据，并使用`ShowChangeLogData`组件将数据打印在控制台。

#### 演示DEMO

[PiflowX组件-OracleCdc_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV16k4y1D71E/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
