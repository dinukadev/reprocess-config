package org.dimashup.reprocess.service.impl;

import org.dimashup.reprocess.constant.ReprocessConstant;
import org.dimashup.reprocess.domain.ReprocessConfiguration;
import org.dimashup.reprocess.exception.ReprocessException;
import org.dimashup.reprocess.repository.ReprocessConfigMongoRepository;
import org.dimashup.reprocess.service.ReprocessService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "reprocess.config.enabled", havingValue = "true")
public class ReprocessServiceImpl implements ReprocessService, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ReprocessService.class);

    @Autowired
    private ReprocessConfigMongoRepository reprocessConfigMongoRepository;

    private ApplicationContext applicationContext;

    /**
     * This method will read the reprocess configuration and invoke the method on the class as per the reprocess configuration.
     * The assumption is that the application is running within a Spring context and the class/method to be invoked
     * is already registered in the Spring context.
     *
     * @param reprocessConfiguration
     */
    @Override
    public void process(ReprocessConfiguration reprocessConfiguration) {
        String reprocessConfigId = reprocessConfiguration.getId();
        log.info("Reprocessing configuration with id : {}", reprocessConfigId);
        try {
            Class reprocessClass = Class.forName(reprocessConfiguration.getClassName());
            Object objectToInvoke = applicationContext.getBean(reprocessClass);

            String methodName = reprocessConfiguration.getMethodName();
            List<Class> parameterTypesList = new LinkedList<>();
            List<Object> paramValueList = new LinkedList<>();
            for (String parameterType : reprocessConfiguration.getParamNameValue().keySet()) {
                //Here we put back the dot which we replaced with a forward slash when saving the class name
                //as it causes issues with mongo if we had a dot
                String formattedParameterType = parameterType.replaceAll("/", "\\.");
                Class paramClass = Class.forName(formattedParameterType);
                parameterTypesList.add(paramClass);
                String base64ByteStream = reprocessConfiguration.getParamNameValue().get(parameterType);
                byte[] decodedByteStream = Base64.getDecoder().decode(base64ByteStream);
                paramValueList.add(getObjectFromByteStream(decodedByteStream));
            }
            Method method = reprocessClass.getMethod(methodName, parameterTypesList.toArray(new Class[]{}));
            method.invoke(objectToInvoke, paramValueList.toArray(new Object[]{}));
            log.info("Successfully invoked the reprocess configuration with id : {} for class : {} " +
                            "and method : {}", reprocessConfigId,
                    reprocessConfiguration.getClassName(), reprocessConfiguration.getMethodName());
            reprocessConfiguration.setStatus(ReprocessConstant.ReprocessStatus.COMPLETED);
            reprocessConfiguration.setArchived(true);
        } catch (Exception e) {
            log.error("Exception occurred while trying to reprocess configuration with id : {}" +
                    " with exception : {}", reprocessConfigId, e);
            if (reprocessConfiguration.getRetryCount().intValue() - 1 == 0) {
                reprocessConfiguration.setStatus(ReprocessConstant.ReprocessStatus.FAILED);
                reprocessConfiguration.setRetryCount(reprocessConfiguration.getRetryCount() - 1);
            } else {
                reprocessConfiguration.setRetryCount(reprocessConfiguration.getRetryCount() - 1);
                reprocessConfiguration.setStatus(ReprocessConstant.ReprocessStatus.NEW);
            }
        } finally {
            reprocessConfiguration.setLastUpdated(DateTime.now());
            reprocessConfigMongoRepository.save(reprocessConfiguration);
        }

    }


    /**
     * As we saved the serialized version of the object, we deserialize them back to its object form here by reading the
     * byte stream saved in the DB.
     *
     * @param objByteStream
     * @return
     */
    private Object getObjectFromByteStream(byte[] objByteStream) {
        Object objToReturn = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objByteStream)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                objToReturn = objectInputStream.readObject();
            }

        } catch (Exception e) {
            log.error("Exception occurred while trying to read the object byte stream with exception : {}", e);
            throw new ReprocessException(e);
        }
        return objToReturn;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
