package com.springtest.distributedtransfercoordinator.db.eventstore;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.springtest.distributedtransfercoordinator.db.eventstore",
        entityManagerFactoryRef = "eventstoreEntityManagerFactory",
        transactionManagerRef = "eventstoreTransactionManager"
)
public class EventStoreDataSourceConfig {
    @Value("${spring.datasource.eventstore.url}")
    private String url;

    @Value("${spring.datasource.eventstore.username}")
    private String username;

    @Value("${spring.datasource.eventstore.password}")
    private String password;

    @Value("${spring.datasource.eventstore.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource eventstoreDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean(name = "eventstoreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("eventstoreDataSource") DataSource dataSource,
            @Qualifier("eventstoreFlyway") Flyway flyway
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.springtest.distributedtransfercoordinator.db.eventstore.models");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return em;
    }

    @Bean(name = "eventstoreTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("eventstoreEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
