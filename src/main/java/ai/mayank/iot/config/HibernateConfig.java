package ai.mayank.iot.config;



import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Component
@EnableTransactionManagement
public class HibernateConfig {
	Logger log = Logger.getLogger(HibernateConfig.class.getName());
	
	@Autowired
	Environment env;
	
	@Bean
	public DataSource dataSource() {
	       DriverManagerDataSource dataSource = new DriverManagerDataSource();
	       dataSource.setDriverClassName(env.getRequiredProperty("JDBC_DRIVER"));
	       dataSource.setUrl( env.getRequiredProperty("DATABASE_URL"));
	       dataSource.setUsername(env.getRequiredProperty("DATABASE_USER"));
	       dataSource.setPassword( env.getRequiredProperty("DATABASE_PASSWORD"));
	       Properties p = new Properties();
	       p.setProperty("spring.session.jdbc.initialize-schema","always");
	       dataSource.setConnectionProperties(p);
	       return dataSource;	       
	}
	
    @Bean
    public LocalSessionFactoryBean getSessionFactory() {    	
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean(); 
        factoryBean.setDataSource(dataSource());
        Properties p = new Properties();

        p.setProperty("hibernate.connection.driver_class", env.getRequiredProperty("JDBC_DRIVER"));
        p.setProperty("hibernate.c3p0.timeout", "1800");
        p.setProperty("hibernate.c3p0.max_statements", "150");
        p.setProperty("hibernate.c3p0.max_size", "5");
        p.setProperty("hibernate.show_sql","true");
        p.setProperty("hibernate.hbm2ddl.auto", "update");
        p.setProperty("hibernate.c3p0.acquire_increment", "1");
        p.setProperty("hibernate.connection.password", env.getRequiredProperty("DATABASE_PASSWORD"));
        p.setProperty("hibernate.connection.username", env.getRequiredProperty("DATABASE_USER"));
        p.setProperty("hibernate.connection.url", env.getRequiredProperty("DATABASE_URL"));
        
        factoryBean.setHibernateProperties(p);
        factoryBean.setPackagesToScan("ai.mayank.iot.tables");
        return factoryBean;
    }
 
    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        LocalSessionFactoryBean sessionBean = getSessionFactory();
        transactionManager.setSessionFactory(sessionBean.getObject());
        return transactionManager;
    }
    
}
