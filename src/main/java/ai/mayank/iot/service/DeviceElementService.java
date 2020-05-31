package ai.mayank.iot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.DeviceElement;

@Service
public class DeviceElementService {
	public String addDeviceElement(DeviceElement device) {
		return "";
	}
	
	public List<DeviceElement> getPortMapping(String deviceId){
		return new ArrayList<DeviceElement>();
	}
}
