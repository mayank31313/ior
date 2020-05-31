package ai.mayank.iot.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.mayank.iot.tables.SupportedDevices;

@Service
public class SupportedDeviceService {
	@Autowired
	SessionFactory sessionFactory;
	
	@Transactional
	public void addDevice(SupportedDevices device) {
		Session session = sessionFactory.getCurrentSession();
		session.save(device);
	}
	
	@Transactional(readOnly = true)
	public List<SupportedDevices> getDevices(){		
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(SupportedDevices.class).list();		
	}
	
	@Transactional(readOnly = true)
	public SupportedDevices getDevice(int id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(SupportedDevices.class, id);
	}
}
