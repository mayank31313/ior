package ai.mayank.iot.Sockets.Devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ai.mayank.iot.utils.inter_exchange.SocketMessage;

public class TunnelDeviceReceiver extends TunnelDevice {

	static Logger log = Logger.getLogger(TunnelDeviceReceiver.class.getName());
	
	public TunnelDeviceReceiver(String token, Integer code, List<Integer> tos) {
		super(token, code, tos);
		// TODO Auto-generated constructor stub
	}
	
	public void run() {
		log.info("Running Receiver Thread");
		while(true) {
			SocketMessage message;
			try {
				message = readData();
				if(message == null)
					continue;
				if(socketClient != null) {
					log.info("Not Null");
					socketClient.sendEvent("receiver", message);
				}
				else {
					log.info("Not Null");
				}
				log.info(message.toString());
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
