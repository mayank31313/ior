package ai.mayank.iot.tables;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "deviceElement")
public class DeviceElement {

	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "State")
	private boolean state;
	
	@Column(name = "Port")
	private String port;
	
	@Column
	private boolean isInput;
	
	@Column(name = "DeviceID")
	private Long deviceId;
	
	
	public DeviceElement() {
		
	}
	public DeviceElement(boolean on,String port,Long deviceId) {
		this.state = on;
		this.port = port;
		this.deviceId = deviceId;
	}

	public void setState(boolean b) { state = b;}
	public boolean getState() { return state; }
	
	public Long getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(Long id) {
		this.deviceId = id;
	}
	public String getPort() {
		return this.port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public boolean isInput() {
		return isInput;
	}
	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}
	
}
