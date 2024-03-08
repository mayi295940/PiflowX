# TopN组件

### 组件说明

按列排序的N个最小值或最大值。  

### 有界性

batch streaming

### 计算引擎

flink  

### 组件分组

common  

### 端口

Inport：默认端口  

outport：默认端口  

### 组件属性

| 名称             | 展示名称           | 默认值   | 允许值                  | 是否必填 | 描述       | 例子                         |
| -------------- | -------------- | ----- | -------------------- | ---- | -------- | -------------------------- |
| column_list    | column_list    | “*”   | 无                    | 否    | 查询字段     | name,age                   |
| partition_list | partition_list | 无     | 无                    | 否    | 分区字段     | name,age                   |
| order_list     | order_list     | 无     | 无                    | 是    | 排序字段     | name->asc,age-desc         |
| tableName      | tableName      | 无     | 无                    | 是    | 表名       | test                       |
| topNum         | topNum         | 无     | 无                    | 是    | TopN的条⽬数 | 10                         |
| conditions     | conditions     | 无     | 无                    | 否    | 查询条件     | age > 10 and name = 'test' |
| isWindow       | isWindow       | false | Set("ture", "false") | 否    | 是否窗口TopN | false                      |

### TopN示例配置

```json
{
    "flow":{
        "name":"topDemo",
        "uuid":"59e583d074b44985aee2ad70a2547a77",
        "runMode":"DEBUG",
        "paths":[
            {
                "inport":"",
                "from":"JDBCRead",
                "to":"TopN",
                "outport":""
            },
            {
                "inport":"",
                "from":"TopN",
                "to":"ShowChangeLogData",
                "outport":""
            }
        ],
        "environment":{
            "runtimeMode":"batch"
        },
        "engineType":"flink",
        "stops":[
            {
                "name":"JDBCRead",
                "bundle":"cn.piflow.bundle.flink.jdbc.JDBCRead",
                "uuid":"31998ca1fbea45b5a6799a6150300cf6",
                "properties":{
                    "url":"jdbc:mysql://192.168.186.100:3306/test",
                    "username":"root",
                    "fetchSize":"100",
                    "driver":"",
                    "properties":{

                    },
                    "tableName":"source_table",
                    "tableDefinition":{
                        "tableBaseInfo":{
                            "registerTableName":"source_table"
                        },
                        "physicalColumnDefinition":[
                            {
                                "columnName":"name",
                                "columnType":"STRING"
                            },
                            {
                                "columnName":"search_cnt",
                                "columnType":"BIGINT"
                            },
                            {
                                "columnName":"key",
                                "columnType":"STRING"
                            },
                            {
                                "columnName":"row_time",
                                "columnType":"TIMESTAMP"
                            }
                        ],
                        "asSelectStatement":{

                        },
                        "likeStatement":{

                        }
                    },
                    "password":"123456"
                },
                "customizedProperties":{

                }
            },
            {
                "name":"TopN",
                "bundle":"cn.piflow.bundle.flink.common.TopN",
                "uuid":"08726631225d4dcca00dc532a83c7344",
                "properties":{
                    "partition_list":"key",
                    "conditions":"",
                    "order_list":"search_cnt->desc",
                    "column_list":"key, name, search_cnt, row_time",
                    "tableName":"source_table",
                    "topNum":"10",
                    "isWindow":"false"
                },
                "customizedProperties":{

                }
            },
            {
                "name":"ShowChangeLogData",
                "bundle":"cn.piflow.bundle.flink.common.ShowChangeLogData",
                "uuid":"6225e5ac18704ed0aa1c95e3fbe1ce28",
                "properties":{
                    "showNumber":"100"
                },
                "customizedProperties":{

                }
            }
        ]
    }
}
```

#### 参考示例

[Flink SQL TopN语句详解 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/665480015)(https://zhuanlan.zhihu.com/p/665480015)

实际案例：取某个搜索关键词下的搜索热度前 10 名的词条数据。

输⼊数据为搜索词条数据的搜索热度数据，当搜索热度发⽣变化时，会将变化后的数据写⼊到数据源的 Kafka 中：

```sql
数据源 schema：
​
-- 字段名 备注
-- key 搜索关键词
-- name 搜索热度名称
-- search_cnt 热搜消费热度（⽐如 3000）
-- timestamp 消费词条时间戳
CREATE TABLE source_table (
 name STRING NOT NULL,
 search_cnt BIGINT NOT NULL,
 key STRING NOT NULL,
 row_time timestamp(3),
 WATERMARK FOR row_time AS row_time
) WITH (
 'connector' = 'filesystem', 
 'path' = 'file:///Users/hhx/Desktop/source_table.csv',
 'format' = 'csv'
);
​
A,100,a,2021-11-01 00:01:03
A,200,a,2021-11-02 00:01:03
A,300,a,2021-11-03 00:01:03
B,200,b,2021-11-01 00:01:03
B,300,b,2021-11-02 00:01:03
B,400,b,2021-11-03 00:01:03
C,300,c,2021-11-01 00:01:03
C,400,c,2021-11-02 00:01:03
C,500,c,2021-11-03 00:01:03
D,400,d,2021-11-01 00:01:03
D,500,d,2021-11-02 00:01:03
D,600,d,2021-11-03 00:01:03
​
-- 数据汇 schema：
-- key 搜索关键词
-- name 搜索热度名称
-- search_cnt 热搜消费热度（⽐如 3000）
-- timestamp 消费词条时间戳
CREATE TABLE sink_table (
 key BIGINT,
 name BIGINT,
 search_cnt BIGINT,
 `timestamp` TIMESTAMP(3)
) WITH (
 ...
);
​
-- DML 逻辑
INSERT INTO sink_table
SELECT key, name, search_cnt, row_time as `timestamp`
FROM (
 SELECT key, name, search_cnt, row_time, 
 -- 根据热搜关键词 key 作为 partition key，然后按照 search_cnt 倒排取前 2 名
 ROW_NUMBER() OVER (PARTITION BY key ORDER BY search_cnt desc) AS rownum
 FROM source_table)
WHERE rownum <= 2
```

### 演示视频

[PiflowX-TopN组件_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1zu4m1u7b8/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
