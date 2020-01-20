package org.dimashup.reprocess.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.dimashup.reprocess.config.ReprocessPropertyConfig;
import org.dimashup.reprocess.constant.ReprocessConstant;
import org.dimashup.reprocess.domain.ReprocessConfiguration;
import org.dimashup.reprocess.exception.ReprocessException;
import org.dimashup.reprocess.repository.ReprocessConfigMongoRepository;
import org.dimashup.reprocess.service.ReprocessConfigService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.LinkedHashMap;

@ConditionalOnProperty(name = "reprocess.config.enabled", havingValue = "true")
@Service
public class ReprocessConfigServiceImpl implements ReprocessConfigService {

    private static final Logger log = LoggerFactory.getLogger(ReprocessConfigService.class);

    @Autowired
    private ReprocessPropertyConfig reprocessConfig;

    @Autowired
    private ReprocessConfigMongoRepository reprocessConfigMongoRepository;

    /**
     * The method parameters passed in here need to be implementing serializable
     *
     * @param throwable
     * @param methodParameters
     */
    @Override
    public <T extends Serializable> void reprocess(Throwable throwable, T... methodParameters) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        log.info("Calling reprocess on class : {} method : {} for exception : {}",
                className, methodName, throwable);
        LinkedHashMap<String, String> parameterNameValue = new LinkedHashMap<>();
        for (Object object : methodParameters) {
            if (object == null) {
                log.error("Passed in a null object on class : {} and method : {}", className, methodName);
                throw new RuntimeException("Unable to reprocess as null object was passed");
            }
            Pair<String, String> objInfoPair = getParameterClassAndObjAsByteArray(object);
            parameterNameValue.put(objInfoPair.getLeft(), objInfoPair.getRight());
        }

        log.info("Saving the reprocess configuration for class : {} and method : {}", className, methodName);
        ReprocessConfiguration reprocessConfiguration = new ReprocessConfiguration(className, methodName, parameterNameValue,
                reprocessConfig.getRetryCount(), ReprocessConstant.ReprocessStatus.NEW,
                throwable != null ? throwable.getMessage() : null,
                DateTime.now());
        reprocessConfigMongoRepository.save(reprocessConfiguration);
        log.info("Successfully saved the reprocess config for class : {} and method : {}", className, methodName);
    }

    /**
     * We serialize the object and get it as a byte stream
     *
     * @param object
     * @return
     */
    private Pair<String, String> getParameterClassAndObjAsByteArray(Object object) {
        Pair<String, String> pair;
        //As mongo keys does not play nice with dots, the dots are replaced by a forward slash before saving
        String parameterClassName = object.getClass().getCanonicalName().replaceAll("\\.", "/");
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                byte[] objByteArr = byteArrayOutputStream.toByteArray();
                String base64EncodedByteArr = Base64.getEncoder().encodeToString(objByteArr);
                pair = Pair.of(parameterClassName, base64EncodedByteArr);
            } catch (IOException e) {
                log.error("Exception occurred while trying to serialize object : {} with exception : {}",
                        object, e);
                throw new ReprocessException(e);
            }
        } catch (IOException e) {
            log.error("Exception occurred while trying to serialize object : {} with exception : {}",
                    object, e);
            throw new ReprocessException(e);
        }
        return pair;
    }
}
