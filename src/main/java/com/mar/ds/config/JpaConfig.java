package com.mar.ds.config;

import com.mar.ds.db.entity.Card;
import com.mar.ds.db.jpa.CardRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteDataSource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URI;

@Configuration
@EntityScan(basePackageClasses = {Card.class})
@EnableJpaRepositories(
        basePackageClasses = {CardRepository.class},
        transactionManagerRef = "jpaTransactionManager",
        entityManagerFactoryRef = "localContainerEntityManagerFactoryBean"
)
@EnableTransactionManagement
public class JpaConfig {

    private Logger logger = LoggerFactory.getLogger(JpaConfig.class);

    @Value("${data.path:./}")
    private String dataPath;

    @Value("${db.file}")
    private String dbFile;

    @Autowired
    @Bean
    public JpaTransactionManager jpaTransactionManager(
            @Qualifier(value = "EmbeddedDataSource") DataSource dataSource,
            EntityManagerFactory entityManagerFactory
    ) {
        JpaTransactionManager jpaTransactionManager
                = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        jpaTransactionManager.setDataSource(dataSource);

        return jpaTransactionManager;
    }

    @Autowired
    @Bean
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(
            @Qualifier(value = "EmbeddedDataSource") DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter
    ) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
                = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        // Проверьте модель для автоматического создания таблиц
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.mar.ds.db.entity");
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setDatabasePlatform("com.enigmabridge.hibernate.dialect.SQLiteDialect");
        return hibernateJpaVendorAdapter;
    }

    @Bean(destroyMethod = "", name = "EmbeddedDataSource")
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
//        String dbPath1 = "src/main/resources/static/db/dark_sun1.db";

        String dbAbsPath = getDbPath().getAbsolutePath();
        logger.debug("Data path: {}, DB path: {}", dataPath, dbAbsPath);
        dataSourceBuilder.url("jdbc:sqlite:" + dbAbsPath);
        dataSourceBuilder.type(SQLiteDataSource.class);
        return dataSourceBuilder.build();
    }

    private File getDbPath() {
        File dataDir = new File(dataPath + dbFile);
        if (!dataDir.exists()) {
            try {
                FileUtils.createParentDirectories(dataDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dataDir;
    }
}