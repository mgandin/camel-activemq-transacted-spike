package fr.mga.camel;

import fr.mga.camel.legacy.LegacyActiveMQComponent;
import fr.mga.camel.legacy.LegacyJmsBindingFactory;
import fr.mga.camel.legacy.LegacyJmsHeaderFilterStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mathieu.gandin
 */
@Configuration
public class Orchestration {

    @Bean
    public LoggerProcessor activeMQProcessor() {
        return new LoggerProcessor();
    }

    @Bean
    public RouteBuilder directToActiveMQ() {
        return new RouteBuilder() {
            @Override public void configure() throws Exception {
                from("direct:start")
                        .to("activemq:queue:confirm-booking?mapJmsMessage=true&requestTimeout=10000&transferExchange=false&replyTo=confirm-booking-reply&concurrentConsumers=5");//&transactedInOut=true&replyToType=Exclusive");
            }
        };
    }

    @Bean
    public RouteBuilder activeMQToCode(final LoggerProcessor activeMQProcessor) {
        return new RouteBuilder() {
            @Override public void configure() throws Exception {
                from("activemq:queue:confirm-booking?mapJmsMessage=true&requestTimeout=10000&transferExchange=false&replyTo=confirm-booking-reply&concurrentConsumers=5")//&transactedInOut=true&replyToType=Exclusive")
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .to("http://localhost:6797/payment")
                        .process(activeMQProcessor)
                .to("mock:end");
            }
        };
    }

    @Bean
    public LegacyJmsBindingFactory jmsBindingFactory() {
        return new LegacyJmsBindingFactory();
    }

    @Bean
    public LegacyJmsHeaderFilterStrategy jmsHeaderFilterStrategy() {
        return new LegacyJmsHeaderFilterStrategy();
    }

    @Bean
    public LegacyActiveMQComponent activemq(JmsConfiguration jmsConfig,
            LegacyJmsBindingFactory jmsBindingFactory,
            LegacyJmsHeaderFilterStrategy jmsHeaderFilterStrategy) {
        LegacyActiveMQComponent activeMQComponent = new LegacyActiveMQComponent();
        activeMQComponent.setConfiguration(jmsConfig);
        activeMQComponent.setTransferException(true);
        activeMQComponent.setJmsBindingFactory(jmsBindingFactory);
        activeMQComponent.setHeaderFilterStrategy(jmsHeaderFilterStrategy);
        return activeMQComponent;
    }
}
