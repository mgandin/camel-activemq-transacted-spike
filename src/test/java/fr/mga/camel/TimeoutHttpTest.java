package fr.mga.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author mathieu.gandin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TimeoutHttpTest.Config.class)
public class TimeoutHttpTest {
    private ProducerTemplate template;

    @Autowired
    private CamelContext context;

    @Test
    public void should_time_out() throws Exception {
        template = new DefaultProducerTemplate(context);
        template.start();
        Exchange exchange = new DefaultExchange(context);
        template.send("direct:start",exchange);
    }


    @Configuration
    public static class Config {
        @Bean
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override public void configure() throws Exception {
                    from("direct:start")
                    .to("http://localhost:6797/request_token")
                    .process(new LoggerProcessor());
                }
            };
        }

        @Bean
        public CamelContext context(RouteBuilder route) throws Exception {
            CamelContext context = new SpringCamelContext();
            context.addRoutes(route);
            return context;
        }
    }
}
