package com.froggengo.fbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class JavaConnectToHbaseTest {

    public static void main(String[] args) throws IOException {

        Configuration conf = HBaseConfiguration.create();
       // conf.set("hbase.zookeeper.quorum", "192.168.43.66");
        //conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.masters", "192.168.43.66:16020");
        Connection conn = ConnectionFactory.createConnection(conf);

        System.out.println(conn);
        Admin admin = conn.getAdmin();
        System.out.println(admin.tableExists(TableName.valueOf("test")));

    }

}
