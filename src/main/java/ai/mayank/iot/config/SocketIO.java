package ai.mayank.iot.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;

import ai.mayank.iot.utils.inter_exchange.SocketMessage;


@Component
public class SocketIO {
	public final static ConcurrentHashMap<String, SocketIOClient> clientLists = new ConcurrentHashMap<String, SocketIOClient>();
	public static final String STATUS = "status";
	private Logger log = Logger.getLogger(SocketIO.class.getName());
	SocketIOServer server;
	@PostConstruct
	public void init() {
		log.info("Starting SocketIO server...");
		Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9000);

        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
			
			@Override
			public void onConnect(SocketIOClient client) {
				clientLists.put(client.getSessionId().toString(), client);
				log.info("Client Connected " + client.getSessionId().toString());
			}
		});
        
        server.addEventListener("chatevent", SocketMessage.class, new DataListener<SocketMessage>() {
            @Override
            public void onData(SocketIOClient client, SocketMessage data, AckRequest ackRequest) {
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });

        //server.start();
	}
	
	@PreDestroy
	public void destroy() {
		//server.stop();
	}
}
