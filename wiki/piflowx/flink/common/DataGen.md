# DataGen组件

### 组件说明

按数据生成规则模拟数据。在开发和演示场景下使用比较方便。具体可以查看Flink官方DataGen connector。

### 计算引擎

flink

### 有界性

字段的数据全部生成完成后，source 就结束了。 因此，有界性取决于字段的有界性。

### 组件分组

common

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称     | 展示名称   | 默认值 | 允许值 | 是否必填 | 描述                  | 例子                                                                                                                                                                                                                           |
| ------ | ------ | --- | --- | ---- | ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| schema | Schema | “”  | 无   | 是    | 数据生成规则，字段规则见字段规则描述。 | [{"filedName":"id","filedType":"INT","kind":"sequence","start":1,"end":10000},{"filedName":"name","filedType":"STRING","kind":"random","length":15},{"filedName":"age","filedType":"INT","kind":"random","max":100,"min":1}] |
| count  | Count  | 10  | 无   | 是    | 测试数据的数量。            | 10                                                                                                                                                                                                                           |
| ratio  | Ratio  | 1   | 无   | 否    | 数据每秒生成速率。           | 10                                                                                                                                                                                                                           |

### 字段规则描述

| **参数**    | **是否必须** | **描述**                                                                                                                                                                                      |
| --------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| filedName | 是        | 指定字段名称。                                                                                                                                                                                     |
| filedType | 是        | 指定字段类型。                                                                                                                                                                                     |
| kind      | 否        | 指定字段的生成器。可以是 'sequence' 或 'random'。随机生成器（random）是默认的生成器，您可以指定随机生成的最大和最小值。char、varchar、binary、varbinary, string （类型）可以指定长度。它是无界的生成器。序列生成器（sequence），您可以指定序列的起始和结束值。它是有界的生成器，当序列数字达到结束值，读取结束。 |
| min       | 否        | 随机生成器的最小值，适用于数字类型。                                                                                                                                                                          |
| max       | 否        | 随机生成器的最大值，适用于数字类型。                                                                                                                                                                          |
| maxPast   | 否        | 随机生成器生成相对当前时间向过去偏移的最大值，适用于 timestamp 类型。                                                                                                                                                    |
| length    | 否        | 随机生成器生成字符的长度，适用于 char、varchar、binary、varbinary、string。                                                                                                                                      |
| start     | 否        | 序列生成器的起始值。                                                                                                                                                                                  |
| end       | 否        | 序列生成器的结束值。                                                                                                                                                                                  |

### DataGen示例配置

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
          "schema": "[{\"filedName\":\"id\",\"filedType\":\"INT\",\"kind\":\"sequence\",\"start\":1,\"end\":10000},{\"filedName\":\"name\",\"filedType\":\"STRING\",\"kind\":\"random\",\"length\":15},{\"filedName\":\"age\",\"filedType\":\"INT\",\"kind\":\"random\",\"max\":100,\"min\":1},{\"filedName\":\"timeField\",\"filedType\":\"AS PROCTIME()\"}]",
          "count": "500",
          "ratio": "5"
        }
      },
      {
        "uuid": "2222",
        "name": "ShowData1",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "500"
        }
      }
    ],
    "paths": [
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

本示例演示了通过`DataGen`组件生成id，name，age，timeField4个字段500条数据，每秒生成5条数据，并使用`ShowData`组件将数据打印在控制台。

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
    },
            {       
        "filedName": "timeField",
        "filedType": "AS PROCTIME()"
    }
]
```

1.id字段

id字段类型为INT,使用sequence生成器，序列生成器的起始值为1，结束值为10000.

2.name字段

name字段类型为STRING,使用random生成器，生成字符长度为15。

3.age字段

age字段类型为INT，使用random生成器，随机生成器的最小值为1，最大值为100。

4.timeField字段

timeField字段为PROCTIME。

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/commonDataGen%E7%BB%84%E4%BB%B6%E6%B5%8B%E8%AF%95%E8%A7%86%E9%A2%91.gif)
