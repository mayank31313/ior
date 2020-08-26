package ai.mayank.iot.config.zookeeper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.control.ZookeeperExecutor;

@Configuration
public class ZookeeperDataStore {
	public final static HashMap<String,String> serverBindings = new HashMap<String, String>();
	
	@Autowired
	Environment env;
	
	@Bean
	public ZookeeperExecutor getZookeeperExecutor() {
		ZookeeperExecutor executor = new ZookeeperExecutor();
		try {
			ServerZookeeperWatcher watcher = new ServerZookeeperWatcher();			
			executor.setKeeper(env.getRequiredProperty("ZOOKEEPER_SERVERS"),watcher);
			
			executor.keeper.addWatch("/ior/master", AddWatchMode.PERSISTENT_RECURSIVE);
			executor.keeper.addWatch("/ior/election", AddWatchMode.PERSISTENT_RECURSIVE);
			
			executor.keeper.addWatch("/ior/server", AddWatchMode.PERSISTENT_RECURSIVE);
			executor.keeper.addWatch("/ior/clients" ,AddWatchMode.PERSISTENT_RECURSIVE);
			
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
		} catch (KeeperException | InterruptedException | IOException | ConfigException e) {
			e.printStackTrace();
		}		
		return executor;
	}
}
