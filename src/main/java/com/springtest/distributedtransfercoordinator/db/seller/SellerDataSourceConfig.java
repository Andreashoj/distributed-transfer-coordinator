package com.springtest.distributedtransfercoordinator.db.seller;

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
        basePackages = "com.springtest.distributedtransfercoordinator.db.seller",
        entityManagerFactoryRef = "sellerEntityManagerFactory",
        transactionManagerRef = "sellerTransactionManager"
)
public class SellerDataSourceConfig {
    @Value("${spring.datasource.seller.url}")
    private String url;

    @Value("${spring.datasource.seller.username}")
    private String username;

    @Value("${spring.datasource.seller.password}")
    private String password;

    @Value("${spring.datasource.seller.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource sellerDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
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

        return em;
    }

    @Bean(name = "sellerTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("sellerEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
