package ai.mayank.iot.Sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ai.mayank.iot.config.zookeeper.StringTemplatesFormats;
import ai.mayank.iot.control.NotInitializedException;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.proxy.ProxyClient;
import ai.mayank.iot.repos.SocketHandlerRepository;
import ai.mayank.iot.repos.SupportedDeviceRepository;
import ai.mayank.iot.tables.SupportedDevices;
import ai.mayank.iot.tables.SupportedDevices.DeviceType;
import io.kuzzle.sdk.Kuzzle;

@Component
public class Server implements Runnable{
    public static ConcurrentHashMap<String, LinkedHashMap<Integer,IClientHandler>> sockets = new ConcurrentHashMap();
    //static ConcurrentHashMap<String,IClientHandler> table = new ConcurrentHashMap<>();
    
    @Autowired
    private SupportedDeviceRepository suppService;    
    @Autowired
    private SocketHandlerRepository service;
    @Autowired
    private ZookeeperExecutor executor;
    @Autowired
    Environment env;
    
    @Autowired(required = false)
    private Kuzzle kuzzle;
    
    
    private static String leaderAddress;
    private static boolean isLeader = false;
    private ServerSocket serverSocket;
    private Thread server;
    private static String serverAddress;
    private Thread watcherThread;
    
    private static Logger log;
    boolean c;
    
    static {
    	log = Logger.getLogger(Server.class.getName());
		
		try {
			serverAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void setMasterAddress(String addr) {
    	leaderAddress = addr;
    	if(leaderAddress.equals(getHost())) {
    		isLeader = true;
    	}
    }
    
    public static boolean isMaster() {
    	return isLeader;
    }
    public String getMaster() {
    	return leaderAddress;
    }
    
    public static String getHost() {
    	return serverAddress;
    }
    
	@PostConstruct
	public void init() {
		log.info("Starting server...");		
		int port = 8000;
		try {
			if(env.containsProperty("IOR_TCP_PORT")) {
				port = Integer.valueOf(env.getProperty("IOR_TCP_PORT"));
				log.info(String.format("Configuring Enviroment Supplied TCP Port: %d",port));
			}else {
				log.info(String.format("Using default port settings: %d", port));
			}
			
			serverSocket = new ServerSocket(port);
			server = new Thread(this);
			server.start();
			log.info("Server Started...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<SupportedDevices> devices = new ArrayList<>();
		DeviceType[] types = DeviceType.values();
		for(DeviceType type : types)
			devices.add(new SupportedDevices(type));
		
		suppService.deleteAll();
		suppService.saveAll(devices);
		
		watcherThread = new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("Initializing Watcher Thread...");
				while(!watcherThread.isInterrupted()) {		
					Iterator<LinkedHashMap<Integer, IClientHandler>> handlers = sockets.values().iterator();
					ArrayList<ClientHandler> removeHandlers = new ArrayList<ClientHandler>();
					while(handlers.hasNext()) {
						Iterator<IClientHandler> handler = handlers.next().values().iterator();						
						while(handler.hasNext()) {
							IClientHandler client = handler.next();							
							if(client instanceof ProxyClient) {
								continue;
							}
							ClientHandler cliHandler = (ClientHandler)client;
							if(!cliHandler.alive()) {
								removeHandlers.add(cliHandler);
							}
							else if(cliHandler.getKuzzleInstance() == null) {
								cliHandler.setKuzzleInstance(kuzzle);
							}
						}
					}				
					for(ClientHandler handler : removeHandlers) {
						log.info(String.format("Removing Client: %s-%d", handler.getToken(),handler.getCode()));
						LinkedHashMap<Integer, IClientHandler> linMap = sockets.get(handler.getToken());
						linMap.remove(handler.getCode());
						if(linMap.size() == 0)
							sockets.remove(handler.getToken());
						handler.close();
						try {							
							DevicoZookeeperInfo info = DevicoZookeeperInfo.castString(executor.getLatestData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, handler.getToken(), handler.getCode())));
							info.state = false;
							executor.updateData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, handler.getToken(), handler.getCode()), info.toString());
						} catch (KeeperException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotInitializedException e) {
							log.info("Zookeeper Not Initialized, Skipping update");
						}
					}
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		},"watcherThread");
		watcherThread.start();
		
		
	}
	
	@PreDestroy
	public void destroySocket() {		
	    server.interrupt();
	    watcherThread.interrupt();
	    
	    Set<String> keys = sockets.keySet();
	    for(String key : keys){
	    	LinkedHashMap<Integer, IClientHandler> map = sockets.get(key);
	    	map.values().forEach(x->x.close());
	    }
	}
	
	/*
	public static void add(String uuid,IClientHandler handler) {
		synchronized (table) {
			table.put(uuid, handler);
		}
	}
	*/
	


    public void run() {
        while(true)
        {
            try {
                Socket socket = serverSocket.accept();
                ClientAuthorizer authorizer = new ClientAuthorizer(socket,service,executor);
                authorizer.start();                                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            if(Thread.interrupted()) {
            	try {
					serverSocket.close();
					log.info("Socket Server closed");
				} catch (IOException e) {
					e.printStackTrace();
				}
            	return;
            }
        }
    }
}
