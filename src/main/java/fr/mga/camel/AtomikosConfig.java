package fr.mga.camel;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.pool.ActiveMQResourceManager;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

/**
 * @author mathieu.gandin
 */
@Configuration
public class AtomikosConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosConnectionFactoryBean pooledConnectionFactory(ActiveMQXAConnectionFactory jmsConnectionFactory) {
        AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
        atomikosConnectionFactoryBean.setPoolSize(15);
        atomikosConnectionFactoryBean.setUniqueResourceName("activemq");
        atomikosConnectionFactoryBean.setXaConnectionFactory(jmsConnectionFactory);
        return atomikosConnectionFactoryBean;
    }

    @Bean
    @DependsOn("pooledConnectionFactory")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public UserTransactionImp atomikosUserTransaction() throws SystemException {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(120);
        return userTransactionImp;
    }

    @Bean
    @Qualifier("jms")
    public JtaTransactionManager jtaTransactionManager(UserTransactionManager atomikosTransactionManager,
            UserTransactionImp atomikosUserTransaction) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(atomikosTransactionManager);
        jtaTransactionManager.setUserTransaction(atomikosUserTransaction);
        return jtaTransactionManager;
    }

    @Bean(initMethod = "recoverResource")
    public ActiveMQResourceManager resourceManager(AtomikosConnectionFactoryBean pooledConnectionFactory,
            UserTransactionManager atomikosTransactionManager) {
        ActiveMQResourceManager activeMQResourceManager = new ActiveMQResourceManager();
        activeMQResourceManager.setConnectionFactory(pooledConnectionFactory);
        activeMQResourceManager.setTransactionManager(atomikosTransactionManager);
        activeMQResourceManager.setResourceName("activemq.default");
        return activeMQResourceManager;
    }

    @Bean
    public SpringTransactionPolicy PROPAGATION_REQUIRED(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy = new SpringTransactionPolicy();
        springTransactionPolicy.setTransactionManager(jtaTransactionManager);
        springTransactionPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return springTransactionPolicy;
    }
}
