package fr.mga.camel.legacy;

import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsHeaderFilterStrategy;


/**
 * @author mathieu.gandin
 */
public class LegacyJmsHeaderFilterStrategy extends JmsHeaderFilterStrategy {

    private boolean relayJmsHeaders = true;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected boolean extendedFilter(Direction direction, String key, Object value, Exchange exchange) {

        // relay no JMS header
        if(!relayJmsHeaders && key.startsWith("JMS")) {
            return true;
        }

        return super.extendedFilter(direction, key, value, exchange);
    }

    public boolean isRelayJmsHeaders() {
        return relayJmsHeaders;
    }

    public void setRelayJmsHeaders(boolean relayJmsHeaders) {
        this.relayJmsHeaders = relayJmsHeaders;
    }

}

