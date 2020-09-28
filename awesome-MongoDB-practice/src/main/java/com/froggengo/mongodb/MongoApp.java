package com.froggengo.mongodb;

import com.mongodb.client.MongoClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoApp {

    private static final Log log = LogFactory.getLog(MongoApp.class);

    public static void main(String[] args) throws Exception {

        MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "database");
        mongoOps.insert(new Person("Joe", 34));

        log.info(mongoOps.findOne(new Query(where("name").is("Joe")), Person.class));
        System.out.println(mongoOps.findOne(new Query(where("name").is("Joe")), Person.class));
        //mongoOps.dropCollection("person");
    }
}
