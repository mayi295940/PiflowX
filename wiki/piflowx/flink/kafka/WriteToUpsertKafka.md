# WriteToUpsertKafka组件

### 组件说明

以upsert方式往Kafka topic中写数据。

### 计算引擎

flink

### 有界性

Streaming Upsert Mode

### 组件分组

kafka

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称                         | 展示名称                    | 默认值 | 允许值                        | 是否必填 | 描述                                                                                                                                                                                                                                                                             | 例子             |
| -------------------------- | ----------------------- | --- | -------------------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------- |
| kafka_host                 | KAFKA_HOST              | ""  | 无                          | 是    | 逗号分隔的Kafka broker列表。                                                                                                                                                                                                                                                           | 127.0.0.1:9092 |
| topic                      | TOPIC                   | ""  | 无                          | 是    | 用于写入Kafka topic名称。                                                                                                                                                                                                                                                             | topic-1        |
| tableDefinition            | TableDefinition         | ""  | 无                          | 是    | Flink table定义。                                                                                                                                                                                                                                                                 |                |
| key_format                 | keyFormat               | ""  | Set("json", "csv", "avro") | 是    | 用于对Kafka消息中key部分序列化的格式。key字段由PRIMARY KEY语法指定。                                                                                                                                                                                                                                  | json           |
| value_format               | ValueFormat             | ""  | Set("json", "csv", "avro") | 是    | 用于对Kafka消息中value部分序列化的格式                                                                                                                                                                                                                                                       | json           |
| value_fields_include       | ValueFieldsInclude      | ALL | Set("ALL", "EXCEPT_KEY")   | 是    | 控制哪些字段应该出现在 value 中。可取值："ALL：消息的 value 部分将包含 schema 中所有的字段包括定义为主键的字段。"EXCEPT_KEY：记录的 value 部分包含 schema 的所有字段，定义为主键的字段除外。                                                                                                                                                       | ALL            |
| key_fields_prefix          | KeyFieldsPrefix         | ""  | 无                          | 否    | 为所有消息键（Key）格式字段指定自定义前缀，以避免与消息体（Value）格式字段重名。默认情况下前缀为空。 如果定义了前缀，表结构和配置项 'key.fields' 都需要使用带前缀的名称。当构建消息键格式字段时，前缀会被移除， 消息键格式将会使用无前缀的名称。请注意该配置项要求必须将 'value.fields-include' 配置为 'EXCEPT_KEY'。                                                                                      |                |
| sink_parallelism           | SinkParallelism         | ""  | 无                          | 否    | 定义upsert-kafka sink算子的并行度。默认情况下，由框架确定并行度，与上游链接算子的并行度保持一致。                                                                                                                                                                                                                      |                |
| sink_buffer_flush_max_rows | SinkBufferFlushMaxRows  | ""  | 无                          | 否    | 缓存刷新前，最多能缓存多少条记录。当sink收到很多同key上的更新时，缓存将保留同key的最后一条记录，因此sink缓存能帮助减少发往Kafka topic的数据量，以及避免发送潜在的tombstone消息。 可以通过设置为 '0' 来禁用它默认，该选项是未开启的。注意，如果要开启sink缓存，需要同时设置 'sink.buffer-flush.max-rows' 和 'sink.buffer-flush.interval两个选项为大于零的值。                                              |                |
| sink_buffer_flush_interval | SinkBufferFlushInterval | ""  | 无                          | 否    | 缓存刷新的间隔时间，超过该时间后异步线程将刷新缓存数据。当 sink 收到很多同 key 上的更新时，缓存将保留同 key 的最后一条记录，因此 sink 缓存能帮助减少发往 Kafka topic 的数据量，以及避免发送潜在的 tombstone 消息。 可以通过设置为 '0' 来禁用它。默认，该选项是未开启的。注意，如果要开启 sink 缓存，需要同时设置 'sink.buffer-flush.max-rows' 和 'sink.buffer-flush.interval' 两个选项为大于零的值。                  |                |
| properties                 | PROPERTIES              | ""  | 无                          | 否    | 该选项可以传递任意的 Kafka 参数。选项的后缀名必须匹配定义在 Kafka 参数文档中的参数名。 Flink 会自动移除 选项名中的 "properties." 前缀，并将转换后的键名以及值传入 KafkaClient。 例如，你可以通过 'properties.allow.auto.create.topics' = 'false' 来禁止自动创建 topic。 但是，某些选项，例如'key.deserializer' 和 'value.deserializer' 是不允许通过该方式传递参数，因为 Flink 会重写这些参数的值。 |                |

### WriteToUpsertKafka示例配置

演示实时统计网页pv和uv的总量。

```json
{
  "flow": {
    "name": "UpsertKafkaTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "JsonStringParser1",
        "bundle": "cn.piflow.bundle.flink.file.json.JsonStringParser",
        "properties": {
          "content": "[{\"user_id\":\"1\",\"client_ip\":\"192.168.12.1\",\"client_info\":\"phone\",\"page_code\":\"1001\",\"access_time\":\"2021-01-08 11:32:24\",\"dt\":\"2021-01-08\"},{\"user_id\":\"1\",\"client_ip\":\"192.168.12.1\",\"client_info\":\"phone\",\"page_code\":\"1201\",\"access_time\":\"2021-01-08 11:32:55\",\"dt\":\"2021-01-08\"},{\"user_id\":\"2\",\"client_ip\":\"192.165.12.1\",\"client_info\":\"pc\",\"page_code\":\"1031\",\"access_time\":\"2021-01-08 11:32:59\",\"dt\":\"2021-01-08\"},{\"user_id\":\"1\",\"client_ip\":\"192.168.12.1\",\"client_info\":\"phone\",\"page_code\":\"1101\",\"access_time\":\"2021-01-08 11:33:24\",\"dt\":\"2021-01-08\"},{\"user_id\":\"3\",\"client_ip\":\"192.168.10.3\",\"client_info\":\"pc\",\"page_code\":\"1001\",\"access_time\":\"2021-01-08 11:33:30\",\"dt\":\"2021-01-08\"},{\"user_id\":\"1\",\"client_ip\":\"192.168.12.1\",\"client_info\":\"phone\",\"page_code\":\"1001\",\"access_time\":\"2021-01-08 11:34:24\",\"dt\":\"2021-01-08\"}]",
          "schema": "user_id:STRING,client_ip:STRING,client_info:STRING,page_code:STRING,access_time:TIMESTAMP,dt:STRING"
        }
      },
      {
        "uuid": "1111",
        "name": "WriteToKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.WriteToKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "user_ip_pv",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":null,\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"user_id\",\"columnType\":\"STRING\",\"comment\":\"用户ID\"},{\"columnName\":\"client_ip\",\"columnType\":\"STRING\",\"comment\":\"客户端IP\"},{\"columnName\":\"client_info\",\"columnType\":\"STRING\",\"comment\":\"设备机型信息\"},{\"columnName\":\"page_code\",\"columnType\":\"STRING\",\"comment\":\"页面代码\"},{\"columnName\":\"access_time\",\"columnType\":\"TIMESTAMP\",\"comment\":\"请求时间\"},{\"columnName\":\"dt\",\"columnType\":\"STRING\",\"comment\":\"时间分区天\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "format": "json",
          "properties": "{\"json.ignore-parse-errors\":\"true\"}"
        }
      },
      {
        "uuid": "2222",
        "name": "ReadFromKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.ReadFromKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "user_ip_pv",
          "group": "test",
          "startup_mode": "earliest-offset",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"source_ods_fact_user_ip_pv\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"user_id\",\"columnType\":\"STRING\",\"comment\":\"用户ID\"},{\"columnName\":\"client_ip\",\"columnType\":\"STRING\",\"comment\":\"客户端IP\"},{\"columnName\":\"client_info\",\"columnType\":\"STRING\",\"comment\":\"设备机型信息\"},{\"columnName\":\"page_code\",\"columnType\":\"STRING\",\"comment\":\"页面代码\"},{\"columnName\":\"access_time\",\"columnType\":\"TIMESTAMP\",\"comment\":\"请求时间\"},{\"columnName\":\"dt\",\"columnType\":\"STRING\",\"comment\":\"时间分区天\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "format": "json",
          "properties": "{}"
        }
      },
      {
        "uuid": "3333",
        "name": "SQLExecute1",
        "bundle": "cn.piflow.bundle.flink.common.SQLExecute",
        "properties": {
          "sql": "CREATE VIEW view_total_pv_uv_min AS SELECT dt AS do_date, count(client_ip) AS pv, count(DISTINCT client_ip) AS uv,max(access_time) AS access_time FROM source_ods_fact_user_ip_pv GROUP BY dt;"
        }
      },
      {
        "uuid": "4444",
        "name": "WriteToUpsertKafka1",
        "bundle": "cn.piflow.bundle.flink.kafka.WriteToUpsertKafka",
        "properties": {
          "kafka_host": "hadoop01:9092",
          "topic": "result_total_pv_uv_min",
          "key_format": "json",
          "value_format": "json",
          "value_fields_include": "ALL",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"result_total_pv_uv_min\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"do_date\",\"columnType\":\"STRING\",\"nullable\":false,\"primaryKey\":true,\"partitionKey\":false,\"comment\":\"统计日期\"},{\"columnName\":\"do_min\",\"columnType\":\"STRING\",\"nullable\":false,\"primaryKey\":true,\"partitionKey\":false,\"comment\":\"统计分钟\"},{\"columnName\":\"pv\",\"columnType\":\"BIGINT\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"点击量\"},{\"columnName\":\"uv\",\"columnType\":\"BIGINT\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"一天内同个访客多次访问仅计算一个UV\"},{\"columnName\":\"currenttime\",\"columnType\":\"TIMESTAMP\",\"nullable\":false,\"primaryKey\":false,\"partitionKey\":false,\"comment\":\"当前时间\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null,\"asSelectStatement\":\"SELECT  do_date,cast(DATE_FORMAT(access_time,'HH:mm') AS STRING) AS do_min,pv,uv,NOW() AS currenttime from view_total_pv_uv_min\"}",
          "properties": "{\"value.json.fail-on-missing-field\": false}"
        }
      }
    ],
    "paths": [
      {
        "from": "JsonStringParser1",
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
        "to": "SQLExecute1"
      },
      {
        "from": "SQLExecute1",
        "outport": "",
        "inport": "",
        "to": "WriteToUpsertKafka1"
      }
    ]
  }
}
```

#### 示例说明

1. 通过`JsonStringParser`将给定的json字符串解析，并输出到下游，通过`WriteToKafka`组件将数据写入到kafka的`user_ip_pv `topic中；

2. 通过ReadFromKafka组件从`user_ip_pv` topic中读取数据；

3. 使用`SQLExecute`组件执行创建视图`view_total_pv_uv_min`的语句；

4. 使用`WriteToUpsertKafka`定义upsert kafka table,并使用`tableDefinition`属性中定义的`asSelectStatement`执行语句，将结果写入kafka。

#### tableDefinition属性结构

```json
{
  "catalogName": null,
  "dbname": null,
  "tableName": "result_total_pv_uv_min",
  "ifNotExists": true,
  "physicalColumnDefinition": [
    {
      "columnName": "do_date",
      "columnType": "STRING",
      "nullable": false,
      "primaryKey": true,
      "partitionKey": false,
      "comment": "统计日期"
    },
    {
      "columnName": "do_min",
      "columnType": "STRING",
      "nullable": false,
      "primaryKey": true,
      "partitionKey": false,
      "comment": "统计分钟"
    },
    {
      "columnName": "pv",
      "columnType": "BIGINT",
      "nullable": false,
      "primaryKey": false,
      "partitionKey": false,
      "comment": "点击量"
    },
    {
      "columnName": "uv",
      "columnType": "BIGINT",
      "nullable": false,
      "primaryKey": false,
      "partitionKey": false,
      "comment": "一天内同个访客多次访问仅计算一个UV"
    },
    {
      "columnName": "currenttime",
      "columnType": "TIMESTAMP",
      "nullable": false,
      "primaryKey": false,
      "partitionKey": false,
      "comment": "当前时间"
    }
  ],
  "metadataColumnDefinition": null,
  "computedColumnDefinition": null,
  "watermarkDefinition": null,
  "asSelectStatement": "SELECT  do_date,cast(DATE_FORMAT(access_time,'HH:mm') AS STRING) AS do_min,pv,uv,NOW() AS currenttime from view_total_pv_uv_min"
}
```

演示DEMO

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/kafkaUpsertKafka%E6%BC%94%E7%A4%BA.gif)

### 演示案例参考

[实时数仓|以upsert的方式读写Kafka数据—Flink1.12为例_upsert-connect 时间周期-CSDN博客](https://blog.csdn.net/u013411339/article/details/113469348)
