package fr.mga.camel;

import fr.mga.camel.legacy.LegacyJmsBinding;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author mathieu.gandin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ActiveMQConfig.class, Orchestration.class, OrchestrationTest.Config.class, AtomikosConfig.class})
public class OrchestrationTest {

    private ProducerTemplate template;

    @Autowired
    private CamelContext context;

    @Test
    public void should_send_message_over_activemq() throws Exception {
        template = new DefaultProducerTemplate(context);
        template.start();

        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        for (int i = 0; i < 5 ; i++) {
            final int counter = i;
            callables.add(new Callable<String>() {
                @Override public String call() throws Exception {
                    Exchange exchange = new DefaultExchange(context);
                    exchange.setProperty(LegacyJmsBinding.CONVERSATION_ID,"123456789");
                    exchange.getIn().setHeader("counter","Hello World " + counter);
                    exchange.getIn().setBody("hello world !");
                    Exchange result = template.send("direct:start", exchange);
                    return result.getIn().getHeader("counter", String.class);
                }
            });

        }

        ExecutorService executorService = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));

        List<Future<String>> futures = executorService.invokeAll(callables);

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        Thread.sleep(8000 * callables.size());
    }

    @Configuration
    public static class Config {

        @Bean
        public CamelContext context(RouteBuilder directToActiveMQ,
                RouteBuilder activeMQToCode) throws Exception {
            CamelContext context = new SpringCamelContext();
            //context.setTracing(true);
            context.setDelayer(0L);
            context.addRoutes(directToActiveMQ);
            context.addRoutes(activeMQToCode);
            return context;
        }
    }
}
