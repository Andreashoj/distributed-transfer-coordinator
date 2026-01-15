package com.springtest.distributedtransfercoordinator.db;

import com.springtest.distributedtransfercoordinator.db.escrow.EscrowDataSourceConfig;
import com.springtest.distributedtransfercoordinator.db.eventstore.EventStoreDataSourceConfig;
import com.springtest.distributedtransfercoordinator.db.seller.SellerDataSourceConfig;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@Import({EscrowDataSourceConfig.class, SellerDataSourceConfig.class, EventStoreDataSourceConfig.class})
public class FlywayConfig {
    @Bean
    public Flyway escrowFlyway(@Qualifier("escrowDataSource") DataSource dataSource) {
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/escrow")
                .baselineOnMigrate(true)
                .load();
        var result = flyway.migrate();
        return flyway;
    }

    @Bean
    public Flyway sellerFlyway(@Qualifier("sellerDataSource") DataSource dataSource) {
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/seller")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    @Bean
    public Flyway eventstoreFlyway(@Qualifier("eventstoreDataSource") DataSource dataSource) {
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/eventstore")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}
