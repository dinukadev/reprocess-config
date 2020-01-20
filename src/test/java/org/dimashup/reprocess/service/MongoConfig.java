package org.dimashup.reprocess.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;


public class MongoConfig {

    @Configuration
    @EnableMongoRepositories(basePackages = {"org.dimashup.reprocess"})
    @EnableTransactionManagement
    @Profile({"dev", "test"})
    public static class MongoConfigDev extends AbstractMongoClientConfiguration {

        @Autowired
        private SystemConfigProperties systemConfigProperties;

        @Override
        protected String getDatabaseName() {
            String serviceName = systemConfigProperties.getServerName();
            String environmentName = systemConfigProperties.getActiveSpringProfile();
            return serviceName + "-" + environmentName;
        }

        @Override
        public MongoClient mongoClient() {
            String mongoUri = systemConfigProperties.getMongoUri();
            String[] addresses = mongoUri.split(",");
            List<ServerAddress> servers = new ArrayList<>();
            for (String address : addresses) {
                String[] split = address.trim().split(":");
                servers.add(new ServerAddress(split[0].trim(), Integer.parseInt(split[1].trim())));

            }
            MongoClientSettings settings = MongoClientSettings.builder().applyToClusterSettings(builder ->
                    builder.hosts(servers)
            ).build();
            return MongoClients.create(settings);
        }
    }


}
