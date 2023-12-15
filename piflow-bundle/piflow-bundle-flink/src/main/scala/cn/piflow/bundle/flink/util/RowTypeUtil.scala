package cn.piflow.bundle.flink.util

import cn.piflow.Constants
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.table.api.{DataTypes, Schema}

object RowTypeUtil {

  /**
   * 生成Row类型的TypeInformation
   */
  def getRowTypeInfo(schema: String): RowTypeInfo = {
    val field = schema.split(Constants.COMMA)

    val columnTypes = new Array[TypeInformation[_]](field.size)

    val fieldNames = new Array[String](field.size)

    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      fieldNames(i) = columnName

      columnType.toLowerCase() match {
        case "string" => columnTypes(i) = BasicTypeInfo.STRING_TYPE_INFO
        case "int" => columnTypes(i) = BasicTypeInfo.INT_TYPE_INFO
        case "double" => columnTypes(i) = BasicTypeInfo.DOUBLE_TYPE_INFO
        case "float" => columnTypes(i) = BasicTypeInfo.FLOAT_TYPE_INFO
        case "long" => columnTypes(i) = BasicTypeInfo.LONG_TYPE_INFO
        case "boolean" => columnTypes(i) = BasicTypeInfo.BOOLEAN_TYPE_INFO
        case "date" => columnTypes(i) = BasicTypeInfo.DATE_TYPE_INFO
        case "timestamp" => columnTypes(i) = BasicTypeInfo.DATE_TYPE_INFO
        case _ =>
          throw new RuntimeException("Unsupported type: " + columnType)
      }
    }

    val info = new RowTypeInfo(columnTypes, fieldNames)

    info
  }


  /**
   * 生成Row类型的TypeInformation.
   */
  def getRowSchema(schema: String): Schema = {
    val schemaBuilder = Schema.newBuilder()
    val field = schema.split(Constants.COMMA)
    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      // todo more type
      // todo date format

      columnType.toLowerCase() match {
        case "string" => schemaBuilder.column(columnName, DataTypes.STRING())
        case "int" => schemaBuilder.column(columnName, DataTypes.INT())
        case "double" => schemaBuilder.column(columnName, DataTypes.DOUBLE())
        case "float" => schemaBuilder.column(columnName, DataTypes.FLOAT())
        case "long" => schemaBuilder.column(columnName, DataTypes.BIGINT())
        case "boolean" => schemaBuilder.column(columnName, DataTypes.BOOLEAN())
        case "date" => schemaBuilder.column(columnName, DataTypes.DATE())
        case "timestamp" => schemaBuilder.column(columnName, DataTypes.TIMESTAMP())
        case _ =>
          throw new RuntimeException("Unsupported type: " + columnType)
      }
    }
    schemaBuilder.build()
  }


  /**
   * 生成table Schema
   */
  def getTableSchema(schema: String): String = {

    var primaryKey:String = ""
    var sourceDDL = ""

    val field = schema.split(Constants.COMMA)
    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      columnType.toLowerCase() match {
        case "string" => sourceDDL += s"  $columnName ${DataTypes.STRING()},"
        case "int" => sourceDDL += s"  $columnName ${DataTypes.INT()},"
        case "double" => sourceDDL += s"  $columnName ${DataTypes.DOUBLE()},"
        case "float" => sourceDDL += s"  $columnName ${DataTypes.FLOAT()},"
        case "long" => sourceDDL += s"  $columnName ${DataTypes.BIGINT()},"
        case "boolean" => sourceDDL += s"  $columnName ${DataTypes.BOOLEAN()},"
        case "date" => sourceDDL += s"  $columnName ${DataTypes.DATE()},"
        case "timestamp" => sourceDDL += s"  $columnName ${DataTypes.TIMESTAMP()},"
        case _ =>
          throw new RuntimeException("Unsupported type: " + columnType)
      }
    }

    if (StringUtils.isNotBlank(primaryKey)) {
      sourceDDL = sourceDDL.stripMargin + s"PRIMARY KEY ($primaryKey) NOT ENFORCED"
      sourceDDL
    }
    else {
      sourceDDL.stripMargin.dropRight(1)
    }
  }

}
