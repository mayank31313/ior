package ai.mayank.iot;

import java.net.UnknownHostException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.control.ZookeeperExecutor;


@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@ComponentScan
@SpringBootApplication
@Component
public class Application {
	@Autowired
	ZookeeperExecutor executor;

	
	static Logger logger = LoggerFactory.getLogger(Application.class);
	
	@PostConstruct
	public void init() {
		try {
			executor.registerServer(Server.getHost());
			logger.info(String.join(",", executor.listChildren("server")));
		} catch (UnknownHostException | KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static Properties getProperties() {
		Properties properties = new Properties();
		properties.put("server.port", 8080);
		return properties;
	}
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.setDefaultProperties(getProperties());
		app.run(args);
		System.out.print("Server Started");
	}	
}