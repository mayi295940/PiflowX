# JdbcCatalog组件

### 组件说明

通过JDBC协议将Flink连接到关系数据库,目前支持Postgres Catalog和MySQL Catalog。

### 计算引擎

flink

### 组件分组

Catalog

### 端口

Inport：默认端口

outport：默认端口

### 组件属性

| 名称              | 展示名称            | 默认值 | 允许值                        | 是否必填 | 描述                               | 例子          |
| --------------- | --------------- | --- | -------------------------- | ---- | -------------------------------- | ----------- |
| catalogName     | CatalogName     | ""  | 无                          | 是    | catalog名称。                       | my_catalog  |
| databaseType    | DatabaseType    | ""  | Set("postgresql", "mysql") | 是    | Postgres Catalog或 MySQL Catalog。 | mysql       |
| username        | Username        | ""  | 无                          | 是    | 连接到Oracle数据库服务器时要使用的Oracle用户的名称。 | root        |
| password        | Password        | ""  | 无                          | 是    | 连接Oracle数据库服务器时使用的密码。            | 123456      |
| ip              | ip              | ""  | 无                          | 是    | 数据库ip。                           | 127.0.0.1   |
| port            | port            | ""  | 无                          | 是    | 数据库端口。                           | 3306        |
| defaultDatabase | defaultDatabase | ""  | 无                          | 是    | 默认要连接的数据库。                       | my_database |

### JdbcCatalog示例配置

```json
{
  "flow": {
    "name": "test",
    "engineType": "flink",
    "uuid": "18f37694f6a0438fa920db1a8cbafc5e",
    "paths": [
      {
        "inport": "",
        "from": "JdbcCatalog",
        "to": "SQLQuery",
        "outport": ""
      },
      {
        "inport": "",
        "from": "SQLQuery",
        "to": "ShowData",
        "outport": ""
      }
    ],
    "stops": [
      {
        "name": "JdbcCatalog",
        "bundle": "cn.piflow.bundle.flink.catalog.JdbcCatalog",
        "uuid": "22ffe6f153fd4322a83de5462bd92a40",
        "properties": {
          "defaultDatabase": "test",
          "ip": "127.0.0.1",
          "username": "root",
          "catalogName": "mysql_catalog",
          "port": "3306",
          "databaseType": "mysql",
          "password": "123456"
        },
        "customizedProperties": {
        }
      },
      {
        "name": "SQLQuery",
        "bundle": "cn.piflow.bundle.flink.common.SQLQuery",
        "uuid": "b42fc07d326440b0a224c55d40b0b17c",
        "properties": {
          "registerSourceViewName": "",
          "registerResultViewName": "",
          "sql": "select\n  *\nfrom\n  mysql_catalog.test.test"
        },
        "customizedProperties": {
        }
      },
      {
        "name": "ShowData",
        "bundle": "cn.piflow.bundle.flink.common.ShowData",
        "uuid": "ed7c46b4552c4bc0960b91fdd0721d7e",
        "properties": {
          "showNumber": "100"
        },
        "customizedProperties": {
        }
      }
    ]
  }
}
```

#### 示例说明

1. 通过`JdbcCatalog`组件注册mysql catalog;

2. 使用`SQLQuery`组件执行`select * from mysql_catalog.test.test`语句，从mysql的test库查询test表数据；

3. 将上个节点的数据，使用`ShowData`组件打印到控制台。

#### 演示DEMO



[PiflowX-JdbcCatalog组件_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1te411E7U7/?vd_source=3fdc89de16a8f73489873ba5a0a3d2a7)
