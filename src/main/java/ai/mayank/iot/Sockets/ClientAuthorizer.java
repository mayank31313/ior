package ai.mayank.iot.Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.corundumstudio.socketio.SocketIOClient;

import ai.mayank.iot.Sockets.Devices.ArduinoDevice;
import ai.mayank.iot.Sockets.Devices.TunnelDevice;
import ai.mayank.iot.Sockets.Devices.TunnelDeviceReceiver;
import ai.mayank.iot.Sockets.Devices.TunnelDeviceTransmitter;
import ai.mayank.iot.config.SocketIO;
import ai.mayank.iot.config.zookeeper.StringTemplatesFormats;
import ai.mayank.iot.control.NotInitializedException;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.proxy.ProxyClient;
import ai.mayank.iot.repos.SocketHandlerRepository;
import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SocketVariables;
import ai.mayank.iot.utils.inter_exchange.SocketMessage;

public class ClientAuthorizer extends Thread{
	Socket socket;
	SocketHandlerRepository service;
	Logger log = LoggerFactory.getLogger(ClientAuthorizer.class);
	Environment env;
	ZookeeperExecutor executor;
	
	public ClientAuthorizer(Socket socket,SocketHandlerRepository service,ZookeeperExecutor executor) {
		this.socket = socket;
		this.service = service;
		this.executor = executor;
	}
	
	public void run() {
		SocketVariables variables = null;
		try {
			BufferedReader buffStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String uuid = buffStream.readLine();
	        log.info("Request for Device: " + uuid);
	        variables = service.findByTempId(uuid).get();
	        if(variables == null) {
	        	System.out.println("Terminating Connection: " + socket.getRemoteSocketAddress());
				socket.getOutputStream().write("Unauthorised Connection".getBytes());
				socket.close();
				return;
	        }
		}
        catch(IOException ex) {
        	ex.printStackTrace();
        	return;
        }

		IClientHandler client;
		Device device = variables.getDevice();
		int deviceId = device.getDeviceType().getDevice_id();
		LinkedHashMap<Integer, IClientHandler> handlers = Server.sockets.get(variables.getToken());
        
		if(handlers == null)
        	handlers = new LinkedHashMap<>();
        
        if(deviceId == 1)
        	client = new ArduinoDevice(variables.getToken(),variables.getDeviceId(),Arrays.asList(variables.getTo()));
        else if(deviceId == 5 || deviceId == 6) {
        	TunnelDevice tunnel = (TunnelDevice)handlers.get(variables.getTo());
        	if(tunnel == null)
        		tunnel = new TunnelDevice(variables.getToken());
        	if(deviceId == 5) {
    			tunnel.receiver = new TunnelDeviceReceiver(variables.getTempId(), variables.getDeviceId(), Arrays.asList(variables.getTo()));
        	}
    		if(deviceId == 6)
    			tunnel.transmitter = new TunnelDeviceTransmitter(variables.getTempId(), variables.getDeviceId(), Arrays.asList(variables.getTo()));    		
    		client = tunnel;
        }
        else
        	client = new ClientHandler(variables.getToken(), variables.getDeviceId(), Arrays.asList(variables.getTo()));
        
                                 
        //Server.table.remove(uuid);
        client.setSocket(socket);
        
        try {
        	DevicoZookeeperInfo devInfo = new DevicoZookeeperInfo(Server.getHost(), true);
			executor.updateData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE,variables.getToken(),variables.getDeviceId()), devInfo.toString());
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotInitializedException e) {
			log.info("Zookeeper Not Initialized, skipping Update");
		}
        
        boolean isTunnel = client instanceof TunnelDevice;
        
        if(isTunnel) {
        	TunnelDevice tdevice = (TunnelDevice)client;                	
        	handlers.put(4444, tdevice);
        	
        	SocketIOClient socketClient = SocketIO.clientLists.get(tdevice.getToken());
        	HashMap<String,String> map = new HashMap<String,String>();
        	try {
	        	if(tdevice.transmitter != null && tdevice.transmitter.getToken().equals(variables.getTempId())) {                		
	        		tdevice.transmitter.setSocket(socket);
	        		if(socketClient != null) {
	        			map.put("id","transmitter");
	        			map.put(SocketIO.STATUS, "Connected");
	        			socketClient.sendEvent(SocketIO.STATUS, map);
	        		}
	        		if(tdevice.receiver != null) {
	        			tdevice.receiver.sendMessage(new SocketMessage("102"));
	        			tdevice.transmitter.sendMessage(new SocketMessage("100"));
	        		}
	        	}
	        	else if(tdevice.receiver!=null && tdevice.receiver.getToken().equals(variables.getTempId())) {
	        		tdevice.receiver.setSocket(socket);
	        		tdevice.receiver.socketClient = socketClient;
	        		tdevice.startReceiverThread();
	        		if(socketClient != null) {
	        			map.put("id","receiver");
	        			map.put(SocketIO.STATUS, "Connected");
	        			socketClient.sendEvent(SocketIO.STATUS, map);
	        		}
	        		if(tdevice.transmitter != null) {
	        			tdevice.receiver.sendMessage(new SocketMessage("102"));
	        			tdevice.transmitter.sendMessage(new SocketMessage("100"));
	        		}
	        	}
	        	if(tdevice.receiver != null && tdevice.transmitter != null) {
	        		Thread clientHandlerThread = new Thread(tdevice);
	        		clientHandlerThread.start();
	        		log.info("Executing Tunnel Thread");                		
	        	}
        	} catch(IOException ex) {ex.printStackTrace();}	
        }
        else {
        	handlers.put(client.getCode(),client );
        	if(!(client instanceof ProxyClient)) {	        	 
	            ((ClientHandler)client).start();
        	}
            log.info("Executing Thread");
        }
        
        Server.sockets.put(variables.getToken(),handlers);
        log.info(String.format("Device Registered: %s--%d",client.getToken(),client.getCode()));
	}
}
