package com.froggengo.fbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class FhbaseMain2 {
    public static void main(String[] args) {
        Configuration configuration;
        Connection connection = null;
        ResultScanner scanner = null;
        HBaseAdmin admin=null;
        try {
            configuration = HBaseConfiguration.create();
            Properties properties = new Properties();
            properties.load(new ClassPathResource("application.properties").getInputStream());
            properties.forEach((k,v)-> {
                configuration.set((String)k,(String)v);
            });
            connection = ConnectionFactory.createConnection(configuration);
            Table table1 = connection.getTable(TableName.valueOf("test"));
            scanner = table1.getScanner(new Scan());
            for (Result res : scanner) {
                byte[] row = res.getRow();
                System.out.println(Bytes.toString(row));
                for (Cell cell : res.rawCells()) {
                    System.out.println("  "+cell);
                    System.out.println("    行"+new String(CellUtil.cloneRow(cell)));
                    System.out.println("    "+new String(CellUtil.cloneFamily(cell)));
                    System.out.println("    "+new String(CellUtil.cloneQualifier(cell)));
                    System.out.println("    "+new String(CellUtil.cloneValue(cell)));
                    System.out.println("    "+cell.getTimestamp());
                }
            }
            //Result row1 = table1.get(new Get(Bytes.toBytes("row1")));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
