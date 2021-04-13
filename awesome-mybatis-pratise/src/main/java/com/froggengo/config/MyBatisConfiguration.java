package com.froggengo.config;

import com.alibaba.druid.pool.DruidDataSource;
import java.io.IOException;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class MyBatisConfiguration {

    @Bean
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/db2019?userUnicode=true&amp;characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        return dataSource;
    }

    //根据配置文件生生sqlfactory
    @Bean
    public SqlSessionFactory getSqlSessionFactory(DataSource ds) throws IOException {
        SqlSessionFactoryBuilder factoryBuilder = new SqlSessionFactoryBuilder();
        ClassPathResource resource = new ClassPathResource("mybatis/mybatis-config.xml");
        XMLConfigBuilder parser = new XMLConfigBuilder(resource.getInputStream(), null, null);
        org.apache.ibatis.session.Configuration configuration = parser.parse();
        Environment environment = new Environment("env", new SpringManagedTransactionFactory(), ds);
        configuration.setEnvironment(environment);
        SqlSessionFactory sqlSessionFactory = factoryBuilder.build(configuration);
        return sqlSessionFactory;
    }

/*    @Bean
     public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }*/
}
