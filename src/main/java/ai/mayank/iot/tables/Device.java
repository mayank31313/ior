package ai.mayank.iot.tables;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "device")
@NamedQuery(name="getDevicesInfo",query="SELECT COUNT(*) from Device WHERE user=:user")
public class Device {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="deviceId")
	private Integer deviceId;
	
	@Column(name = "user_id")
	private Long user;
	
	@Column(name = "DeviceName")
	private String deviceName;
	
	@OneToOne(mappedBy = "")
	@JoinColumn(name = "DeviceType")
	private SupportedDevices deviceType;
	
	@Column(name = "Current_State")
	private boolean state;
	
	@Column(name = "devicePodIP")
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
	
	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name = "DeviceID")
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
	public Long getId() {
		return id;
	}
	
	public Long getUser() {
		return user;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public SupportedDevices getDeviceType() {
		return deviceType;
	}
	
	public void setId(Long id) {
		this.id= id;
	}
	public void setUser(Long user) {
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
