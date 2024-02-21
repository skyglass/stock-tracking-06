package net.greeta.stock.shipping;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ShippingJdbcConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.shipping")
    public DataSourceProperties shippingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.shipping.hikari")
    public DataSource shippingDataSource() {
        return shippingDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate shippingJdbcTemplate(@Qualifier("shippingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
