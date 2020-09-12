package ai.mayank.iot.controllers;


import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.mayank.iot.config.zookeeper.StringTemplatesFormats;
import ai.mayank.iot.control.NotInitializedException;
import ai.mayank.iot.control.ZookeeperExecutor;
import ai.mayank.iot.proxy.DevicoZookeeperInfo;
import ai.mayank.iot.repos.SocketHandlerRepository;
import ai.mayank.iot.repos.UserRepo;
import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SocketVariables;

@RestController
public class Controller {
	@Autowired
	UserRepo userrepo;
		

	@Autowired
	SocketHandlerRepository socketService;
	
	@Autowired
	ZookeeperExecutor executor;
	
	Logger log = Logger.getLogger(Controller.class.getName());

	@GetMapping("/home")
	public ResponseEntity<String> home() {
		return new ResponseEntity<String>("Running Successfully", HttpStatus.OK);
	}
	
	@PostMapping("/subscribe")
	public ResponseEntity<String> subscribe(@RequestParam String uuid,@RequestParam Integer from,@RequestParam Integer to) {
		userrepo.findByUuid(uuid).get().getDevices().forEach(x->System.out.println(x.getDeviceId()));
		Optional<Device> deviceOptional = userrepo.findByUuid(uuid).get().getDevices().stream().filter(x->x.getDeviceId().equals(from)).findFirst();
		if(!deviceOptional.isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
 
        Device device = deviceOptional.get();      
        
        try {
			Stat stag = executor.getKeeper().exists("/ior/" + String.format(StringTemplatesFormats.CLIENT_TEMPLATE, uuid, from), false);
			if(stag == null) {
				executor.createZNode(String.format(StringTemplatesFormats.CLIENT_TEMPLATE, uuid, from), "");
			}
			else {
				DevicoZookeeperInfo info = DevicoZookeeperInfo.castString(executor.getLatestData(String.format(StringTemplatesFormats.CLIENT_TEMPLATE,uuid, from)));				
				if(info.state) {					
					Stat s = executor.getKeeper().exists(String.format("/ior/server/%s",info.podIp),false);	
					if(s != null)
						return new ResponseEntity<String>(HttpStatus.CONFLICT);
				}
			}
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotInitializedException e) {}
        
		
        String token = UUID.randomUUID().toString();
        SocketVariables var = new SocketVariables(uuid, token, device.getDeviceId(),to);
        var.setDevice(device);
        socketService.save(var);	
		log.info("Device Registered: " + device.getUser());
		return new ResponseEntity<String>(token+"\n",HttpStatus.CREATED);
	}
}
