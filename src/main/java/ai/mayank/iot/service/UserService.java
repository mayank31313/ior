package ai.mayank.iot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.DeviceElement;
import ai.mayank.iot.tables.User;

@Service
public class UserService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public String addUser(User user){
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(user);
	    return user.getUuid();
	}
	
	@Transactional
	public User get(Long id) {
		Session session = sessionFactory.getCurrentSession();
		return session.get(User.class,id);
	}
	@Transactional
	public void update(User user) {
		Session session = sessionFactory.getCurrentSession();
		session.update(user);
	}
	
	@Transactional(readOnly = true)
	public User getId(String email,String pass) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("email", email);
		//map.put("password",pass);
		
		criteria.add(Restrictions.allEq(map));
		List<User> users = criteria.list();
		if(users.isEmpty())
			return null;
		User user = users.get(0);
		
		return user.checkPassword(pass) ? user : null;
	}
	
	@Transactional(readOnly = true)
	public User getUser(String token) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("uuid", token);
		criteria.add(Restrictions.allEq(map));
		List<User> users = criteria.list();
		if(users.isEmpty())
			return null;
		User user = users.get(0);
		return user;
	}
	
	@Transactional(readOnly = true)
	public Device getDevice(Long deviceId) {
		Session session = sessionFactory.getCurrentSession();
		Device device = session.get(Device.class, deviceId);
		return device;
	}
	@Transactional
	public boolean deleteDevice(User user,Long deviceId) {
		Session session = sessionFactory.getCurrentSession();
		Device device = session.get(Device.class, deviceId);
		session.delete(device);
		return true;
	}
	
	@Transactional
	public boolean addDeviceElement(DeviceElement element) {
		Session session = sessionFactory.getCurrentSession();
		session.save(element);
		return true;
	}
	
	@Transactional
	public boolean deleteElement(Long elementId) {
		Session session = sessionFactory.getCurrentSession();
		DeviceElement element = session.get(DeviceElement.class, elementId);
		session.delete(element);
		return true;
	}
	
	@Transactional(readOnly = true)
	public User getValidUser(String otp,String email) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("email", email);
		map.put("otp",otp);
		criteria.add(Restrictions.allEq(map));
		List<User> users = criteria.list();
		if(users.isEmpty())
			return null;
		
		return users.get(0);
	}
}
