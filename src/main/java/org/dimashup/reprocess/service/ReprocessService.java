package org.dimashup.reprocess.service;

import org.dimashup.reprocess.domain.ReprocessConfiguration;

public interface ReprocessService {

    void process(ReprocessConfiguration reprocessConfiguration);
}
