package ai.mayank.iot.service;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.mayank.iot.tables.Device;
import ai.mayank.iot.tables.SocketVariables;

@Service
public class SocketHandlerService {

	@Autowired
	SessionFactory sessionFactory;
	
	@Transactional
	public void addHandler(SocketVariables vars) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(vars);
	}
	
	@Transactional
	public SocketVariables getVars(String temp_id) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SocketVariables.class);
		HashMap<String,Object> map = new HashMap<>();
		map.put("tempId", temp_id);
		criteria.add(Restrictions.allEq(map));
		List<SocketVariables> vars = criteria.list();
		if(vars.size() > 0) {
			return vars.get(0);
		}
		else
			return null;
	}

	@Transactional
	public SocketVariables getVars(String token,Device device) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(SocketVariables.class);
		HashMap<String,Object> map = new HashMap<>();
		map.put("token", token);
		map.put("device",device);
		criteria.add(Restrictions.allEq(map));
		criteria.addOrder(Order.desc("device"));
		return (SocketVariables) criteria.list().get(0);
	}
}
