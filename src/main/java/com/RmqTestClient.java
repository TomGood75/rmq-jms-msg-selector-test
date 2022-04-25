package com;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import com.rabbitmq.jms.client.RMQConnection;
import com.rabbitmq.jms.client.RMQMessageProducer;
import com.rabbitmq.jms.client.RMQSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TopicSubscriber;

public class RmqTestClient {
    final static String RMQ_HOST_STRING = "amqp://lme:lme@atscmn-lme-rmq-prd-01:5672/test";
    final static String TEST_MSG_SELECTOR = "for_user = 'bob'";
    final static String testTopicName = "testTopic";
    final static RMQDestination testTopic = new RMQDestination(testTopicName, false, false);

    final static String ARG_SUBSCRIBER_MODE = "S";
    final static String ARG_PUBLISHER_MODE = "P";

    final static String testMsg = "Hello World";
    final static long SUBSCRIBE_DURATION_IN_MS = 3000;
    final static long UNSUBSCRIBE_WAIT_IN_MS = 3000;

    private static Logger logger = LoggerFactory.getLogger(RmqTestClient.class);


    public static void main(String[] args) {
        String testMode = ARG_SUBSCRIBER_MODE;
//        String testMode = ARG_PUBLISHER_MODE;
        if(args != null && args.length == 1)
            testMode = args[0].toUpperCase().trim();
        if(ARG_PUBLISHER_MODE.equals(testMode))
            publishContinuously();
        else if(ARG_SUBSCRIBER_MODE.equals(testMode))
            subscribeAndDisconnectRepeatedly();
    }

    private static RMQConnection getNewConnection() throws JMSException {
        RMQConnectionFactory connectionFact = new RMQConnectionFactory();
        connectionFact.setUri(RMQ_HOST_STRING);
        RMQConnection connection = (RMQConnection) connectionFact.createTopicConnection();
        connection.start();
        return connection;
    }

    static void publishContinuously(){
        try {
            logger.info("Initializing infinite publishing loop.");
            RMQConnection connection = getNewConnection();
            RMQSession session = (RMQSession) connection.createSession(false, RMQSession.AUTO_ACKNOWLEDGE);

            while(true) {
                RMQMessageProducer publisher = (RMQMessageProducer) session.createPublisher(testTopic);
                ObjectMessage message = session.createObjectMessage(testMsg);
                message.setStringProperty("for_user", "bob");
                publisher.publish(message);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    static void subscribeAndDisconnectRepeatedly(){
        try {
            while(true) {
                logger.info("Initializing subscriber");
                RMQConnection connection = getNewConnection();
                RMQSession session = (RMQSession) connection.createTopicSession(false, RMQSession.AUTO_ACKNOWLEDGE);
                TopicSubscriber subscriber = session.createSubscriber(testTopic, TEST_MSG_SELECTOR, false);
                subscriber.setMessageListener(msg -> {
                });
                Thread.sleep(SUBSCRIBE_DURATION_IN_MS);
                logger.info("closing connection (including channel and subscriber)");
                connection.close();
                Thread.sleep(UNSUBSCRIBE_WAIT_IN_MS);

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

