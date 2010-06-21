package org.duracloud.common.util;


public class TableSpec {

    private final int version = 0;
    private String primaryKey;
    private String tableName;
    private String ddl;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public int getVersion() {
        return version;
    }
}
