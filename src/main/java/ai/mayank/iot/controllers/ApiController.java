package ai.mayank.iot.controllers;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.mayank.iot.Sockets.ClientHandler;
import ai.mayank.iot.Sockets.Server;
import ai.mayank.iot.repos.UserRepo;
import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SupportedDevices;
import ai.mayank.iot.tables.User;
import ai.mayank.iot.tables.SupportedDevices.DeviceType;
import ai.mayank.iot.utils.inter_exchange.InterMessageProtocol;

@Controller
@RequestMapping("/api")
public class ApiController {
	@Autowired
	UserRepo userrepo;
	Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/version")
	public ResponseEntity<String> version(){
		return new ResponseEntity<String>("v1.0",HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<User>> listUsers(){
		return new ResponseEntity<List<User>>(userrepo.findAll(),HttpStatus.OK);
	}
	
	@GetMapping("/create/user")
	public ResponseEntity<User> createUser() {
		User u = new User("Anonymous","anonymous@domain.com","");
		u.setUuid("5a5a83c3-2588-42fb-84bd-fa3129a2ac45");
		u.setValid(true);
		
		HashSet<Device> devices = new HashSet<Device>();
		devices.add(new Device(u, "t1", 1234,new SupportedDevices(DeviceType.Raspberry_PI)));
		devices.add(new Device(u, "t2", 789,new SupportedDevices(DeviceType.Raspberry_PI)));
		
		u.setPassword(passwordEncoder.encode("12345"));
		u.setDevices(devices);
		userrepo.save(u);
		return new ResponseEntity<User>(u,HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*")
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
