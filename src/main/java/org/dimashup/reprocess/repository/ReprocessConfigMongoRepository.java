package org.dimashup.reprocess.repository;

import org.dimashup.reprocess.domain.ReprocessConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReprocessConfigMongoRepository extends MongoRepository<ReprocessConfiguration, String> {

    Page<ReprocessConfiguration> findByStatusAndRetryCountGreaterThan(Pageable pageable, String status,
                                                                      Integer retryCount);
}
