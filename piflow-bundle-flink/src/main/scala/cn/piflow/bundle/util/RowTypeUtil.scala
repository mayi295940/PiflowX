package cn.piflow.bundle.util

import cn.piflow.Constants
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.java.typeutils.RowTypeInfo

object RowTypeUtil {

  /**
   * 生成Row类型的TypeInformation.
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
}
