package fr.mga.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mathieu.gandin
 */
public class SleepProcessor implements Processor {

    private final static Logger logger = LoggerFactory.getLogger(SleepProcessor.class);

    private int sleep;

    public SleepProcessor(int sleep) {
        this.sleep = sleep;
    }

    @Override public void process(Exchange exchange) throws Exception {
        logger.info("Sleeping {} ms", sleep);
        Thread.sleep(sleep);
    }
}
