# OrderBy组件

### 组件说明

ORDER BY组件使结果行根据指定的表达式进行排序。  

### 计算引擎

flink  

### 组件分组

common  

### 端口

Inport：默认端口  

outport：默认端口  

### 组件属性

| 名称         | 展示名称       | 默认值 | 允许值 | 是否必填 | 描述                                                           | 例子                  |
| ---------- | ---------- | --- | --- | ---- | ------------------------------------------------------------ | ------------------- |
| expression | Expression | “”  | 无   | 否    | 在流模式下运行时，表的主要排序顺序必须按时间属性升序。所有后续的orders都可以自由选择。但是批处理模式没有这个限制。 | name->desc,age->asc |
| offset     | offset     | “”  | 无   | 否    | Offset操作根据偏移位置来限定（可能是已排序的）结果集。                               | 10                  |
| fetch      | fetch      | “”  | 无   | 否    | Fetch操作将（可能已排序的）结果集限制为前n行。                                   | 10                  |

### OrderBy示例配置

```json
{
  "flow": {
    "name": "OrderByTest",
    "uuid": "1234",
    "runtimeMode": "batch",
    "stops": [
      {
        "uuid": "0000",
        "name": "JDBCRead1",
        "bundle": "cn.piflow.bundle.flink.jdbc.JDBCRead",
        "properties": {
          "url": "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true",
          "username": "root",
          "password": "123456",
          "tableName": "test",
          "tableDefinition": "{\"catalogName\":null,\"dbname\":null,\"tableName\":\"\",\"ifNotExists\":true,\"physicalColumnDefinition\":[{\"columnName\":\"id\",\"columnType\":\"INT\",\"comment\":\"id\"},{\"columnName\":\"name\",\"columnType\":\"STRING\",\"comment\":\"name\"},{\"columnName\":\"age\",\"columnType\":\"INT\"}],\"metadataColumnDefinition\":null,\"computedColumnDefinition\":null,\"watermarkDefinition\":null}",
          "properties": "{}"
        }
      },
      {
        "uuid": "2222",
        "name": "OrderBy",
        "bundle": "cn.piflow.bundle.flink.common.OrderBy",
        "properties": {
          "expression": "age->asc",
          "offset": "30"
        }
      },
      {
        "uuid": "3333",
        "name": "ShowChangeLogData",
        "bundle": "cn.piflow.bundle.flink.common.ShowChangeLogData",
        "properties": {
          "showNumber": "100"
        }
      }
    ],
    "paths": [
      {
        "from": "JDBCRead1",
        "outport": "",
        "inport": "",
        "to": "OrderBy"
      },
      {
        "from": "OrderBy",
        "outport": "",
        "inport": "",
        "to": "ShowChangeLogData"
      }
    ]
  }
}
```

#### 示例说明

1.通过`JdbcRead`组件从mysql库中读取user表数据;

2.使用OrderBy组件对输入数据进行排序操作，排序按照age升序，结果跳过前30条记录；

3.使用`ShowChangelog`组件将排序后的数据打印在控制台。  

#### 演示DEMO

[PiflowX-OrderBy组件_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1JC411r7AR/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
