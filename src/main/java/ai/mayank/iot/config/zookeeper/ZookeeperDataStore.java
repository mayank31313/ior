package ai.mayank.iot.config.zookeeper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.control.NotInitializedException;
import ai.mayank.iot.control.ZookeeperExecutor;

@Configuration
public class ZookeeperDataStore {
	public final static HashMap<String,String> serverBindings = new HashMap<String, String>();
	Logger log = LoggerFactory.getLogger(ZookeeperDataStore.class);
	
	@Autowired
	Environment env;
	
	@Value(value = "${zookeeper.enable}")
	boolean isEnabled;
	
	@Bean
	public ZookeeperExecutor getZookeeperExecutor() {
		ZookeeperExecutor executor = new ZookeeperExecutor(isEnabled);
		if(isEnabled) {
			try {
				ServerZookeeperWatcher watcher = new ServerZookeeperWatcher();			
				executor.setKeeper(env.getRequiredProperty("ZOOKEEPER_SERVERS"),watcher);
				ZooKeeper keeper = executor.getKeeper();
				keeper.addWatch("/ior/master", AddWatchMode.PERSISTENT_RECURSIVE);
				keeper.addWatch("/ior/election", AddWatchMode.PERSISTENT_RECURSIVE);
				
				keeper.addWatch("/ior/server", AddWatchMode.PERSISTENT_RECURSIVE);
				keeper.addWatch("/ior/clients" ,AddWatchMode.PERSISTENT_RECURSIVE);
				
				watcher.setExecutor(executor);
				
				List<String> servers = executor.listChildren("server");
				for(String  serverIp : servers) {
					String serverName = executor.getLatestData("server/" + serverIp);
					serverBindings.put(serverIp, serverName);
				}
				
				List<String> leader_data = executor.listChildren("master");
				if(leader_data.size() > 0) {
					String leader = leader_data.get(0);
					Server.setMasterAddress(leader);
				}
				else {
					executor.applyForLeader(Server.getHost());
				}
			}
			catch(NotInitializedException ex) {
				log.warn("Zookeeper is disabled, we won't be able to connect in cluster");
			}
			catch (KeeperException | InterruptedException | IOException | ConfigException e) {
				e.printStackTrace();
			}		
		}
		return executor;
	}
}
