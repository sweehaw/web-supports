package io.github.sweehaw.websupports.appender;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sweehaw
 */
public class DynamoDbAbstractDaemonAppender extends AbstractDaemonAppender<ILoggingEvent> {

    private final String tableName;
    private final String instanceName;
    private final AmazonDynamoDBClient dynamoClient;
    private final Layout<ILoggingEvent> layout;

    DynamoDbAbstractDaemonAppender(String tableName, String instanceName, AmazonDynamoDBClient dynamoClient, Layout<ILoggingEvent> layout, int maxQueueSize) {
        super(maxQueueSize);
        this.tableName = tableName;
        this.instanceName = instanceName;
        this.dynamoClient = dynamoClient;
        this.layout = layout;
    }

    @Override
    protected void append(ILoggingEvent rawData) {
        String msg;
        if (this.layout != null) {
            msg = this.layout.doLayout(rawData);
        } else {
            msg = rawData.toString();
        }

        if (msg != null && msg.length() > DynamoDbLogbackAppender.MSG_SIZE_LIMIT) {
            msg = msg.substring(0, DynamoDbLogbackAppender.MSG_SIZE_LIMIT);
        }

        Date loggerDate = new Date(rawData.getTimeStamp());

        String level = rawData.getLevel().levelStr;
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(loggerDate);
        String id = date + "-" + RandomStringUtils.randomAlphanumeric(8);
        String ipAdd = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAdd = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Map<String, AttributeValue> data = new HashMap<>(5);
        data.put("instance", new AttributeValue().withS(this.instanceName));
        data.put("id", new AttributeValue().withS(id));
        data.put("ip address", new AttributeValue().withS(ipAdd));
        data.put("level", new AttributeValue().withS(level));
        data.put("msg", new AttributeValue().withS(msg));

        PutItemRequest itemRequest = new PutItemRequest().withTableName(this.tableName).withItem(data);
        this.dynamoClient.putItem(itemRequest);
    }

    @Override
    protected void close() {
        try {
            super.close();
        } finally {
            this.dynamoClient.shutdown();
        }
    }
}
