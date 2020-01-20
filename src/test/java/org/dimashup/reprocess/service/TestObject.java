package org.dimashup.reprocess.service;

import org.joda.time.DateTime;

import java.io.Serializable;

public class TestObject implements Serializable {

    private String valueOne;

    private String valueTwo;

    private DateTime dateTime;

    public TestObject(String valueOne, String valueTwo, DateTime dateTime) {
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        this.dateTime = dateTime;
    }

    public String getValueOne() {
        return valueOne;
    }

    public String getValueTwo() {
        return valueTwo;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
}
