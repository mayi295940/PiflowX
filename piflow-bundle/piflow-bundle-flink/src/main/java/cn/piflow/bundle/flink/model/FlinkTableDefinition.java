package cn.piflow.bundle.flink.model;

import cn.piflow.Constants;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * CREATE TABLE [IF NOT EXISTS] [catalog_name.][db_name.]table_name ( { <physical_column_definition>
 * | <metadata_column_definition> | <computed_column_definition> }[ , ...n] [ <watermark_definition>
 * ] [ <table_constraint> ][ , ...n] ) [COMMENT table_comment] [PARTITIONED BY
 * (partition_column_name1, partition_column_name2, ...)] WITH (key1=val1, key2=val2, ...) [ LIKE
 * source_table [( <like_options> )] | AS select_query ]
 *
 * <p><physical_column_definition>: column_name column_type [ <column_constraint> ] [COMMENT
 * column_comment]
 *
 * <p><column_constraint>: [CONSTRAINT constraint_name] PRIMARY KEY NOT ENFORCED
 *
 * <p><table_constraint>: [CONSTRAINT constraint_name] PRIMARY KEY (column_name, ...) NOT ENFORCED
 *
 * <p><metadata_column_definition>: column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL
 * ]
 *
 * <p><computed_column_definition>: column_name AS computed_column_expression [COMMENT
 * column_comment]
 *
 * <p><watermark_definition>: WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
 *
 * <p><source_table>: [catalog_name.][db_name.]table_name
 *
 * <p><like_options>: { { INCLUDING | EXCLUDING } { ALL | CONSTRAINTS | PARTITIONS } | { INCLUDING |
 * EXCLUDING | OVERWRITING } { GENERATED | OPTIONS | WATERMARKS } }[, ...]
 */
public class FlinkTableDefinition {

  private String catalogName;

  private String dbname;
  private String schema;

  private String tableName;

  private Boolean ifNotExists;

  /**
   * <physical_column_definition>:
   * column_name column_type [ <column_constraint> ] [COMMENT column_comment]
   */
  private List<FlinkTablePhysicalColumn> physicalColumnDefinition;

  /**
   * <metadata_column_definition>:
   * column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL]
   */
  private List<FlinkTableMetadataColumn> metadataColumnDefinition;

  /**
   * <computed_column_definition>:
   * column_name AS computed_column_expression [COMMENT column_comment]
   */
  private List<FlinkTableComputedColumn> computedColumnDefinition;

  /**
   * <watermark_definition>:
   * WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
   */
  private FlinkTableWatermark watermarkDefinition;

  public String getCatalogName() {
    return catalogName;
  }

  public void setCatalogName(String catalogName) {
    this.catalogName = catalogName;
  }

  public String getDbname() {
    return dbname;
  }

  public void setDbname(String dbname) {
    this.dbname = dbname;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public Boolean getIfNotExists() {
    return ifNotExists;
  }

  public void setIfNotExists(Boolean ifNotExists) {
    this.ifNotExists = ifNotExists;
  }

  public List<FlinkTablePhysicalColumn> getPhysicalColumnDefinition() {
    return physicalColumnDefinition;
  }

  public void setPhysicalColumnDefinition(List<FlinkTablePhysicalColumn> physicalColumnDefinition) {
    this.physicalColumnDefinition = physicalColumnDefinition;
  }

  public List<FlinkTableMetadataColumn> getMetadataColumnDefinition() {
    return metadataColumnDefinition;
  }

  public void setMetadataColumnDefinition(List<FlinkTableMetadataColumn> metadataColumnDefinition) {
    this.metadataColumnDefinition = metadataColumnDefinition;
  }

  public List<FlinkTableComputedColumn> getComputedColumnDefinition() {
    return computedColumnDefinition;
  }

  public void setComputedColumnDefinition(List<FlinkTableComputedColumn> computedColumnDefinition) {
    this.computedColumnDefinition = computedColumnDefinition;
  }

  public FlinkTableWatermark getWatermarkDefinition() {
    return watermarkDefinition;
  }

  public void setWatermarkDefinition(FlinkTableWatermark watermarkDefinition) {
    this.watermarkDefinition = watermarkDefinition;
  }

  public String getRealTableName() {
    String realTableName = "";
    if (StringUtils.isNotEmpty(catalogName)) {
      realTableName += catalogName + Constants.DOT();
    }
    if (StringUtils.isNotEmpty(dbname)) {
      realTableName += dbname + Constants.DOT();
    }

    if (StringUtils.isNotEmpty(schema)) {
      realTableName += "`" + schema + Constants.DOT() + tableName + "`";
    } else {
      realTableName += tableName;
    }

    return realTableName;
  }
}
