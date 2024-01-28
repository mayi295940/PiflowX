# Elasticsearch7Write组件

### 组件说明

将数据写入到Elasticsearch-7引擎的索引中。

### 计算引擎

flink

### 组件分组

ElasticSearch

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值 | 允许值 | 是否必填 | 描述                                                                                     | 例子                                          |
| --------------- | --------------- | --- | --- | ---- | -------------------------------------------------------------------------------------- | ------------------------------------------- |
| hosts           | hosts           | ""  | 无   | 是    | 要连接到的一台或多台Elasticsearch主机。                                                             | http://host_name:9092;http://host_name:9093 |
| index           | index           | ""  | 无   | 是    | Elasticsearch中每条记录的索引。可以是一个静态索引（例如 'myIndex'）或一个动态索引（例如 'index-{log_ts\|yyyy-MM-dd}'）。 | myIndex                                     |
| username        | Username        | ""  | 无   | 否    | 用于连接Elasticsearch实例的用户名。                                                               | root                                        |
| password        | Password        | ""  | 无   | 否    | 用于连接Elasticsearch实例的密码。如果配置了username，则此选项也必须配置为非空字符串。                                  | 123456                                      |
| tableDefinition | TableDefinition | ""  | 无   | 是    | Flink table定义。                                                                         |                                             |
| properties      | PROPERTIES      | ""  | 无   | 否    | 连接器其他配置。                                                                               |                                             |

### Elasticsearch7Write示例配置

```json
{
  "flow": {
    "name": "Elasticsearch7WriteTest",
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
        "name": "Elasticsearch7Write1",
        "bundle": "cn.piflow.bundle.flink.es7.Elasticsearch7Write",
        "properties": {
          "hosts": "192.168.186.102:9200",
          "index": "test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\"},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "3333",
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
        "to": "Elasticsearch7Write1"
      },
      {
        "from": "DataGen1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`DataGen`生成100条测试数据，并使用`Elasticsearch7Write`组件将数据写入到es的test索引中。

#### 演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/cdcMysqlCdc.gif)



[PiflowX-数据写入ES_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1hC4y1C7u2/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
