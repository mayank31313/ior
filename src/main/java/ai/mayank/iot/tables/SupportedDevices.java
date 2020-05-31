package ai.mayank.iot.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="supported_devices")
public class SupportedDevices {
	@Id
	@Column(name = "id",unique = true,nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "deviceType")
	private String deviceType;

	public SupportedDevices() {
		
	}
	
	public SupportedDevices(DeviceType type) {
		this.id = type.id;
		this.deviceType = type.name();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getDeviceName() {
		return deviceType;
	}

	public void setDeviceName(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public static enum DeviceType{
		ARDUINO(1),
		NodeMCU(2),
		EV3(3),
		Raspberry_PI(4),
		Tunnel_Device_R(5),
		Tunnel_Device_T(6);
		
		private int id;
		private DeviceType(int i) {
			this.id = i;
		}
	}
}
