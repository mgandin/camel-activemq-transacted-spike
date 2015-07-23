package fr.mga.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.region.policy.IndividualDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.StoreUsage;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.usage.TempUsage;
import org.apache.camel.component.jms.JmsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mathieu.gandin
 */
@Configuration
public class ActiveMQConfig {

    @Bean
    public BrokerService broker() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("test");
        brokerService.setUseJmx(true);
        brokerService.setPersistent(false);

        PolicyMap policyMap = new PolicyMap();

        List<PolicyEntry> policyEntries = new ArrayList<PolicyEntry>();
        PolicyEntry policyEntry = new PolicyEntry();
        policyEntry.setQueue(">");
        policyEntry.setProducerFlowControl(true);
        policyEntry.setMemoryLimit(20000);
        policyEntry.setConsumersBeforeDispatchStarts(2);

        IndividualDeadLetterStrategy deadLetterStrategy = new IndividualDeadLetterStrategy();
        deadLetterStrategy.setQueuePrefix("DLQ.");
        deadLetterStrategy.setUseQueueForQueueMessages(true);
        policyEntry.setDeadLetterStrategy(deadLetterStrategy);

        policyEntries.add(policyEntry);
        policyMap.setPolicyEntries(policyEntries);
        brokerService.setDestinationPolicy(policyMap);

        ManagementContext managementContext = new ManagementContext();
        managementContext.setCreateConnector(false);
        brokerService.setManagementContext(managementContext);

        SystemUsage systemUsage = new SystemUsage();
        MemoryUsage memoryUsage = new MemoryUsage();
        memoryUsage.setLimit(20000);
        systemUsage.setMemoryUsage(memoryUsage);

        StoreUsage storeUsage = new StoreUsage();
        storeUsage.setLimit(1);
        systemUsage.setStoreUsage(storeUsage);

        TempUsage tempUsage = new TempUsage();
        tempUsage.setLimit(1);
        systemUsage.setTempUsage(tempUsage);
        brokerService.setSystemUsage(systemUsage);
        TransportConnector transportConnector = new TransportConnector();
        transportConnector.setName("openwire");
        transportConnector.setUri(new URI("tcp://localhost:0"));
        brokerService.addConnector(transportConnector);
        return brokerService;
    }

    @Bean
    @DependsOn("broker")
    public ActiveMQXAConnectionFactory jmsConnectionFactory() {
        return new ActiveMQXAConnectionFactory("vm://test?jms.redeliveryPolicy.maximumRedeliveries=0");
    }

//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public PooledConnectionFactory pooledConnectionFactory(ActiveMQXAConnectionFactory jmsConnectionFactory) {
//        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
//        pooledConnectionFactory.setMaxConnections(80);
//        pooledConnectionFactory.setConnectionFactory(jmsConnectionFactory);
//        return pooledConnectionFactory;
//    }

    @Bean
    public JmsConfiguration jmsConfig(ActiveMQXAConnectionFactory jmsConnectionFactory, JtaTransactionManager jtaTransactionManager) {
        JmsConfiguration jmsConfig = new JmsConfiguration();
        jmsConfig.setConnectionFactory(jmsConnectionFactory);
        jmsConfig.setConcurrentConsumers(5);
        jmsConfig.setTransacted(true);
        jmsConfig.setTransactionManager(jtaTransactionManager);
        return jmsConfig;
    }

}
