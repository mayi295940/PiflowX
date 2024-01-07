# Filter组件

### 组件说明

数据过滤。  

### 计算引擎

flink  

### 组件分组

common  

### 端口

Inport：默认端口  

outport：默认端口  

### 组件属性

| 名称        | 展示名称      | 默认值 | 允许值 | 是否必填 | 描述    | 例子                    |
| --------- | --------- | --- | --- | ---- | ----- | --------------------- |
| condition | condition | “”  | 无   | 是    | 过滤条件。 | age >= 50 or age < 20 |



### Filter示例配置

```json
{
  "flow": {
    "name": "FilterTest",
    "uuid": "1234",
    "stops": [
      {
        "uuid": "0000",
        "name": "MockData1",
        "bundle": "cn.piflow.bundle.flink.common.DataGen",
        "properties": {
          "schema": "[{\"filedName\":\"id\",\"filedType\":\"INT\",\"kind\":\"sequence\",\"start\":1,\"end\":10000},{\"filedName\":\"name\",\"filedType\":\"STRING\",\"kind\":\"random\",\"length\":15},{\"filedName\":\"age\",\"filedType\":\"INT\",\"kind\":\"random\",\"max\":100,\"min\":1}]",
          "count": "500",
          "ratio": "1"
        }
      },
      {
        "uuid": "2222",
        "name": "Filter1",
        "bundle": "cn.piflow.bundle.flink.common.Filter",
        "properties": {
          "condition": "age >= 50 or age < 20"
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
        "from": "MockData1",
        "outport": "",
        "inport": "",
        "to": "Filter1"
      },
      {
        "from": "Filter1",
        "outport": "",
        "inport": "",
        "to": "ShowData1"
      }
    ]
  }
}
```

#### 示例说明

本示例演示了通过`DataGen`组件生成id，name，age 3个字段500条数据，每秒生成1条数据，使用`Filter`组件过滤数据，过滤条件为`age >= 50 or age < 20`，然后使用`ShowData`组件将过滤后的数据打印在控制台。  

#### 生成字段描述

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

 

![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flinkFilter%E6%BC%94%E7%A4%BADEMO.gif)
