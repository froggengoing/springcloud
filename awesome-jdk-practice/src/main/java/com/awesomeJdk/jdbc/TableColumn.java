package com.awesomeJdk.jdbc;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author froggengo@qq.com
 * @date 2021/1/30 13:32.
 */
public class TableColumn {

    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://localhost:3306/bitwells?user=root&password=123456";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(url);
            String dbName = connection.getCatalog();
            System.out.println("数据库名：" + dbName);
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(dbName, "%", "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String tableRemark = tables.getString("REMARKS");
                System.out.println(tableName);
                System.out.println(tableRemark);

//                ResultSetMetaData tableMeta = tables.getMetaData();
//                int columnCount = tableMeta.getColumnCount();
//                for (int i = 0; i < columnCount; i++) {
//                    TableInfo tableInfo = new TableInfo(tableMeta, i);
//                    System.out.println(tableInfo);
//                }
                //获取所有字段
                ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
//                列名称
                while (colRet.next()){
                    TableInfo tableInfo = new TableInfo(colRet).invoke();
                    System.out.println(tableInfo);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printlnStr(String columnName, String columnType, int dataSize, int digits, int nullable,
                                   int remarks) {

    }

    private static class TableInfo {

        private ResultSet colRet;
        private String columnName;
        private String columnType;
        private int dataSize;
        private int digits;
        private int nullable;
        private String remarks;

        public TableInfo(ResultSet colRet) {
            this.colRet = colRet;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getColumnType() {
            return columnType;
        }

        public int getDataSize() {
            return dataSize;
        }

        public int getDigits() {
            return digits;
        }

        public int getNullable() {
            return nullable;
        }

        public String getRemarks() {
            return remarks;
        }

        public TableInfo invoke() throws SQLException {
            columnName = colRet.getString("COLUMN_NAME");
//                DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
//                TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
            columnType = colRet.getString("TYPE_NAME");
//                COLUMN_SIZE int => 列的大小。
            dataSize = colRet.getInt("COLUMN_SIZE");
//                DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
            digits = colRet.getInt("DECIMAL_DIGITS");
//                是否允许使用 NULL
            nullable = colRet.getInt("NULLABLE");
//                列注释
            remarks = colRet.getString("REMARKS");
            return this;
        }

        @Override
        public String toString() {
            return "TableInfo{" +
                "columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", dataSize=" + dataSize +
                ", digits=" + digits +
                ", nullable=" + nullable +
                ", remarks=" + remarks +
                '}';
        }
    }
}
