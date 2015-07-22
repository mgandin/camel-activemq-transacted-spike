package fr.mga.camel.legacy;

import org.apache.camel.component.jms.JmsEndpoint;

/**
 * @author mathieu.gandin
 */
public class LegacyJmsBindingFactory {
    public LegacyJmsBinding create(JmsEndpoint endpoint) {
        return endpoint != null ? new LegacyJmsBinding(endpoint) : new LegacyJmsBinding();
    }
}
