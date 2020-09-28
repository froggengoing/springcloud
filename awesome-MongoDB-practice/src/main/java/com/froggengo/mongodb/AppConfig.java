package com.froggengo.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class AppConfig {
    /**
     * <mongo:mongo-client id="replicaSetMongo" replica-set="rs0">
     *     <mongo:client-settings cluster-hosts="127.0.0.1:27017,localhost:27018" />
     * </mongo:mongo-client>
     *
     *   <mongo:mongo-client host="localhost" port="27017">
     *     <mongo:client-settings connection-pool-max-connection-life-time="10"
     *         connection-pool-min-size="10"
     * 		connection-pool-max-size="20"
     * 		connection-pool-maintenance-frequency="10"
     * 		connection-pool-maintenance-initial-delay="11"
     * 		connection-pool-max-connection-idle-time="30"
     * 		connection-pool-max-wait-time="15" />
     *   </mongo:mongo-client>
      * @return
     */
    /*
     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
     */
    /*@Bean*/
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    /*
     * Factory bean that creates the com.mongodb.client.MongoClient instance
     * 这种方式更好 ：@Repository、ExceptionTranslator
     */
    public @Bean
    MongoClientFactoryBean mongo() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setHost("localhost");
        return mongo;
    }
}
