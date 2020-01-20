package org.dimashup.reprocess.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashMap;

@Document(collection = "reprocessConfig")
public class ReprocessConfiguration {

    @Id
    private String id;

    private String className;

    private String methodName;

    private LinkedHashMap<String, String> paramNameValue;

    private Integer retryCount;

    private String status;

    private String exceptionMessage;

    private DateTime created;

    private DateTime lastUpdated;

    private boolean isArchived;

    public ReprocessConfiguration(String className, String methodName, LinkedHashMap<String, String> paramNameValue, Integer retryCount, String status, String exceptionMessage, DateTime created) {
        this.className = className;
        this.methodName = methodName;
        this.paramNameValue = paramNameValue;
        this.retryCount = retryCount;
        this.status = status;
        this.exceptionMessage = exceptionMessage;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public LinkedHashMap<String, String> getParamNameValue() {
        return paramNameValue;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
