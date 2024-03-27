# Faker组件

### 组件说明

根据每列提供的Data Faker表达式生成模拟数据。

### 计算引擎

flink

### 有界性

count属性设置了便是有界的，否则为无界流。

### 组件分组

common

### 端口

Inport：None

outport：默认端口

### 组件属性

| 名称     | 展示名称   | 默认值   | 允许值 | 是否必填 | 描述                                                                                                                                | 例子                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ------ | ------ | ----- | --- | ---- | --------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| schema | Schema | “”    | 无   | 是    | schema                                                                                                                            | [{"filedName":"name","filedType":"STRING","expression":"<superhero.name>","comment":"姓名"},{"filedName":"power","filedType":"STRING","expression":"<superhero.power>","nullRate":0.5},{"filedName":"age","filedType":"INT","expression":"<number.numberBetween ''0'',''1000''>"},{"filedName":"timeField","computedColumnExpression":"PROCTIME()"},{"filedName":"timestamp1","filedType":"TIMESTAMP(3)","expression":"<date.past ''15'',''SECONDS''>"},{"filedName":"timestamp2","filedType":"TIMESTAMP(3)","expression":"<date.past ''15'',''5'',''SECONDS''>"},{"filedName":"timestamp3","filedType":"TIMESTAMP(3)","expression":"<date.future ''15'',''5'',''SECONDS''>"},{"filedName":"time","filedType":"TIME","expression":"<time.future ''15'',''5'',''SECONDS''>"},{"filedName":"date1","filedType":"DATE","expression":"<date.birthday>"},{"filedName":"date2","filedType":"DATE","expression":"<date.birthday ''1'',''100''>"},{"filedName":"order_status","filedType":"STRING","expression":"<Options.option ''RECEIVED'',''SHIPPED'',''CANCELLED'')>"}] |
| count  | Count  | “”    | 无   | 否    | The number of rows to produce. If this is options is set, the source is bounded otherwise it is unbounded and runs indefinitely.。 | 10                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ratio  | Ratio  | 10000 | 无   | 否    | The maximum rate at which the source produces records.。                                                                           | 10                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |

### 字段规则描述

[flink-faker/README.md 在 master ·knaufk/flink-faker --- flink-faker/README.md at master · knaufk/flink-faker (github.com)](https://github.com/knaufk/flink-faker/blob/master/README.md)

[All providers - Datafaker](https://www.datafaker.net/documentation/providers/#provider-groups)

| **参数**                    | **默认值** | **描述**                                                           |
| ------------------------- | ------- | ---------------------------------------------------------------- |
| fields.<field>.expression | None    | The Data Faker expression to generate the values for this field. |
| fields.<field>.null-rate  | 0.0     | Fraction of rows for which this field is null                    |
| fields.<field>.length     | 1       | Size of array, map or multiset                                   |

注：字段expression使用Data Faker expression,但是需要将格式进行转换，如

`#{superhero.name}`需要写成`<superhero.name>`,即将`#{}`替换为`<>`。这是为了避免sql注入和mytaties语法冲突。

###示例配置

```json
{
  "flow": {
    "name": "FakerTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "Faker",
        "bundle": "cn.piflow.bundle.flink.common.Faker",
        "properties": {
          "count": "100",
          "ratio": "5",
          "schema": [
            {
              "filedName": "name",
              "filedType": "STRING",
              "expression": "<superhero.name>",
              "comment": "姓名"
            },
            {
              "filedName": "power",
              "filedType": "STRING",
              "expression": "<superhero.power>",
              "nullRate": 0.5
            },
            {
              "filedName": "age",
              "filedType": "INT",
              "expression": "<number.numberBetween ''0'',''1000''>"
            },
            {
              "filedName": "timeField",
              "computedColumnExpression": "PROCTIME()"
            },
            {
              "filedName": "timestamp1",
              "filedType": "TIMESTAMP(3)",
              "expression": "<date.past ''15'',''SECONDS''>"
            },
            {
              "filedName": "timestamp2",
              "filedType": "TIMESTAMP(3)",
              "expression": "<date.past ''15'',''5'',''SECONDS''>"
            },
            {
              "filedName": "timestamp3",
              "filedType": "TIMESTAMP(3)",
              "expression": "<date.future ''15'',''5'',''SECONDS''>"
            },
            {
              "filedName": "time",
              "filedType": "TIME",
              "expression": "<time.future ''15'',''5'',''SECONDS''>"
            },
            {
              "filedName": "date1",
              "filedType": "DATE",
              "expression": "<date.birthday>"
            },
            {
              "filedName": "date2",
              "filedType": "DATE",
              "expression": "<date.birthday ''1'',''100''>"
            },
            {
              "filedName": "order_status",
              "filedType": "STRING",
              "expression": "<Options.option ''RECEIVED'',''SHIPPED'',''CANCELLED'')>"
            }
          ]
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
        "from": "Faker",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例演示

#### [PiflowX-Faker组件_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1Mt421V7LL/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
