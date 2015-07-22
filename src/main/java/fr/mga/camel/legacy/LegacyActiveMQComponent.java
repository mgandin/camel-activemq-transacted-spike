package fr.mga.camel.legacy;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Endpoint;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.component.jms.JmsEndpoint;

/**
 * @author mathieu.gandin
 */
public class LegacyActiveMQComponent extends ActiveMQComponent {

    private LegacyJmsBindingFactory jmsBindingFactory;

    @Override
    public Endpoint createEndpoint(String uri) throws Exception {
        Endpoint endpoint = super.createEndpoint(uri);
        if (endpoint != null && endpoint instanceof JmsEndpoint) {
            JmsEndpoint jmsEndpoint = (JmsEndpoint) endpoint;
            if( (jmsEndpoint.getBinding() == null || jmsEndpoint.getBinding().getClass().equals(JmsBinding.class)) && getJmsBindingFactory() != null) {
                jmsEndpoint.setBinding(getJmsBindingFactory().create(jmsEndpoint));
            }
        }
        return endpoint;
    }

    public LegacyJmsBindingFactory getJmsBindingFactory() {
        return jmsBindingFactory;
    }

    public void setJmsBindingFactory(LegacyJmsBindingFactory jmsBindingFactory) {
        this.jmsBindingFactory = jmsBindingFactory;
    }
}
