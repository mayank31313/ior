package ai.mayank.iot;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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

import ai.mayank.iot.Sockets.ClientHandler;
import ai.mayank.iot.Sockets.IClientHandler;
import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.config.zookeeper.StringTemplatesFormats;
import ai.mayank.iot.control.NotInitializedException;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.proxy.ProxyClient;


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
		} catch (NotInitializedException e) {
			logger.info("Zookeeper Not Initialized");
		}
	}
	
	@PreDestroy
	public void destroy() {
		Collection<LinkedHashMap<Integer, IClientHandler>> values = Server.sockets.values();
		for(LinkedHashMap<Integer, IClientHandler> value : values) {
			Collection<IClientHandler> handlers = value.values();
			for(IClientHandler handler : handlers) {
				if(handler instanceof ProxyClient) {
					continue;
				}
				
				ClientHandler h = (ClientHandler)handler;
				h.close();
				
				try {
					String data = executor.getLatestData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, h.getToken(),h.getCode()));
					DevicoZookeeperInfo info = DevicoZookeeperInfo.castString(data);
					info.state = false;
					executor.updateData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, h.getToken(),h.getCode()), info.toString());
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				} catch (NotInitializedException e) {
					logger.info("Zookeeper Not Initialized");
				}
				
			}
		}
	}
	
	private static Properties getProperties() {
		Properties properties = new Properties();
		properties.put("server.port", 5001);
		
		properties.put("kuzzle.enable", false);
		properties.put("kafka.enable", false);
		properties.put("zookeeper.enable", false);
		properties.put("websocket.enable", false);
		
		return properties;
	}
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		//app.setDefaultProperties(getProperties());
		app.run(args);
		System.out.print("Server Started");
	}	
}