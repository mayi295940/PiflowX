# FileWrite组件

### 组件说明

往文件系统写入。

### 计算引擎

flink

### 组件分组

file

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值 | 允许值                                                                                | 是否必填 | 描述               | 例子                                      |
| --------------- | --------------- | --- | ---------------------------------------------------------------------------------- | ---- | ---------------- | --------------------------------------- |
| path            | path            | ""  | 无                                                                                  | 是    | 文件路径。            | hdfs://server1:8020/flink/test/text.txt |
| format          | format          | ""  | Set("json", "csv", "avro", "parquet", "orc", "raw", "debezium-json", "canal-json") | 是    | 文件系统连接器支持format。 | json                                    |
| tableDefinition | TableDefinition | ""  | 无                                                                                  | 是    | Flink table定义。   |                                         |
| properties      | PROPERTIES      | ""  | 无                                                                                  | 否    | 连接器其他配置。         |                                         |

### FileWrite示例配置

```json
{
  "flow": {
    "name": "file write",
    "executorMemory": "1g",
    "executorNumber": "1",
    "uuid": "bf651f2e3f9340d29bfb737a0808bc31",
    "paths": [
      {
        "inport": "",
        "from": "DataGen",
        "to": "FileWrite",
        "outport": ""
      }
    ],
    "engineType": "flink",
    "stops": [
      {
        "name": "DataGen",
        "bundle": "cn.piflow.bundle.flink.common.DataGen",
        "uuid": "878a23d54e754efd914c71e4a6536b31",
        "properties": {
          "schema": [
            {
              "filedName": "id",
              "end": 10000,
              "kind": "sequence",
              "start": 1,
              "filedType": "INT"
            },
            {
              "filedName": "name",
              "filedType": "STRING",
              "kind": "random",
              "length": 15
            },
            {
              "min": 1,
              "filedName": "age",
              "max": 100,
              "kind": "random",
              "filedType": "INT"
            }
          ],
          "count": "100",
          "ratio": "10"
        },
        "customizedProperties": {
        }
      },
      {
        "name": "FileWrite",
        "bundle": "cn.piflow.bundle.flink.file.FileWrite",
        "uuid": "e137b3d1b82045249d717d88f7449f68",
        "properties": {
          "path": "hdfs://hadoop01:8020/test2/",
          "tableDefinition": {
            "tableBaseInfo": {
              "ifNotExists": true,
              "registerTableName": "hdfswrite"
            },
            "asSelectStatement": {
            },
            "likeStatement": {
            }
          },
          "format": "json",
          "properties": {
          }
        },
        "customizedProperties": {
        }
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`DataGen`生成测试数据，然后使用`FileWrite`组件将数据写入到`hdfs`中。

#### 演示DEMO

[PiflowX组件-FIleWrite_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1ZC4y167J7/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
