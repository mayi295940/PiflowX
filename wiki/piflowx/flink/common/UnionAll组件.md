# UnionAll组件

### 组件说明

Union多个输入源。输入源必须具有相同的字段类型。  

### 计算引擎

flink  

### 组件分组

common  

### 端口

Inport：Any  

outport：默认端口  

### 组件属性

| 名称      | 展示名称    | 默认值 | 允许值 | 是否必填 | 描述                                      | 例子  |
| ------- | ------- | --- | --- | ---- | --------------------------------------- | --- |
| inports | Inports | “”  | 无   | 否    | Inports string are separated by commas。 | 1,2 |

### UnionAll示例配置

```json
{
  "flow": {
    "uuid": "7a5d97d288224a358a2d30ae8a229230",
    "runMode": "DEBUG",
    "name": "UnionAllTest",
    "engineType": "flink",
    "environment": {
      "runtimeMode": "batch"
    },
    "paths": [
      {
        "inport": "1",
        "from": "CsvStringParser1",
        "to": "UnionAll",
        "outport": ""
      },
      {
        "inport": "2",
        "from": "CsvStringParser2",
        "to": "UnionAll",
        "outport": ""
      },
      {
        "inport": "",
        "from": "UnionAll",
        "to": "ShowData",
        "outport": ""
      }
    ],
    "stops": [
      {
        "uuid": "0000",
        "name": "CsvStringParser1",
        "bundle": "cn.piflow.bundle.flink.file.CsvStringParser",
        "properties": {
          "content": "1,zs\n2,ls",
          "delimiter": ",",
          "schema": "id:int,name:string"
        }
      },
      {
        "uuid": "0000",
        "name": "CsvStringParser2",
        "bundle": "cn.piflow.bundle.flink.file.CsvStringParser",
        "properties": {
          "content": "1,zs\n3,ww",
          "delimiter": ",",
          "schema": "id:int,name:string"
        }
      },
      {
        "customizedProperties": {},
        "name": "UnionAll",
        "uuid": "0f0cf231dbf64852b17ac3ee2064134d",
        "bundle": "cn.piflow.bundle.flink.common.UnionAll",
        "properties": {
          "inports": "1,2"
        }
      },
      {
        "customizedProperties": {},
        "name": "ShowData",
        "uuid": "c2a84707d76b4083a74e46e6ea8cd5e3",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "properties": {
          "showNumber": "1000"
        }
      }
    ]
  }
}
```

#### 示例说明

1.通过两个`CsvStringParser`组件解析字符串类型的csv数据;

2.使用UnionAll组件将2个数据源的数据连接起来；

3.使用`ShowData`组件将union后的数据打印在控制台。  

#### 演示DEMO

![](![](https://cdn.jsdelivr.net/gh/mayi295940/blog_pic_ma@main/img/piflowx/stop/flink/UnionAll%E7%BB%84%E4%BB%B6%E6%BC%94%E7%A4%BA.gif))
