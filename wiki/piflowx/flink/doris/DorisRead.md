# DorisRead组件

### 组件说明

从Doris存储读取数据。

### 计算引擎

flink

### 有界性

目前Doris Source是有界流，不支持CDC方式读取。

### 组件分组

Doris

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值  | 允许值 | 是否必填 | 描述                                                                     | 例子                          |
| --------------- | --------------- | ---- | --- | ---- | ---------------------------------------------------------------------- | --------------------------- |
| fenodes         | Fenodes         | ""   | 无   | 是    | Doris FE http地址， 支持多个地址，使用逗号分隔。                                        | 127.0.0.1:8030              |
| benodes         | Benodes         | ""   | 无   | 否    | Doris BE http地址， 支持多个地址，使用逗号分隔。                                        |                             |
| username        | Username        | ""   | 无   | 是    | 访问Doris的用户名。                                                           | root                        |
| password        | Password        | ""   | 无   | 是    | 访问Doris的密码。                                                            | 123456                      |
| tableIdentifier | TableIdentifier | ""   | 无   | 是    | Doris表名。                                                               | db.tbl                      |
| jdbcUrl         | JdbcUrl         | ""   | 无   | 否    | jdbc连接信息。                                                              | jdbc:mysql://127.0.0.1:9030 |
| batchSize       | BatchSize       | 1024 | 无   | 否    | 一次从 BE 读取数据的最大行数。增大此数值可减少 Flink 与 Doris 之间建立连接的次数。 从而减轻网络延迟所带来的额外时间开销。 | 1024                        |
| readField       | ReadField       | ""   | 无   | 否    | 读取Doris表的列名列表，多列之间使用逗号分隔。                                              |                             |
| queryFilter     | QueryFilter     | ""   | 无   | 否    | 过滤读取数据的表达式，此表达式透传给Doris。Doris使用此表达式完成源端数据过滤。                           | age=18                      |
| tableDefinition | TableDefinition | ""   | 无   | 是    | Flink table定义。                                                         |                             |
| properties      | PROPERTIES      | ""   | 无   | 否    | 连接器其他配置。                                                               |                             |

### DorisRead示例配置

```json
{
  "flow": {
    "name": "DorisWriteTest",
    "uuid": "1234",
    "stops": [
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
        "name": "DorisWrite1",
        "bundle": "cn.piflow.bundle.flink.doris.DorisWrite",
        "properties": {
          "fenodes": "192.168.186.102:8030",
          "username": "root",
          "password": "",
          "sinkLabelPrefix": "doris_label2",
          "tableIdentifier": "demo.test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\"},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "3333",
        "name": "DorisRead1",
        "bundle": "cn.piflow.bundle.flink.doris.DorisRead",
        "properties": {
          "fenodes": "192.168.186.102:8030",
          "username": "root",
          "password": "",
          "tableIdentifier": "demo.test",
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
        "from": "DataGen1",
        "outport": "",
        "inport": "",
        "to": "DorisWrite1"
      },
      {
        "from": "DorisWrite1",
        "outport": "",
        "inport": "",
        "to": "DorisRead1"
      },
      {
        "from": "DorisRead1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### DorisRead示例说明

本示例演示了通过`DataGen`生成100条测试数据，然后使用DorisWrite组件写入doris，然后使用DorisRead组件将数据从doris中读取，最后通过`showData`组件打印到控制台。

#### 演示DEMO



[PiflowX-Droris读写组件_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV18i4y1B7wj/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/dorisDorisRead.gif)
