package ai.mayank.iot.tables;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SocketVariables {
	@Id
	String id;	
	Device device;
	@Indexed(unique = true)
	String tempId;	
	String token;
	Integer deviceId;
	Integer to;	
	private Date createDate;
	
	public SocketVariables() {
		createDate = new Date();
		token = UUID.randomUUID().toString();
	}
	
	public SocketVariables(String token,String tempId,Integer device, Integer to) {
		this.token = token;
		this.tempId = tempId;
		this.deviceId = device;
		this.to = to;
		createDate = new Date();
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Integer getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}


	public Integer getTo() {
		return to;
	}


	public void setTo(Integer to) {
		this.to = to;
	}


	public Device getDevice() {
		return device;
	}


	public void setDevice(Device device) {
		this.device = device;
	}
	
	
}
