package org.dimashup.reprocess.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.dimashup.reprocess.domain.ReprocessConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SampleApplication.class})
@ActiveProfiles("test")
public class ReprocessConfigServiceImplTest {

    @Autowired
    private ReprocessTest reprocessTest;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    public void shouldSaveReprocessConfigStringParamPassed() {
        String test = "test";
        reprocessTest.testReprocessWithStringParam(test);

        ReprocessConfiguration reprocessConfiguration =
                mongoTemplate.findOne(Query.query(Criteria.where("className").is(ReprocessTest
                                .class.getCanonicalName())),
                        ReprocessConfiguration.class);
        assertNotNull(reprocessConfiguration);
    }

    @Test
    public void shouldSaveReprocessConfigWhenObjectParamPassed() {
        reprocessTest.testReprocessWithObject(new TestObject(RandomStringUtils.random(2),
                RandomStringUtils.random(2), DateTime.now()));

        ReprocessConfiguration reprocessConfiguration =
                mongoTemplate.findOne(Query.query(Criteria.where("className").is(ReprocessTest
                                .class.getCanonicalName())),
                        ReprocessConfiguration.class);
        assertNotNull(reprocessConfiguration);
    }
}
