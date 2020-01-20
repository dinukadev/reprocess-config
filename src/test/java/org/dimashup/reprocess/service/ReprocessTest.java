package org.dimashup.reprocess.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReprocessTest {

    @Autowired
    private ReprocessConfigService reprocessConfigService;

    public void testReprocessWithStringParam(String test) {
        try {
            throw new RuntimeException("Exception occurred");
        } catch (Exception e) {
            reprocessConfigService.reprocess(e, test);
        }
    }

    public void testReprocessWithObject(TestObject testObject) {
        try {
            throw new RuntimeException("Exception occurred");
        } catch (Exception e) {
            reprocessConfigService.reprocess(e, testObject);
        }
    }
}
