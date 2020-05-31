package ai.mayank.iot.Sockets.Devices;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import ai.mayank.iot.Sockets.ClientHandler;
import ai.mayank.iot.utils.inter_exchange.SocketMessage;

public final class ArduinoDevice extends ClientHandler{

	private static Logger log = Logger.getLogger(ArduinoDevice.class.getName());
	
	public ArduinoDevice(String token, Integer code, List<Integer> tos) {
		super(token, code, tos);
	}
	
	
	@Override
	public boolean sendMessage(SocketMessage message) throws IOException {
		if(!socket.isConnected())
			return false;
		writer.write(message.message);
		writer.newLine();
		if(message.syncData != null) {
			Set<String> set = message.syncData.keySet();
			for(String s : set) {
				writer.write(s + " " + String.valueOf(message.syncData.get(s)));
				writer.newLine();
			}
		}
			
		writer.flush();
		log.info("Sending Arduino Message");
		return confirmMessageDelivery();
	}
	@Override
	public SocketMessage readData() throws IOException {
		if(!socket.isConnected() || socket.getInputStream().available() == 0) {
            return null;
        }
		SocketMessage msg = new SocketMessage();
		msg.message = reader.readLine();
		String metaData = reader.readLine();
		msg.syncData = new HashMap<>();
		while(!metaData.equals("")) {
			String[] d = metaData.split(" ");
			if(d.length == 2)
				msg.syncData.put(d[0],d[1]);
			metaData = reader.readLine();			
		}
		log.info(msg.toString());
		return msg;
	}
	
}
