package ai.mayank.iot.tables;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SupportedDevices {
	@Id
	private String id;
	private Integer device_id;
	private String deviceType;

	public SupportedDevices() {
		
	}
	
	public SupportedDevices(DeviceType type) {
		this.device_id = type.id;
		this.deviceType = type.name();
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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
