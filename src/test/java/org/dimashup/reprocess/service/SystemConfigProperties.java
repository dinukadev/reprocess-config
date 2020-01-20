package org.dimashup.reprocess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SystemConfigProperties {

    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

    @Value("${server.name}")
    private String serverName;


    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;


    public String getActiveSpringProfile() {
        return activeSpringProfile;
    }

    public String getServerName() {
        return serverName;
    }


    public String getMongoUri() {
        return mongoUri;
    }


}
