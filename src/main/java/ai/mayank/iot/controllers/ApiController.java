package ai.mayank.iot.controllers;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ai.mayank.iot.Sockets.ClientHandler;
import ai.mayank.iot.Sockets.IClientHandler;
import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.service.UserService;
import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SupportedDevices;
import ai.mayank.iot.tables.User;
import ai.mayank.iot.tables.SupportedDevices.DeviceType;
import ai.mayank.iot.utils.inter_exchange.InterMessageProtocol;

@Controller
@RequestMapping("/api")
public class ApiController {
	@Autowired
	UserService userService;
	Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@GetMapping("/version")
	public ResponseEntity<String> version(){
		return new ResponseEntity<String>("v1.0",HttpStatus.OK);
	}
	
	@GetMapping("/create/user")
	public ResponseEntity<User> createUser() {
		System.out.println("HEy There");
		User u = new User("Mayank","12345","jacob8547black@gmail.com","1234567890");
		u.setUuid("5a5a83c3-2588-42fb-84bd-fa3129a2ac45");
		u.setValid(true);
		
		HashSet<Device> devices = new HashSet<Device>();
		devices.add(new Device(u, "t1", 1234,new SupportedDevices(DeviceType.Raspberry_PI)));
		devices.add(new Device(u, "t2", 789,new SupportedDevices(DeviceType.Raspberry_PI)));
		
		u.setDevices(devices);
		u.setPassword("12345");
		userService.addUser(u);
		return new ResponseEntity<User>(u,HttpStatus.OK);
	}
	
	@PostMapping("/send")
	public ResponseEntity<InterMessageProtocol> sendMessage(@RequestBody InterMessageProtocol protocol){
		logger.info(protocol.user);
		logger.info(protocol.device);
		
		ClientHandler handler = (ClientHandler)Server.sockets.get(protocol.user).get(Integer.valueOf(protocol.device));
		InterMessageProtocol message = new InterMessageProtocol();
		message.user = "SERVER";
		handler.addMessage(protocol.message);
		message.type = "FORWARDED";
		return new ResponseEntity<InterMessageProtocol>(message,HttpStatus.OK);
	}
}
