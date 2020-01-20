# Introduction

This document explains the way we reprocess failures from asynchronous processes such as reading from queue.

## Configuration

Enable the following configuration in your `application.yaml`

```
reprocess:
  config:
    enabled: true

```


## Code changes
In the services that process asynchronous messages from the queue, in event of an error, pass the details to the `ReprocessConfigService` as follows;

```
    @Autowired
    private ReprocessConfigService reprocessConfigService;

    @Override
    @Async("processExecutor")
    public void processDataEvents(NotificationEventDto notificationEventDto) {
        try {
            //your business logic
        } catch (Exception e) {
            log.error("Exception received while trying to pass the message received : {}", e);
            reprocessConfigService.reprocess(e, notificationEventDto);
        }
    }

```

As you can see, on exception we need to call the `reprocessConfigService.reprocess` passing the exception object and the method parameters of the object which in this case is the `notificationEventDto`. 

One thing to note is that you can only pass objects that implement Serializable as we write this in a mongo collection as an object stream and hence need it to be serializable.



### ReprocessConfigServiceImpl.java

This class deals with writing the exceptions into a mongo collection which will later can be picked up by a scheduler task to be reprocessed. The method gets the class and the method from where the error originated by calling `Thread.currentThread().getStackTrace()[2].getMethodName()` to get the method name, and `Thread.currentThread().getStackTrace()[2].getClassName()` to get the class name. 

Why `getStackTrace()[2]`? Because the `[0]` belongs to the `Thread` while the `[1]` belongs to the current executing class which in this case is the `ReprocessConfigServiceImpl.java`.

After that, all objects passed in are serialized and the byte stream from that is base 64 encoded and saved to the mongo collection as a key value pair where the key is the class name of the parameter.

When saving the parameter types, the dots are converted to a forward slash due to a limitation in mongo to use dots as attribute names. So `java.lang.String` is converted to `java/lang/String` when persisting to Mongo and later when read, it is converted back to the dot format to load the class from the class loader.

### ReprocessServiceImpl.java

This class is called from the `ReprocessTask.java` as a scheduler task. The records that needs to be reprocessed are fetched as a first step.

Afterwards, the class is retrieved via the Application context and the respective method is called by deserializing the method parameters from the byte stream stored in mongo.

