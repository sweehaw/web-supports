package io.github.sweehaw.websupports.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import lombok.Data;

/**
 * @author sweehaw
 */
@Data
public class DynamoDbLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public static final int MSG_SIZE_LIMIT = 65535;

    private AbstractDaemonAppender<ILoggingEvent> appender;
    private Layout<ILoggingEvent> layout;

    private String filePath;
    private String endPoint;
    private String tableName;
    private String instanceName;

    private int maxQueueSize;

    private boolean initializeAppender() {
        try {
            PropertiesCredentials credentials = new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream(filePath));
            AmazonDynamoDBClient dynamoClient = new AmazonDynamoDBClient(credentials);
            dynamoClient.setEndpoint(this.endPoint);
            this.appender = new DynamoDbAbstractDaemonAppender(this.tableName, this.instanceName, dynamoClient, this.layout, this.maxQueueSize);
            return true;
        } catch (Exception e) {
            System.err.println("Could not initialize " + DynamoDbLogbackAppender.class.getCanonicalName() + " ( will try to initialize again later ): " + e);
            return false;
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (this.appender == null) {
            synchronized (this) {
                if (!initializeAppender()) {
                    System.err.println("The log which supposed to be added to DynamoDB was not added because the dynamo db appender could not be initialized. Ignored the message: " + System.lineSeparator() + eventObject.toString());
                    return;
                }
            }
            this.append(eventObject);
            return;
        }
        eventObject.prepareForDeferredProcessing();
        this.appender.log(eventObject);
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } finally {
            synchronized (this) {
                if (this.appender != null) {
                    this.appender.close();
                }
            }
        }
    }
}
