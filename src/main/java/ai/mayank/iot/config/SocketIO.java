package ai.mayank.iot.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import ai.mayank.iot.SocketIOClient;
import ai.mayank.iot.SocketIOServer;
import ai.mayank.iot.Sockets.IClientHandler;
import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.Sockets.Devices.TunnelDevice;
import ai.mayank.iot.utils.inter_exchange.SocketMessage;


@Component
public class SocketIO {
	public static final String STATUS = "status";
	public static final ConcurrentHashMap<String, SocketIOClient> clientLists = new ConcurrentHashMap<String, SocketIOClient>();
	private Logger log = Logger.getLogger(SocketIO.class.getName());
	SocketIOServer server;
	
	@PostConstruct
	public void init() {
		
		log.info("Starting SocketIO server...");
		
		//Configuration config = new Configuration();
        //config.setHostname("0.0.0.0");
        //config.setPort(9095);

        server = new SocketIOServer();
        /*
        server.addEventListener("init", String.class, new DataListener<String>() {
			@Override
			public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
				clientLists.put(data, client);		
				LinkedHashMap<Integer, IClientHandler> handler = Server.sockets.get(data);
				if(handler != null) {
					if(handler.containsKey(4444)) {
						TunnelDevice device = (TunnelDevice)handler.get(4444);
						device.setSocketIO(client);
						if(device.receiver != null) {
							HashMap map = new HashMap();
                			map.put("id","receiver");
                			map.put(STATUS, "Connected");
                			client.sendEvent(STATUS, map);
						}
						if(device.transmitter != null) {
							HashMap map = new HashMap();
							map.put("id","transmitter");
                			map.put(STATUS, "Connected");
                			client.sendEvent(STATUS, map);
						}
					}
				}
				log.info("Client Connected " + data );
			}
		});
        
        server.addEventListener("on_receive", SocketMessage.class,new DataListener<SocketMessage>() {
			@Override
			public void onData(SocketIOClient client, SocketMessage data, AckRequest ackSender) throws Exception {
				String token = data.message;
				LinkedHashMap<Integer,IClientHandler> handler = Server.sockets.get(token);
				if(handler != null) {
					if(handler.containsKey(4444)) {
						TunnelDevice device = (TunnelDevice)handler.get(4444);
						if(device.receiver != null) {
							device.sendMessage(data);
							log.info("Message Sended");
						}
						else
							log.info("Message not sended to client");
					}
					else
						log.info("Key Not Found");
				}
				log.info(data.toString());			
			}
		});
		*/
        //server.start();        
	}
	
	@PreDestroy
	public void destroy() {
		server.stop();
	}
}