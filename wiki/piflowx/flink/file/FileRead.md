# FileRead组件

### 组件说明

从文件系统读取。

### 计算引擎

flink

### 组件分组

file

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值 | 允许值                                                                                | 是否必填 | 描述                                                                                                                                                                                                            | 例子                                      |
| --------------- | --------------- | --- | ---------------------------------------------------------------------------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| path            | path            | ""  | 无                                                                                  | 是    | 文件路径。                                                                                                                                                                                                         | hdfs://server1:8020/flink/test/text.txt |
| format          | format          | ""  | Set("json", "csv", "avro", "parquet", "orc", "raw", "debezium-json", "canal-json") | 是    | 文件系统连接器支持format。                                                                                                                                                                                              | json                                    |
| monitorInterval | monitorInterval | ""  | 无                                                                                  | 否    | 设置新文件的监控时间间隔，并且必须设置 > 0 的值。 每个文件都由其路径唯一标识，一旦发现新文件，就会处理一次。 已处理的文件在source的整个生命周期内存储在state中，因此，source的state在checkpoint和savepoint时进行保存。更短的时间间隔意味着文件被更快地发现，但也意味着更频繁地遍历文件系统/对象存储。 如果未设置此配置选项，则提供的路径仅被扫描一次，因此源将是有界的。 |                                         |
| tableDefinition | TableDefinition | ""  | 无                                                                                  | 是    | Flink table定义。                                                                                                                                                                                                |                                         |
| properties      | PROPERTIES      | ""  | 无                                                                                  | 否    | 连接器其他配置。                                                                                                                                                                                                      |                                         |

### FileRead示例配置

```json
{
  "flow": {
    "name": "FileReadTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "1111",
        "name": "FileRead",
        "bundle": "cn.piflow.bundle.flink.file.FileRead",
        "properties": {
          "path": "src/test/resources/file/user.csv",
          "format": "csv",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{'csv.field-delimiter':',','csv.ignore-parse-errors':'true'}"
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
        "from": "FileRead",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`FileRead`从本地文件`user.csv`中读取内容，然后使用`ShoData`组件将数据打印在控制台。

#### 演示DEMO

[PiflowX组件-FileRead_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1Hg4y1e7ke/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
