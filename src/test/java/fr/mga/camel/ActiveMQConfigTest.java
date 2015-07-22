package fr.mga.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.*;

/**
 * @author mathieu.gandin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ActiveMQConfig.class, AtomikosConfig.class})
public class ActiveMQConfigTest {

    private static Logger logger = LoggerFactory.getLogger(ActiveMQConfigTest.class);

    @Test
    public void should_send_and_receive_jms_message() throws InterruptedException {
        Thread jmsProducer = new Thread() {
            public void run() {
                try {
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://test");

                    Connection connection = connectionFactory.createConnection();
                    connection.start();

                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    Destination destination = session.createQueue("confirm-booking");

                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                    String text = "Hello world!";
                    TextMessage message = session.createTextMessage(text);
                    logger.info("Sent message: "+ message);
                    producer.send(message);

                    session.close();
                    connection.close();
                }
                catch (Exception e) {
                    logger.info("Caught: " + e);
                    e.printStackTrace();
                }
            }

        };
        Thread jmsConsumer = new Thread() {
            public void run() {
                try {
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://test");

                    Connection connection = connectionFactory.createConnection();
                    connection.start();

                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    Destination destination = session.createQueue("confirm-booking");

                    MessageConsumer consumer = session.createConsumer(destination);
                    Message message = consumer.receive();

                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        logger.info("Received: " + text);
                        Assertions.assertThat(text).isEqualTo("Hello world!");
                    } else {
                        logger.info("Received: " + message);
                    }

                    consumer.close();
                    session.close();
                    connection.close();
                } catch (Exception e) {
                    logger.info("Caught: " + e);
                    e.printStackTrace();
                }
            }
        };

        jmsProducer.setDaemon(false);
        jmsProducer.start();
        jmsConsumer.setDaemon(false);
        jmsConsumer.start();

        Thread.sleep(2000);
    }
}
