# ReadFromUpsertKafka组件

### 组件说明

upsert方式从Kafka topic中读取数据。

### 计算引擎

flink

### 有界性

Unbounded

### 组件分组

kafka

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称                   | 展示名称               | 默认值 | 允许值                        | 是否必填 | 描述                                                                                                                                                                                                                                                                             | 例子             |
| -------------------- | ------------------ | --- | -------------------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------- |
| kafka_host           | KAFKA_HOST         | ""  | 无                          | 是    | 逗号分隔的Kafka broker列表。                                                                                                                                                                                                                                                           | 127.0.0.1:9092 |
| topic                | TOPIC              | ""  | 无                          | 是    | 用于写入Kafka topic名称。                                                                                                                                                                                                                                                             | topic-1        |
| tableDefinition      | TableDefinition    | ""  | 无                          | 是    | Flink table定义。                                                                                                                                                                                                                                                                 |                |
| key_format           | keyFormat          | ""  | Set("json", "csv", "avro") | 是    | 用于对Kafka消息中key部分反序列化的格式。key字段由PRIMARY KEY语法指定。                                                                                                                                                                                                                                 | json           |
| value_format         | ValueFormat        | ""  | Set("json", "csv", "avro") | 是    | 用于对Kafka消息中value部分反序列化的格式                                                                                                                                                                                                                                                      | json           |
| value_fields_include | ValueFieldsInclude | ALL | Set("ALL", "EXCEPT_KEY")   | 是    | 控制哪些字段应该出现在 value 中。可取值："ALL：消息的 value 部分将包含 schema 中所有的字段包括定义为主键的字段。"EXCEPT_KEY：记录的 value 部分包含 schema 的所有字段，定义为主键的字段除外。                                                                                                                                                       | ALL            |
| key_fields_prefix    | KeyFieldsPrefix    | ""  | 无                          | 否    | 为所有消息键（Key）格式字段指定自定义前缀，以避免与消息体（Value）格式字段重名。默认情况下前缀为空。 如果定义了前缀，表结构和配置项 'key.fields' 都需要使用带前缀的名称。当构建消息键格式字段时，前缀会被移除， 消息键格式将会使用无前缀的名称。请注意该配置项要求必须将 'value.fields-include' 配置为 'EXCEPT_KEY'。                                                                                      |                |
| properties           | PROPERTIES         | ""  | 无                          | 否    | 该选项可以传递任意的 Kafka 参数。选项的后缀名必须匹配定义在 Kafka 参数文档中的参数名。 Flink 会自动移除 选项名中的 "properties." 前缀，并将转换后的键名以及值传入 KafkaClient。 例如，你可以通过 'properties.allow.auto.create.topics' = 'false' 来禁止自动创建 topic。 但是，某些选项，例如'key.deserializer' 和 'value.deserializer' 是不允许通过该方式传递参数，因为 Flink 会重写这些参数的值。 |                |

### ReadFromUpsertKafka示例配置

演示实时统计网页pv和uv的总量。

```json
{
  "flow": {
    "name": "ReadFromUpsertKafkaTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "5555",
        "name": "ReadFromUpsertKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.ReadFromUpsertKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "result_total_pv_uv_min",
          "key_format": "json",
          "value_format": "json",
          "value_fields_include": "ALL",
          "tableDefinition": "{\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"do_date\",\"columnType\":\"STRING\",\"nullable\":false,\"primaryKey\":true,\"partitionKey\":false,\"comment\":\"统计日期\"},{\"columnName\":\"do_min\",\"columnType\":\"STRING\",\"nullable\":false,\"primaryKey\":true,\"partitionKey\":false,\"comment\":\"统计分钟\"},{\"columnName\":\"pv\",\"columnType\":\"BIGINT\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"点击量\"},{\"columnName\":\"uv\",\"columnType\":\"BIGINT\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"一天内同个访客多次访问仅计算一个UV\"},{\"columnName\":\"currenttime\",\"columnType\":\"TIMESTAMP\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"当前时间\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null}",
          "properties": "{\"value.json.fail-on-missing-field\": false,\"properties.group.id\": \"test\"}"
        }
      },
      {
        "uuid": "6666",
        "name": "ShowChangeLogData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "100",
          "changeLog": true
        }
      }
    ],
    "paths": [
      {
        "from": "ReadFromUpsertKafka1",
        "outport": "",
        "inport": "",
        "to": "ShowChangeLogData1"
      }
    ]
  }
}
```

#### 示例说明

1. 通过`k.kafka.ReadFromUps`从kafka的`result_total_pv_uv_min `topic中读取数据（使用`WriteToUpsertKafka`组件写入到`result_total_pv_uv_min`中的数据）；

2. 通过`ShowChangeLogData`组件将数据输出到控制台。

#### tableDefinition属性结构

```json
{
    "ifNotExists": true,
    "physicalColumnDefinition": [{
        "columnName": "do_date",
        "columnType": "STRING",
        "nullable": false,
        "primaryKey": true,
        "partitionKey": false,
        "comment": "统计日期"
    }, {
        "columnName": "do_min",
        "columnType": "STRING",
        "nullable": false,
        "primaryKey": true,
        "partitionKey": false,
        "comment": "统计分钟"
    }, {
        "columnName": "pv",
        "columnType": "BIGINT",
        "nullable": false,
        "primaryKey": false,
        "partitionKey": false,
        "comment": "点击量"
    }, {
        "columnName": "uv",
        "columnType": "BIGINT",
        "nullable": false,
        "primaryKey": false,
        "partitionKey": false,
        "comment": "一天内同个访客多次访问仅计算一个UV"
    }, {
        "columnName": "currenttime",
        "columnType": "TIMESTAMP",
        "nullable": false,
        "primaryKey": false,
        "partitionKey": false,
        "comment": "当前时间"
    }],
    "metadataColumnDefinition": null,
    "computedColumnDefinition": null
}
```

演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/kafkaUpsertKafka%E6%BC%94%E7%A4%BA.gif)

### 演示案例参考

[实时数仓|以upsert的方式读写Kafka数据—Flink1.12为例_upsert-connect 时间周期-CSDN博客](https://blog.csdn.net/u013411339/article/details/113469348)
