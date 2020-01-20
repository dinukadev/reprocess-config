package org.dimashup.reprocess.service;

import java.io.Serializable;

public interface ReprocessConfigService {

    <T extends Serializable> void reprocess(Throwable throwable, T... methodParameters);
}
