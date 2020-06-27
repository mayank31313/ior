package ai.mayank.iot.Sockets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.mayank.iot.proxy.ProxyClient;
import ai.mayank.iot.service.DeviceService;
import ai.mayank.iot.service.SyncService;
import ai.mayank.iot.tables.DeviceSync;
import ai.mayank.iot.utils.inter_exchange.SocketMessage;
import ai.mayank.iot.utils.inter_exchange.Status;
import io.kuzzle.sdk.Kuzzle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ClientHandler extends Thread implements IClientHandler{ 
    protected DeviceService service;
    protected SyncService sync;
    
    protected Socket socket;
    protected InputStream in_stream;
    protected OutputStream out_stream;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected boolean is_alive = false;
    protected String token;
    protected Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    protected Integer code;
    protected List<Integer> tos;
    protected Long start;
    protected Queue<SocketMessage> messages = new ConcurrentLinkedQueue<>();
    protected Kuzzle kuzzle = null;
    
    private Logger log = Logger.getLogger(ClientHandler.class.getName());   
    
    public ClientHandler(String token) {
    	this.token = token;
    }
    public ClientHandler(String token,Integer code,List<Integer> tos) {
    	this.token = token;
    	this.code = code;
    	this.tos = tos;
    }
    
    public void setSocket(final Socket s) {
        socket = s;      
        try {
        	start = System.currentTimeMillis();
        	in_stream = socket.getInputStream();
        	out_stream = socket.getOutputStream();
        	
			reader = new BufferedReader(new InputStreamReader(in_stream));
	        writer = new BufferedWriter(new OutputStreamWriter(out_stream));
		} catch (IOException e) {
			e.printStackTrace();
			this.close();
		}
    }
    
    public Kuzzle getKuzzleInstance() {
    	return this.kuzzle;
    }
    public void setKuzzleInstance(Kuzzle kuzzle) {
    	this.kuzzle = kuzzle;
    }
    
    public void close() {
        try {	        	
			socket.close();	
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}        
    }
    
    
	public Integer getCode() {
    	return code;
    }
	
    public boolean isConnected(){
        return (socket.isConnected() && !socket.isClosed());
    }
    public boolean sendMessage(SocketMessage msg) throws IOException {
    	return sendMessage(msg,true);
    }
    public boolean sendMessage(SocketMessage msg,boolean confirm_delivery) throws IOException {
		String data = gson.toJson(msg);
		log.info(String.format("Sending Message :%s",data));
        writer.write(data);      
        writer.newLine();
        writer.flush();       
    	 
    	if(confirm_delivery) {
    		return this.confirmMessageDelivery();
    	}else {
    		return true;
    	}
    }
    
    public boolean alive() {
    	return is_alive;
    }
    
    public void run() {
        log.info("Starting Sockets " + socket.getRemoteSocketAddress());
        start = System.currentTimeMillis();
        try {
        	is_alive = true;
	        while(!this.isInterrupted()){	        	
	            try {
	            	SocketMessage msg = null;
	            	if(this.messages.size() > 0) {
	            		msg = this.messages.peek();
	            		if(this.sendMessage(msg))
	            			this.messages.remove();
	            	}
	                msg = this.readData();                
	                if(msg == null) {
	                	if(System.currentTimeMillis() - start > timeout) {
	                		this.interrupt();
	                		break;
	                	}
	                }
	                else{
	                	start = System.currentTimeMillis();
	                	if(msg.message != null)
		                    if(msg.message.equals("<HEARTBEAT>")) {
		                    	continue;
		                    }
		                    else if(msg.message.equals("ack"))
		                    	continue;
	                    if(msg.status == Status.SYNC) {
	                    	if(kuzzle != null && msg.syncData != null) {
	                    		kuzzle.getRealtimeController().publish("ior", "drone-data", new ConcurrentHashMap<String, Object>(msg.syncData));
	                    	}
	                    	else {
	                    		log.warning("Kuzzle is NULL");
	                    	}
	                    	continue;
	                    }
	                    
	                    LinkedHashMap<Integer, IClientHandler> handlers = Server.sockets.get(this.token);
	                    if(handlers == null) 
	                    	continue;	
	                    for(Integer key : tos) {
	                        if (handlers.containsKey(key)) {
	                            IClientHandler s = handlers.get(key);
	                            s.addMessage(msg);
	                        } else {
	                        	SocketMessage m = new SocketMessage();
	                        	m.message = "Client NOT Found with Id" + key;
	                        	m.status = Status.ERROR;
	                            this.sendMessage(m);
	                        }
	                    }
	                }
	            }
	            catch(IOException ex) {
	            	ex.printStackTrace();
	            	this.interrupt();
	            	break;
	            }          
	        }
        }
        catch (Exception e) {
        	if(!(e instanceof InterruptedException))
        		e.printStackTrace();      		
		}        
        is_alive = false;
    }    
    
    public SocketMessage readData() throws IOException{
        if(!this.isConnected() || socket.getInputStream().available() == 0) {
            return null;
        }
        
    	String dataString = reader.readLine();
        if(dataString.equals("")) {
        	return new SocketMessage("<HEARTBEAT>");
        }
        SocketMessage msg = gson.fromJson(dataString,SocketMessage.class);
        return msg;
    }

	@Override
	public void setTos(List<Integer> ts) {
		this.tos = ts;
		this.tos.remove(new Integer(0));
	}
	
	public void setService(DeviceService service) {
		this.service = service;
	}

	@Override
	public String getToken() {
		return this.token;
	}

	@Override
	public boolean confirmMessageDelivery() throws IOException{
		long start = System.currentTimeMillis();		
	    while(in_stream.available() == 0) {
	        	if(System.currentTimeMillis()- start > hold_delay)
	        		return false;
	    }
	    SocketMessage msg = readData();
	    if(msg.message.equals("ack"))
	        return true;
		return false;
	}

	@Override
	public Queue<SocketMessage> getMessages() {
		return this.messages;
	}

	@Override
	public void addMessage(SocketMessage msg) {
		synchronized (this.messages) {
			this.messages.add(msg);	
		}		
	}

	@Override
	public Integer getTo() {
		return this.tos.get(0);
	}
	
	@Override
	public boolean checkLink() {
		try {
			synchronized (writer) {
				writer.write("\n");
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}