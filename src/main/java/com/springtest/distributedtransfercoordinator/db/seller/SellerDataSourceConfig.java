package com.springtest.distributedtransfercoordinator.db.seller;

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
        basePackages = "com.springtest.distributedtransfercoordinator.db.seller",
        entityManagerFactoryRef = "sellerEntityManagerFactory",
        transactionManagerRef = "sellerTransactionManager"
)
public class SellerDataSourceConfig {
    @Bean
    public DataSource sellerDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5433/seller_db")
                .username("seller_user")
                .password("seller_password")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "sellerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("sellerDataSource") DataSource dataSource,
            @Qualifier("sellerFlyway") Flyway flyway
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.springtest.distributedtransfercoordinator.db.seller.models");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Add these properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "sellerTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("sellerEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
