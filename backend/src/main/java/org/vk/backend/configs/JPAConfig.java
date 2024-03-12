package org.vk.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableJpaRepositories("org.vk")
@EnableTransactionManagement
public class JPAConfig {
    @Bean
    public DataSource getSource() {
        final Properties info = new Properties();

        try {
            info.load(this.getClass().getResourceAsStream("/db.cfg"));
        } catch (IOException ignored) {
            System.out.println("DataBase configure file error!");
        }

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(info.getProperty("driver"));
        dataSource.setUrl(info.getProperty("url"));
        dataSource.setUsername(info.getProperty("user"));
        dataSource.setPassword(info.getProperty("password"));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(getSource());
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setPackagesToScan("org.vk");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final var object = entityManagerFactory().getObject();
        assert object != null;
        return new JpaTransactionManager(object);
    }
}
