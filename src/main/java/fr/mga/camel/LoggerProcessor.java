package fr.mga.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.TextMessage;

/**
 * @author mathieu.gandin
 */
public class LoggerProcessor implements Processor {
    private final static Logger logger = LoggerFactory.getLogger(LoggerProcessor.class);

    @Override public void process(Exchange exchange) throws Exception {
        String text = exchange.getIn().getBody(String.class);
        exchange.getOut().setBody(text.toUpperCase());
        logger.info("Header ::: {}",exchange.getIn().getHeader("counter"));
    }
}
