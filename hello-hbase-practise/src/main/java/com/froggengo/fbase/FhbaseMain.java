package com.froggengo.fbase;

import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * https://blog.csdn.net/vbirdbest/article/details/88410954
 */
//@SpringBootApplication
public class FhbaseMain {
    public static void main(String[] args) {
        //默认的构造方式会从hbase-default.xml和hbase-site.xml中读取配置
        //Configuration con= HBaseConfiguration.create();
/*        Properties properties = new Properties();
        properties.load(new ClassPathResource("application.properties").getInputStream());
        properties.forEach((k,v)-> System.out.println(k+"=="+v));*/

    }
}
