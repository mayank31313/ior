package ai.mayank.iot.controllers;


import java.util.UUID;
import java.util.logging.Logger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ai.mayank.iot.config.zookeeper.StringTemplatesFormats;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.service.DeviceService;
import ai.mayank.iot.service.EndpointHitsService;
import ai.mayank.iot.service.SocketHandlerService;
import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SocketVariables;

@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	DeviceService deviceService;
	
	@Autowired
	EndpointHitsService endpointService;

	@Autowired
	SocketHandlerService socketService;
	
	@Autowired
	ZookeeperExecutor executor;
	
	Logger log = Logger.getLogger(Controller.class.getName());

	@GetMapping("/home")
	public ResponseEntity<String> home() {
		return new ResponseEntity<String>("Running Successfully", HttpStatus.OK);
	}
	
	@PostMapping("/subscribe/{tok}/{code}/{to}")
	public ResponseEntity<String> subscribe(@PathVariable String tok,@PathVariable Integer code,@PathVariable Integer to) {		
		Device device = deviceService.getDevice(tok,code);
		if(device == null)
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
 
        
        /*
		EndpointHits hit = endpointService.get(device.getUser());
		if(hit == null) {
			hit = new EndpointHits();
			hit.setUser(device.getUser());
		}
		
		hit.setHits(hit.getHits() + 1);
		endpointService.update(hit);
		      */  
        //Server.add(uuid, client);
        //handlers.put(code, client);        
        /*
        try {
			Stat stag = executor.keeper.exists("/ior/" + String.format(StringTemplatesFormats.CLIENT_TEMPLATE, tok,code), false);
			if(stag == null) {
				executor.createZNode(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, tok,code), "");
			}
			else {
				DevicoZookeeperInfo info = DevicoZookeeperInfo.castString(executor.getLatestData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE,tok,code)));				
				if(info.state) {					
					Stat s = executor.keeper.exists(String.format("/ior/server/%s",info.podIp),false);	
					if(s != null)
						return new ResponseEntity<String>(HttpStatus.CONFLICT);
				}
			}
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
        */
        String uuid = UUID.randomUUID().toString();
        SocketVariables var = new SocketVariables(tok, uuid, device.getDeviceId(),to);
        var.setDevice(device);
        
        socketService.addHandler(var);	
		log.info("Device Registered: " + device.getUser());
		return new ResponseEntity<String>(uuid+"\n",HttpStatus.CREATED);
	}
}
