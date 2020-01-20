package org.dimashup.reprocess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "reprocess.config.enabled", havingValue = "true")
public class ReprocessPropertyConfig {

    @Value("${reprocess.config.retry.count:5}")
    private Integer retryCount;

    public Integer getRetryCount() {
        return retryCount;
    }
}
