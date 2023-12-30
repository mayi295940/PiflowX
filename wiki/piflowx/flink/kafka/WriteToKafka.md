# WriteToKafka组件

### 组件说明

将数据写入kafka。

### 计算引擎

flink

### 有界性

Streaming Append Mode

### 组件分组

kafka

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称         | 展示名称       | 默认值 | 允许值                                                                                                                       | 是否必填 | 描述                                                   | 例子                         |
| ---------- | ---------- | --- | ------------------------------------------------------------------------------------------------------------------------- | ---- | ---------------------------------------------------- | -------------------------- |
| kafka_host | KAFKA_HOST | ""  | 无                                                                                                                         | 是    | 逗号分隔的Kafka broker列表。                                 | 127.0.0.1:9092             |
| topic      | TOPIC      | ""  | 无                                                                                                                         | 否    | 写入的topic名。注意不支持topic列表。                              | test                       |
| schema     | SCHEMA     | ""  | 无                                                                                                                         | 否    | Kafka消息的schema信息。若不指定，将从上游输入数据推断。                    | id:int,name:string,age:int |
| format     | FORMAT     | ""  | Set("json", "csv", "avro", "parquet", "orc", "raw", "protobuf","debezium-json", "canal-json", "maxwell-json", "ogg-json") | 是    | 用来序列化或反序列化Kafka消息的格式。注意：该配置项和 'value.format' 二者必需其一。 | json                       |
| properties | PROPERTIES | ""  | 无                                                                                                                         | 否    | Kafka source连接器其他配置                                  |                            |

### WriteToKafka示例配置

```json
{
  "flow": {
    "name": "DataGenTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "DataGen1",
        "bundle": "cn.piflow.bundle.flink.common.DataGen",
        "properties": {
          "schema": "[{\"filedName\":\"id\",\"filedType\":\"INT\",\"kind\":\"sequence\",\"start\":1,\"end\":10000},{\"filedName\":\"name\",\"filedType\":\"STRING\",\"kind\":\"random\",\"length\":15},{\"filedName\":\"age\",\"filedType\":\"INT\",\"kind\":\"random\",\"max\":100,\"min\":1}]",
          "count": "100",
          "ratio": "5"
        }
      },
      {
        "uuid": "1111",
        "name": "WriteToKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.WriteToKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "test",
          "schema": "",
          "format": "json",
          "properties": "{}"
        }
      },
      {
        "uuid": "2222",
        "name": "ReadFromKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.ReadFromKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "test",
          "group": "test",
          "startup_mode": "earliest-offset",
          "schema": "id:int,name:string,age:int",
          "format": "json",
          "properties": "{}"
        }
      },
      {
        "uuid": "3333",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "5000"
        }
      }
    ],
    "paths": [
      {
        "from": "DataGen1",
        "outport": "",
        "inport": "",
        "to": "WriteToKafka1"
      },
      {
        "from": "WriteToKafka1",
        "outport": "",
        "inport": "",
        "to": "ReadFromKafka1"
      },
      {
        "from": "ReadFromKafka1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`DataGen`组件生成id，name，age3个字段100条数据，每秒生成5条数据，通过`WriteToKafka`组件将数据写入到kafka的test topic中，然后通过ReadFromKafka组件从test topic中读取数据，最后使用`ShowData`组件将数据打印在控制台。

#### 字段描述

```js
[
    {       
        "filedName": "id",
        "filedType": "INT",
        "kind": "sequence",
        "start": 1,
        "end": 10000
    },
        {       
        "filedName": "name",
        "filedType": "STRING",
        "kind": "random",
        "length": 15
    },
        {       
        "filedName": "age",
        "filedType": "INT",
        "kind": "random",
        "max": 100,
        "min": 1
    } 
]
```

1.id字段

id字段类型为INT,使用sequence生成器，序列生成器的起始值为1，结束值为10000.

2.name字段

name字段类型为STRING,使用random生成器，生成字符长度为15。

3.age字段

age字段类型为INT，使用random生成器，随机生成器的最小值为1，最大值为100。

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/kafkakafka%E6%BC%94%E7%A4%BA%E8%A7%86%E9%A2%91.gif)
