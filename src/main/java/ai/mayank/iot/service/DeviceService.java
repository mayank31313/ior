package ai.mayank.iot.service;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.*;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

@Service
public class DeviceService {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Transactional(readOnly = true)
    public Device getDevice(String token,Integer code){
        Session session = sessionFactory.getCurrentSession();
        Long userId = getUserId(token);
        System.out.println(token);
        if(token == null)
        	return null;
        Criteria criteria = session.createCriteria(Device.class);
        HashMap<String,Object> map = new HashMap<>();
        map.put("user",userId);
        map.put("deviceId", code);
        criteria.add(Restrictions.allEq(map));
        List<Device> devices = criteria.list();
        if(devices.isEmpty())
        	return null;
        return devices.get(0);
    }
	
	@Transactional(readOnly = true)
	public List<Device> getDevices(String token){
		Session session = sessionFactory.getCurrentSession();
		Long userId = getUserId(token);
		if(token == null)
			return null;
        Criteria criteria = session.createCriteria(Device.class);
        HashMap<String,Object> map = new HashMap<>();
        map.put("user",userId);
        criteria.add(Restrictions.allEq(map));
        criteria.addOrder(Order.desc("id"));
        List<Device> devices = criteria.list();
        return devices;
	}
	private Long getUserId(String token) {
		Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        HashMap<String,Object> map = new HashMap<>();
        map.put("uuid",token);
        criteria.add(Restrictions.allEq(map));
        List<User> users = criteria.list();
        if(users.isEmpty())
        	return null;
        return users.get(0).getId();
	}
	@Transactional
    public void save(Device device){
        Session session = sessionFactory.getCurrentSession();
        session.update(device);
    }
	@Transactional(readOnly = true)
    public boolean getState(String token,Integer code){
        return getDevice(token,code).getState();
    }
    @Transactional
    public void setState(String token,Integer code,boolean state){
        Device d = getDevice(token,code);
        if(d != null) {
        	d.setState(state);
        	save(d);
        }
    }
    @Transactional(readOnly = true)
    public boolean isSubDevice(String userToken,String deviceId) {
    	Session session = sessionFactory.getCurrentSession();
    	Long userid = getUserId(userToken);
    	if(userToken == null)
    		return false;
    	Criteria criteria = session.createCriteria(Device.class);
    	HashMap<String, String> map = new HashMap<>();
    	criteria.add(Restrictions.allEq(map));
    	return !criteria.list().isEmpty();
    }
}
