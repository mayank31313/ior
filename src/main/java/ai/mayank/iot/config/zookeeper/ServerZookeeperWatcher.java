package ai.mayank.iot.config.zookeeper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.mayank.iot.Sockets.IClientHandler;
import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.proxy.ProxyClient;

public class ServerZookeeperWatcher implements Watcher {
	Logger logger = LoggerFactory.getLogger(ServerZookeeperWatcher.class);
	
	ZookeeperExecutor executor;
	
	public void setExecutor(ZookeeperExecutor executor) {
		this.executor = executor;
	}
		
	@Override
	public void process(WatchedEvent event) {
		if(event.getPath() == null)
			return;			
		if(event.getPath().startsWith("/ior/server/")) {
			String serverPath = "/ior/server/";
			String server = event.getPath().substring(serverPath.length());
			if(event.getType() == EventType.NodeCreated || event.getType() == EventType.NodeDataChanged) {
				try {
					String data = this.executor.getLatestData("server/" + server);
					ZookeeperDataStore.serverBindings.put(server, data);
					
					logger.info("Node Data: " + data);
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(event.getType() == EventType.NodeDeleted) {
				ZookeeperDataStore.serverBindings.remove(server);			
				logger.info("Node Deleted: " + server);
			}
		}
		else if(event.getPath().startsWith("/ior/clients/")) {
			String clientPath = "/ior/clients/";
			String client = event.getPath().substring(clientPath.length());
			
			if(event.getType() == EventType.NodeDataChanged) {
				String token = client.split("--")[0];
				String code = client.split("--")[1];
				try {
					
					DevicoZookeeperInfo deviceInfo = DevicoZookeeperInfo.castString(executor.getLatestData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, token,Integer.valueOf(code))));
					String server = deviceInfo.podIp;					
					if(!server.equals(Server.getHost())) {
						ProxyClient proxy = new ProxyClient(server,8080);
						proxy.setToken(token);
						proxy.setCode(Integer.valueOf(code));
						LinkedHashMap<Integer, IClientHandler> handlers = Server.sockets.get(token);
				        logger.info(String.format("Setting porxy Client for %s token %s-%s",server,token,code));
						if(handlers == null)
				        	handlers = new LinkedHashMap<>();
				        if(deviceInfo.state)
				        	handlers.put(proxy.getCode(),proxy);
				        else
				        	handlers.remove(proxy.getCode());
				        
				        if(handlers.size() == 0) {
				        	Server.sockets.remove(token);
				        }
					}
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else if(event.getPath().startsWith("/ior/election")) {
			if(event.getType() == EventType.NodeCreated) {
				List<String> leader_data;
				try {
					leader_data = this.executor.listChildren("election");
					Collections.sort(leader_data);
					String leader = this.executor.getLatestData("election/" + leader_data.get(0));
					if(leader.equals(Server.getHost())) {						
						this.executor.createZNode("master/" + Server.getHost(), "",true);
					}
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		else if(event.getPath().startsWith("/ior/master")) {
			if(event.getType() == EventType.NodeDeleted) {
				executor.applyForLeader(Server.getHost());
			}
			else if(event.getType() == EventType.NodeCreated) {
				List<String> leader_data;
				try {
					leader_data = executor.listChildren("master");	
					String leader = leader_data.get(0);
					Server.setMasterAddress(leader);
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		logger.info("Event Path: " + event.getPath());
		logger.info("Event Type: " + event.getType().name());
		logger.info("State: " + event.getState().name());
		
		printUpdatedServerList();
	}
	
	public void printUpdatedServerList() {
		logger.info("Updated Servers");
		ZookeeperDataStore.serverBindings.forEach((x,y) -> logger.info(String.format("\t%s:%s", x,y)));
	}
}
