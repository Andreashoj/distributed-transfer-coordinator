package com.springtest.distributedtransfercoordinator.db.escrow;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.springtest.distributedtransfercoordinator.db.escrow",
        entityManagerFactoryRef = "escrowEntityManagerFactory",
        transactionManagerRef = "escrowTransactionManager"
)
public class EscrowDataSourceConfig {
    @Bean
    public DataSource escrowDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/escrow_db")
                .username("escrow_user")
                .password("escrow_password")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "escrowEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("escrowDataSource") DataSource dataSource,
            @Qualifier("escrowFlyway") Flyway flyway) {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.springtest.distributedtransfercoordinator.db.escrow.models");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Add these properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "escrowTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("escrowEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
