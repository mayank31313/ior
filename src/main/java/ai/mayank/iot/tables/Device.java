package ai.mayank.iot.tables;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Device {
	@Id
	private String id;
		
	private Integer deviceId;
	private String user;
	private String deviceName;	
	private SupportedDevices deviceType;
	private boolean state;
	private String pod;
	
	
	public String getPod() {
		return pod;
	}
	public void setPod(String pod) {
		this.pod = pod;
	}
	public boolean getState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	
	private Set<DeviceElement> elements = new HashSet<>();
	
	public Device() {
		
	}
	public Device(User user,String deviceName,Integer code,SupportedDevices deviceType) {
		this.user = user.getId();
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.deviceId = code;
	}
	public Set<DeviceElement> getElements(){
		return elements;
	}
	public void setElements(HashSet<DeviceElement> elem) {
		this.elements = elem;
	}
	public String getId() {
		return id;
	}
	
	public String getUser() {
		return user;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public SupportedDevices getDeviceType() {
		return deviceType;
	}
	
	public void setId(String id) {
		this.id= id;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setDeviceName(String name) {
		deviceName = name;
	}
	public void setDeviceType(SupportedDevices type) {
		deviceType = type;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	
}
