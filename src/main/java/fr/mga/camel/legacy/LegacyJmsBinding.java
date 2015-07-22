package fr.mga.camel.legacy;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.component.jms.JmsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mathieu.gandin
 */
public class LegacyJmsBinding extends JmsBinding {
    private static Logger log = LoggerFactory.getLogger(LegacyJmsBinding.class);

    public static String CONVERSATION_ID = "conversationId";

    public LegacyJmsBinding() {
        super();
    }

    public LegacyJmsBinding(JmsEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void appendJmsProperties(Message jmsMessage, Exchange exchange, org.apache.camel.Message in) throws JMSException {
        if (!isAdvisory(jmsMessage)) {
            String conversationId = exchange.getProperty(CONVERSATION_ID, String.class);
            if (conversationId != null) {
                log.debug("Using JMSXGroupId {}", conversationId);
                appendJmsProperty(jmsMessage, exchange, in, "JMSXGroupID", conversationId);
                appendJmsProperty(jmsMessage, exchange, in, CONVERSATION_ID, conversationId);
            } else {
                log.debug("Could not extract conversationId from Camel exchange");
            }
        }
        super.appendJmsProperties(jmsMessage, exchange, in);
    }

    public boolean isAdvisory(Message jmsMessage) {
        return jmsMessage instanceof ActiveMQMessage && ((ActiveMQMessage)jmsMessage).getDataStructure() != null;
    }

    @Override
    public Map<String, Object> extractHeadersFromJms(Message jmsMessage, Exchange exchange) {
        if (!isAdvisory(jmsMessage)) {
            String conversationId = null;
            try {
                conversationId = jmsMessage.getStringProperty(CONVERSATION_ID);
            } catch (JMSException e) {
            }
            if (conversationId != null) {
                log.debug("Extracted conversationId {} from JMS message", conversationId);
                exchange.setProperty(CONVERSATION_ID, conversationId);
            } else {
                log.debug("Could not extract conversationId from JMS message");
            }
        }

        return super.extractHeadersFromJms(jmsMessage, exchange);
    }

}
