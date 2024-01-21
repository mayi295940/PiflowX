# PostgresCdc组件

### 组件说明

Postgres CDC连接器允许从PostgreSQL数据库读取快照数据和增量数据。

### 计算引擎

flink

### 组件分组

cdc

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称                                                                                                                                                                                                 | 展示名称            | 默认值  | 允许值 | 是否必填 | 描述                                                                                                                                                                                                                                        | 例子        |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------- | ---- | --- | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- |
| hostname                                                                                                                                                                                           | Hostname        | ""   | 无   | 是    | PostgreSQL数据库服务器的IP地址或主机名。                                                                                                                                                                                                                | 127.0.0.1 |
| username                                                                                                                                                                                           | Username        | ""   | 无   | 是    | 连接到PostgreSQL数据库服务器时要使用的用户名。                                                                                                                                                                                                              | root      |
| password                                                                                                                                                                                           | Password        | ""   | 无   | 是    | 连接PostgreSQL数据库服务器时使用的密码。                                                                                                                                                                                                                 | 123456    |
| databaseName                                                                                                                                                                                       | DatabaseName    | ""   | 无   | 是    | 要监视的PostgreSQL服务器的数据库名称。                                                                                                                                                                                                                  | test      |
| schemaName                                                                                                                                                                                         | Schema          | ""   | 无   | 是    | 要监视的PostgreSQL数据库的Schema。                                                                                                                                                                                                                 | public    |
| tableName                                                                                                                                                                                          | TableName       | ""   | 无   | 是    | "需要监视的PostgreSQL数据库的表名。                                                                                                                                                                                                                   | test      |
| port                                                                                                                                                                                               | Port            | 5432 | 无   | 否    | PostgreSQL数据库服务器的整数端口号。                                                                                                                                                                                                                   | 5432      |
| slotName                                                                                                                                                                                           | SlotName        | ""   | 无   | 是    | The name of the PostgreSQL logical decoding slot that was created for streaming changes from a particular plug-in for a particular database/schema. The server uses this slot to stream events to the connector that you are configuring. |           |
| Slot names must conform to PostgreSQL replication slot naming rules, which state: "Each replication slot has a name, which can contain lower-case letters, numbers, and the underscore character." |                 |      |     |      |                                                                                                                                                                                                                                           |           |
| tableDefinition                                                                                                                                                                                    | TableDefinition | ""   | 无   | 是    | Flink table定义。                                                                                                                                                                                                                            |           |
| properties                                                                                                                                                                                         | PROPERTIES      | ""   | 无   | 否    | 连接器其他配置。                                                                                                                                                                                                                                  |           |

### PostgresCdc示例配置

```json
{
	"flow":{
		"name":"pgcdc",
		"uuid":"273553c2ece043c29fba179df6826c5a",
		"paths":[
			{
				"inport":"",
				"from":"products",
				"to":"SQLQuery",
				"outport":""
			},
			{
				"inport":"",
				"from":"orders",
				"to":"SQLQuery",
				"outport":""
			},
			{
				"inport":"",
				"from":"shipments",
				"to":"SQLQuery",
				"outport":""
			},
			{
				"inport":"",
				"from":"SQLQuery",
				"to":"ShowChangeLogData",
				"outport":""
			}
		],
		"engineType":"flink",
		"stops":[
			{
				"name":"shipments",
				"bundle":"cn.piflow.bundle.flink.cdc.postgres.PostgresCdc",
				"uuid":"13ac002a8a4d404e8dda77c9a1d8360f",
				"properties":{
					"hostname":"192.168.186.102",
					"username":"postgres",
					"port":"5432",
					"properties":{
						
					},
					"schemaName":"public",
					"tableName":"shipments",
					"tableDefinition":{
						"tableBaseInfo":{
							"ifNotExists":true,
							"registerTableName":"shipments"
						},
						"physicalColumnDefinition":[
							{
								"columnName":"shipment_id",
								"columnType":"INT",
								"primaryKey":true
							},
							{
								"columnName":"order_id",
								"columnType":"INT"
							},
							{
								"columnName":"origin",
								"columnType":"STRING"
							},
							{
								"columnName":"destination",
								"columnType":"STRING"
							},
							{
								"columnName":"is_arrived",
								"columnType":"BOOLEAN"
							}
						],
						"asSelectStatement":{
							
						},
						"likeStatement":{
							
						}
					},
					"databaseName":"postgres",
					"slotName":"flink",
					"password":"postgres"
				},
				"customizedProperties":{
					
				}
			},
			{
				"name":"SQLQuery",
				"bundle":"cn.piflow.bundle.flink.common.SQLQuery",
				"uuid":"a5afa1368e1e4e348950bb2eda2011a8",
				"properties":{
					"viewName":"temp",
					"sql":"SELECT\n  o.*,\n  p.name,\n  p.description,\n  s.shipment_id,\n  s.origin,\n  s.destination,\n  s.is_arrived\nFROM\n  orders AS o\n  LEFT JOIN products AS p ON o.product_id = p.id\n  LEFT JOIN shipments AS s ON o.order_id = s.order_id"
				},
				"customizedProperties":{
					
				}
			},
			{
				"name":"ShowChangeLogData",
				"bundle":"cn.piflow.bundle.flink.common.ShowChangeLogData",
				"uuid":"c6604e600aa645d29264f4be5ce4e2eb",
				"properties":{
					"showNumber":"10"
				},
				"customizedProperties":{
					
				}
			},
			{
				"name":"products",
				"bundle":"cn.piflow.bundle.flink.cdc.mysql.MysqlCdc",
				"uuid":"ab91c42f26cb4e119e34830178611293",
				"properties":{
					"hostname":"192.168.186.102",
					"username":"root",
					"serverId":"5400",
					"port":"3306",
					"properties":{
						"server-time-zone":"UTC"
					},
					"tableName":"products",
					"tableDefinition":{
						"tableBaseInfo":{
							"registerTableName":"products"
						},
						"physicalColumnDefinition":[
							{
								"columnName":"id",
								"columnType":"INT",
								"primaryKey":true
							},
							{
								"columnName":"name",
								"columnType":"STRING"
							},
							{
								"columnName":"description",
								"columnType":"STRING"
							}
						],
						"asSelectStatement":{
							
						},
						"likeStatement":{
							
						}
					},
					"databaseName":"mydb",
					"password":"123456"
				},
				"customizedProperties":{
					
				}
			},
			{
				"name":"orders",
				"bundle":"cn.piflow.bundle.flink.cdc.mysql.MysqlCdc",
				"uuid":"4ffa58f3db6144739f6b797fd3839025",
				"properties":{
					"hostname":"192.168.186.102",
					"username":"root",
					"serverId":"5410",
					"port":"3306",
					"properties":{
						"scan.incremental.snapshot.chunk.key-column":"order_id",
						"server-time-zone":"UTC"
					},
					"tableName":"orders",
					"tableDefinition":{
						"tableBaseInfo":{
							"ifNotExists":true,
							"registerTableName":"orders"
						},
						"physicalColumnDefinition":[
							{
								"columnName":"order_id",
								"columnType":"INT"
							},
							{
								"columnName":"order_date",
								"columnType":"TIMESTAMP",
								"length":0
							},
							{
								"columnName":"customer_name",
								"columnType":"STRING"
							},
							{
								"columnName":"price",
								"columnType":"DECIMAL",
								"precision":10,
								"scale":5
							},
							{
								"columnName":"product_id",
								"columnType":"INT"
							},
							{
								"columnName":"order_status",
								"columnType":"BOOLEAN"
							}
						],
						"asSelectStatement":{
							
						},
						"likeStatement":{
							
						}
					},
					"databaseName":"mydb",
					"password":"123456"
				},
				"customizedProperties":{
					
				}
			}
		]
	}
}
```

#### 示例说明

本示例演示了基于PiflowX构建MySQL和Postgres的Streaming ETL。

#### 演示DEMO
