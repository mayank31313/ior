package ai.mayank.iot.Sockets.Devices;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.corundumstudio.socketio.SocketIOClient;

import ai.mayank.iot.Sockets.ClientHandler;
import ai.mayank.iot.config.SocketIO;
import ai.mayank.iot.utils.inter_exchange.SocketMessage;

public class TunnelDevice extends ClientHandler {
	public TunnelDeviceReceiver receiver;
	public TunnelDeviceTransmitter transmitter;
	public SocketIOClient socketClient =null;
	private static Logger log = Logger.getLogger(TunnelDevice.class.getName());
	final static List<String> ids = Arrays.asList("battery","aileron","elevator","throttle","rudder");
	
	public TunnelDevice(String token) {	
		super(token);
	}
	public TunnelDevice(String token, Integer code, List<Integer> tos) {
		super(token, code, tos);	
	}

	public void setSocketIO(SocketIOClient io) {
		this.socketClient = io;
		if(receiver != null)
			this.receiver.socketClient = io;
		if(transmitter != null)
			this.transmitter.socketClient = io;
	}
	public void startReceiverThread() {
		Thread receiverThread = new Thread(this.receiver);
		receiverThread.start();
	}
	@Override
	public void run() {		
		while(this.receiver == null || this.transmitter == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		in_stream = transmitter.in_stream;
		out_stream = receiver.out_stream;
		
		if(socketClient == null)
			socketClient = SocketIO.clientLists.get(getToken());
		
		reader  = receiver.reader;
		byte[] buffer = new byte[128];
		
		log.info("Starting Tunnel Threads");
		try {
			while(true) {				
				if(in_stream.available() > 0){		
					try {
		                int read = in_stream.read(buffer,0,Math.min(in_stream.available(),buffer.length));
		                out_stream.write(buffer,0, read);
		                out_stream.flush();
					}
					catch(SocketTimeoutException ex) {
						
					}
				}
				if(receiver.in_stream.available() > 0 && socketClient != null) {
					String read= reader.readLine();
					try {
						int value = Integer.parseInt(read);
						HashMap map = new HashMap();
						int index = value/1000;
						map.put("id",ids.get(index));
		    			map.put(SocketIO.STATUS, value % 1000);
						socketClient.sendEvent(SocketIO.STATUS, map);
					}
					catch(NumberFormatException ex) {
						
					}
				}
			}
		}
		catch(IOException ex) {
			log.info("Error Occured");
			try {
				transmitter.sendMessage(new SocketMessage("250"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				receiver.sendMessage(new SocketMessage("250"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
		}
		
		if(socketClient != null) {
			HashMap map = new HashMap();
			if(transmitter == null) {                		
	    		if(socketClient != null) {
	    			map.put("id","transmitter");
	    			map.put(SocketIO.STATUS, "Disconnected");
	    			socketClient.sendEvent(SocketIO.STATUS, map);
	    		}
	    	}
	    	else if(receiver ==null) {

	    		if(socketClient != null) {
	    			map.put("id","receiver");
	    			map.put(SocketIO.STATUS, "Disconnected");
	    			socketClient.sendEvent(SocketIO.STATUS, map);
	    		}
	    	}
		}
		
	}
	
}
